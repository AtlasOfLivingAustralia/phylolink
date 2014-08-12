modules = {
    core {
        dependsOn 'jquery'
        resource url: "${grailsApplication.config.ala.baseURL?:'http://www.ala.org.au'}/wp-content/themes/ala2011/images/favicon.ico", attrs:[type:'ico'], disposition: 'head'
//        resource url: "${grailsApplication.config.ala.baseURL?:'http://www.ala.org.au'}/wp-content/themes/ala2011/css/jquery.autocomplete.css"
//        resource url: "${grailsApplication.config.ala.baseURL?:'http://www.ala.org.au'}/wp-content/themes/ala2011/scripts/jquery.autocomplete.js", disposition: 'head'
//        resource url: "${grailsApplication.config.ala.baseURL?:'http://www.ala.org.au'}/wp-content/themes/ala2011/scripts/uservoice.js", disposition: 'head'
        resource url: [dir:'js', file:'html5.js', plugin: "ala-web-theme"], wrapper: { s -> "<!--[if lt IE 9]>$s<![endif]-->" }, disposition: 'head'
    }

    bootstrap {
        dependsOn 'core'
        resource url:[dir:'js', file:'bootstrap.js', plugin: 'ala-web-theme', disposition: 'head']
        resource url:[dir:'css', file:'bootstrap.css', plugin: 'ala-web-theme'], attrs:[media:'screen, projection, print']
        resource url:[dir:'css', file:'bootstrap-responsive.css', plugin: 'ala-web-theme'], attrs:[media:'screen', id:'responsiveCss']
    }

    application {
        resource url:[dir:'css', file:  "main.css"]
        resource url:[dir:'css', file:  "jquery-ui.css"]
        resource url: [ dir: "js" , file:"jquery-ui.min.js"]
        resource url:[dir:"js", file:'application.js']
        resource url:[dir:"js", file:'utils.js']
    }
}