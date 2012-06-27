<!DOCTYPE HTML>
<html>
<head>
    <meta name="layout" content="main"/>

    <title><g:message code="folder.index"/></title>

</head>

<body class="body">

<div class="nav">
    <span class="menuButton"><a class="home" href="${resource(dir: 'folder', file: 'index')}"><g:message
            code="home"/></a></span>
    <sec:ifAllGranted roles="_superusers">
    <span class="menuButton"><a class="admin" href="${createLink(controller: 'admin', action: 'index')}"><g:message
    code="link.to.administration"/></a></span>
    </sec:ifAllGranted>
</div>

<div class="content">
    <div id="message"><g:if test="${flash.message}"><g:message code="${flash.message}"/>
        <script type="text/javascript">
            showClearButton();
        </script>
    </g:if>
        <g:if test="${msgList}">
            <div id="msgList">
                <br>
                <ul>
                    <g:each in="${msgList}" var="msg">
                        <li>${msg}</li>
                    </g:each>
                </ul>

            </div>
        </g:if>

    </div>

    <div id="folderTree" class="folder_tree">
        <g:render template="/folder/subFolders"
                  model="[children: children, grandChildren: grandChildren,
                          contentSet: contentSet, triggerSet: triggerSet, folderConfigs:folderConfigs]"/>
    </div>
       
   
    <div id="searchResults" class="search_results"></div>

    <div id="folderContent" class="folder_content"></div>

    <div id="objectSelection" class="object_selection">
        <g:render template="selectionForm"/>
    </div>

    <div id="folderMeta" class="folder_meta"></div>

    <div id="objectDetails" class="object_details"></div>

</div>

</body></html>
