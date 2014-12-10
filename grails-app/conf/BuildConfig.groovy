grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.groupId = "au.org.ala"


grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {

    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
//    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        mavenLocal()
        mavenRepo ("http://nexus.ala.org.au/content/groups/public/") {
            updatePolicy 'always'
        }
    }

    dependencies {
    }

    plugins {
        build ":release:3.0.1"

        // plugins for the compile step
//        compile ":scaffolding:2.0.2"
//        compile ':cache:1.1.1'

        // plugins needed at runtime but not for compilation
        runtime ":hibernate:3.6.10.7" // or ":hibernate4:4.3.4"
        runtime ":database-migration:1.3.8"
        runtime ":jquery:1.8.3"
        runtime ":csv:0.3.1"
        runtime ":resources:1.2.1"
        runtime ":ala-web-theme:0.8.2"
//        runtime: ":ala-cas-client:1.0-SNAPSHOT"
        // plugins for the build system only
        build ":tomcat:7.0.50"

    }
}
