package cinnamon

import humulus.EnvironmentHolder

import cinnamon.exceptions.IgnorableException
import grails.plugins.springsecurity.Secured

@Secured(["isAuthenticated()"])
class FolderController {

    def folderService
    def userService

    protected Set<String> loadUserPermissions(Acl acl){
        Set<String> permissions
        try {
            log.debug("us: ${userService.user} acl: ${acl} repo: ${session.repositoryName}")
            permissions = userService.getUsersPermissions(userService.user, acl)
//            log.debug("found permissions: ${permissions.dump()}")
        } catch (RuntimeException ex) {
            log.debug("getUserPermissions failed",ex)
            render(status:503, text:message(code:'error.access.failed'))
            throw new IgnorableException("error.access.failed")
        }
        return permissions
    }
    
    def index() {
        try {
            Folder rootFolder = Folder.findRootFolder()
            if (!rootFolder) {
                def logoutMessage = message(code: "error.no.rootFolder")
                return redirect(controller: 'logout', action: 'info', params: [logoutMessage: logoutMessage])
            }
            Collection childFolders = fetchChildFolders(rootFolder)
            Map grandChildren = [:]
            Set<Folder> contentSet = new HashSet<Folder>()
            childFolders.each { child ->
                Collection<Folder> gc = fetchChildFolders(child)
                grandChildren.put(child, gc)

                if (folderService.hasContent(child)) {
                    contentSet.add(child)
                }

                def grandChildrenWithContent = fetchChildrenWithContent(child)
                contentSet.addAll(grandChildrenWithContent)
            }
            def triggerSet = folderService.createTriggerSet(params.folder, params.osd)
            session.triggerFolder = params.folder
            session.triggerOsd = params.osd

            return [rootFolder: rootFolder,
                    contentSet: contentSet,
                    grandChildren: grandChildren,
                    children: childFolders,
                    triggerSet: triggerSet,
                    triggerFolder: params.folder,
                    envId: EnvironmentHolder.getEnvironment()?.get('id'),
                    msgList: flash.msgList
            ]

        }
        catch (Exception e) {
            log.debug("failed to show index:",e)
            def logoutMessage = message(code: 'error.loading.folders', args: [e.getMessage()])
            return redirect(controller: 'logout', action: 'info', params: [logoutMessage: logoutMessage])
        }


    }

    // not in folderService because it needs access to session.
    protected Collection<Folder> fetchChildFolders(Folder folder) {
        Collection<Folder> folderList = Folder.findAll("from Folder as f where f.parent=:parent and f.id != :id",
                [parent: folder, id: folder.id])

        Validator validator = fetchValidator()
        return validator.filterUnbrowsableFolders(folderList)
    }

    protected Validator fetchValidator(){
        UserAccount user = userService.user
        return new Validator(user)
    }

    // not in folderService because it needs access to session.
    protected Collection<Folder> fetchChildrenWithContent(Folder folder) {
        Collection<Folder> folderList =
            Folder.findAll("from Folder as f where f.parent=:parent and f in (select p.parent from ObjectSystemData as p where p.parent.parent=:parent2)",
                    [parent: folder, parent2: folder])
        Validator validator = fetchValidator()
        return validator.filterUnbrowsableFolders(folderList)
    }

    def fetchFolder(){
        Folder folder = Folder.get(params.folder)
        if (!folder) {
            return render(status: 503, text: message(code: 'error.folder.not.found'))
        }
        
        if (!folderService.mayBrowseFolder(folder, userService.user)) {
            return render(status: 401, text: message(code: 'error.access.denied'))
        }
        
        def childFolders = fetchChildFolders(folder)
        def grandChildren = [:]

        def childrenWithContent = fetchChildrenWithContent(folder)
        Set<Folder> contentSet = new HashSet<Folder>()
        contentSet.addAll(childrenWithContent)

        childFolders.each {child ->
            def gc = fetchChildFolders(child)
            if (gc.isEmpty()) {
                log.debug("${child.name} has no subfolders.")
            }
            else {
                log.debug("${child.name} has subfolders.")
            }
            grandChildren.put(child, gc)

            def grandChildrenWithContent = fetchChildrenWithContent(child)
            contentSet.addAll(grandChildrenWithContent)

        }

        def triggerSet = null
        if (session.triggerFolder) {
            triggerSet = folderService.createTriggerSet(session.triggerFolder, session.triggerOsd)
        }

        return render(
                template: "/folder/subFolders",
                model: [folder: folder,
                        children: childFolders,
                        grandChildren: grandChildren,
                        contentSet: contentSet,
                        triggerSet: triggerSet,
                        triggerFolder: session.triggerFolder,
                ])
    }

    def fetchFolderContent () {
        def repositoryName = session.repositoryName
        Folder folder
        try {
            if (params.folder) {
                folder = Folder.get(params.folder) // called by OSD
            }
            else {
                folder = Folder.get(params.id) // called by remoteFunction
            }
            if (!folder) {
                throw new RuntimeException('error.folder.not.found')
            }
            UserAccount user = userService.user
            if (!folderService.mayBrowseFolder(folder, user)) {
                return render(status: 401, text: message(code: 'error.access.denied'))
            }

            log.debug("found folder. ${params.folder}: $folder")
            if (!params.versions?.trim()?.matches('^all|head|branch$')) {
                // log.debug("params.versions: ${params.versions}")
                params.versions = 'head'
            }
            log.debug("fetch OSDs")
            def osdList = folderService.getObjects(user, folder, repositoryName, params.versions)
            /*
            * if this folder contains the triggerOsd, we add it to the osdList even if it
            * is not of the default version (all/head/branch).
            */
            def triggerOsd = session.triggerOsd
            if (triggerOsd && folder.id.toString().equals(session.triggerFolder)) {
                def id = Long.parseLong(triggerOsd)
                if (!osdList.find {it.id.equals(id)}) {
                    osdList.add(ObjectSystemData.get(triggerOsd))
                    session.triggerOsd = null
                    session.triggerFolder = null
                }
            }

            Set<String> permissions
            try {
                permissions = loadUserPermissions(folder.acl)
            } catch (RuntimeException ex) {
                log.debug("getUserPermissions failed", ex)
                throw new RuntimeException('error.access.failed')
            }

            log.debug("superuserStatus: ${userService.isSuperuser(user)}")

            return render(template: "/folder/folderContent", model: [folder: folder,
                    osdList: osdList,
                    permissions: permissions,
                    folders: folderService.getFoldersInside(user, folder),
                    superuserStatus: userService.isSuperuser(user),
                    selectedVersion: params.versions,
                    versions: [all: 'folder.version.all', head: 'folder.version.head', branch: 'folder.version.branch']
            ])
        }
        catch (Exception e) {
            log.debug("fetchFolderContent failed",e)
            return render(status: 500, text: message(code: e.message))
        }
    }
    
    
    
}