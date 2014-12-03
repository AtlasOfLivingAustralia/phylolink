modules = {

    core {
        dependsOn 'jquery'
        resource url: "${grailsApplication.config.ala.baseURL ?: 'http://www.ala.org.au'}/wp-content/themes/ala2011/images/favicon.ico", attrs: [type: 'ico'], disposition: 'head'
        resource url: [dir: 'js', file: 'html5.js', plugin: "ala-web-theme"], wrapper: { s -> "<!--[if lt IE 9]>$s<![endif]-->" }, disposition: 'head'
    }

    bootstrap {
        dependsOn 'core'
        resource url: [dir: 'js', file: 'bootstrap.js', plugin: 'ala-web-theme', disposition: 'head']
        resource url: [dir: 'css', file: 'bootstrap.css', plugin: 'ala-web-theme'], attrs: [media: 'screen, projection, print']
        resource url: [dir: 'css', file: 'bootstrap-responsive.css', plugin: 'ala-web-theme'], attrs: [media: 'screen', id: 'responsiveCss']
    }

    knockout {
        resource url: 'js/knockout-2.3.0.js',disposition: 'head'
    }

    application {
        resource url: [dir: 'css', file: "main.css"]
        resource url: [dir: 'css', file: "jquery-ui.css"]
        resource url: [dir: "js", file: "jquery-ui.min.js"]
    }

    appSpecific {
        resource url: [dir: "js", file: 'application.js'], disposition: 'head'
        resource url: [dir: "js", file: 'utils.js']
    }

    form {
        resource url: [dir: "js", file: 'knockout-2.3.0.js'],disposition: 'head'
    }

    jqxCore {
        dependsOn( 'jquery' )
        resource url: [dir: "/jqwidgets/styles", file: "jqx.base.css"],disposition: 'head'
        resource url: [dir: '/jqwidgets', file: 'jqxcore.js'], disposition: 'head'

    }

    jqxGrid {
        dependsOn('jqxCore')
        resource url: [dir: '/jqwidgets', file: 'jqxdata.js' ], disposition: 'head'
        resource url: [dir: '/jqwidgets', file: 'jqxbuttons.js'], disposition: 'head'
        resource url: [dir: '/jqwidgets', file: 'jqxscrollbar.js'], disposition: 'head'
        resource url: [dir: '/jqwidgets', file: 'jqxmenu.js'], disposition: 'head'
        resource url: [dir: '/jqwidgets', file: 'jqxlistbox.js'], disposition: 'head'
        resource url: [dir: '/jqwidgets', file: 'jqxdropdownlist.js'], disposition: 'head'
        resource url: [dir: '/jqwidgets', file: 'jqxgrid.js'], disposition: 'head'
        resource url: [dir: '/jqwidgets', file: 'jqxgrid.selection.js'], disposition: 'head'
        resource url: [dir: '/jqwidgets', file: 'jqxgrid.columnsresize.js'], disposition: 'head'
        resource url: [dir: '/jqwidgets', file: 'jqxgrid.filter.js'], disposition: 'head'
        resource url: [dir: '/jqwidgets', file: 'jqxgrid.sort.js'], disposition: 'head'
        resource url: [dir: '/jqwidgets', file: 'jqxgrid.pager.js'], disposition: 'head'
        resource url: [dir: '/jqwidgets', file: 'jqxgrid.grouping.js'], disposition: 'head'
        resource url: [dir: '/jqwidgets', file: 'jqxgrid.edit.js'], disposition: 'head'
    }

    slickgrid {
        dependsOn( 'jquery', "jquery-ui" )
        resource url: 'slickgrid/jquery.event.drag-2.2.js', disposition: 'head'
        resource url: 'slickgrid/slick.core.js', disposition: 'head'
        resource url: 'slickgrid/slick.grid.js', disposition: 'head'
        resource url: 'slickgrid/slick.dataview.js', disposition: 'head'
        resource url: 'slickgrid/slick.formatters.js', disposition: 'head'
        resource url: 'slickgrid/slick.editors.js', disposition: 'head'
        resource url: 'slickgrid/slickgrid.bvp.js', disposition: 'head'
        resource url: 'slickgrid/slick.grid.css', disposition: 'head'
    }
}