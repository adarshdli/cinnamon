// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

grails.config.locations = ["classpath:${appName}-config.properties",
        "classpath:${appName}-config.groovy",
        "file:${userHome}/.grails/${appName}-config.properties",
        "file:${userHome}/.grails/${appName}-config.groovy",
        "file:${System.env.CINNAMON_HOME_DIR}/${appName}-config.groovy"
]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }


grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
        xml: ['text/xml', 'application/xml'],
        text: 'text/plain',
        js: 'text/javascript',
        rss: 'application/rss+xml',
        atom: 'application/atom+xml',
        css: 'text/css',
        csv: 'text/csv',
        all: '*/*',
        json: ['application/json', 'text/json'],
        form: 'application/x-www-form-urlencoded',
        multipartForm: 'multipart/form-data'
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// The default codec used to encode data with ${}
grails.views.default.codec = "html" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart = false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// enable query caching by default
grails.hibernate.cache.queries = false

grails.gorm.default.mapping = {
    cache false
}

grails.gorm.autoFlush = true
grails.gorm.failOnError = true

// set per-environment serverURL stem for creating absolute links
environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    appenders {
        'null' name: 'stacktrace'
        console name: 'stdout', layout: pattern(conversionPattern: '%d{ISO8601} %t %c %m%n')
    }

    error 'net.sf.ehcache.hibernate',
            'org.apache.tomcat.util',
            'org.apache.coyote',
            'org.apache.commons.beanutils',
            'org.springframework',
            'org.hibernate',
            'org.apache.naming.SelectorContext',
            'net.sf.ehcache',
            'org.apache.catalina'

    error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
            'org.codehaus.groovy.grails.web.pages', //  GSP
            'org.codehaus.groovy.grails.web.sitemesh', //  layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping', // URL mapping
            'org.codehaus.groovy.grails.commons', // core / classloading
            'org.codehaus.groovy.grails.plugins', // plugins
            'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
            'grails.app.taglib.org.grails.plugin.resource',
            'org.grails.plugin.resource',
            'org.codehaus.groovy.grails.io'

    debug 'org.codehaus.groovy.grails.context',
            'org.codehaus.groovy.grails.io',
            'org.codehaus.groovy.grails.web',
            'cinnamon.global.ConfThreadLocal'

    info 'grails.plugins.twitterbootstrap.BootstrapResources'
    info 'cinnamon.RequestTicketAuthenticationFilter'
    info 'cinnamon.debug.ProviderManager'
    info 'humulus.RepositoryLoginFilter'
    info 'cinnamon.CinnamonUserDetailsService'
    info 'grails.app.filters.cinnamon.PageFilters'
    warn 'grails.app.filters.TriggerFilters'
    debug 'cinnamon.data'
    debug 'cinnamon.OsdController'
    info 'cinnamon.index.LuceneActor'
    info 'cinnamon.index.LuceneMaster'
    info 'cinnamon.index.LuceneService'
    info 'cinnamon.workflow.TransitionActor'
    info 'cinnamon.workflow'
    info 'humulus',
            'cinnamon.UserAccountController',
            'cinnamon.TriggerFilters',
            'cinnamon.index.indexer.ParentFolderPathIndexer',
            'org.springframework.security.authentication',
            'cinnamon.PreAuthenticatedAuthenticationProvider'

    info 'grails.app.domain.cinnamon.index'

    root {
        debug 'stdout'
    }
}

// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName = 'cinnamon.UserAccount'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'cinnamon.CmnGroupUser'
grails.plugins.springsecurity.authority.className = 'cinnamon.CmnGroup'
grails.plugins.springsecurity.authority.nameField = 'name'
grails.plugins.springsecurity.userLookup.usernamePropertyName = 'name'
grails.plugins.springsecurity.userLookup.passwordPropertyName = 'pwd'
grails.plugins.springsecurity.userLookup.enabledPropertyName = 'activated'
grails.plugins.springsecurity.userLookup.authoritiesPropertyName = 'groupUsers'
grails.plugins.springsecurity.successHandler.defaultTargetUrl = '/folder/index'
grails.plugins.springsecurity.auth.loginFormUrl = '/login/auth'
grails.plugins.springsecurity.providerNames = ['preauthAuthProvider', 'daoAuthenticationProvider', 'anonymousAuthenticationProvider']
grails.plugins.springsecurity.logout.afterLogoutUrl = '/login/auth'

grails.logging.jul.usebridge = false

/*
 The default page is responsible for connecting to the right database,
 so we always redirect the user there:
*/
grails.plugins.springsecurity.successHandler.alwaysUseDefault = false
grails.plugins.springsecurity.http.useExpressions = false

