grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.groupId = "au.org.ala"


//forkConfig = [maxMemory: 1024, minMemory: 64, debug: false, maxPerm: 256]
//grails.project.fork = [
//        test: forkConfig, // configure settings for the test-app JVM
//        run: forkConfig, // configure settings for the run-app JVM
//        war: forkConfig, // configure settings for the run-war JVM
//        console: forkConfig // configure settings for the Swing console JVM
//]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {

    }
    log "debug" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
//    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        mavenLocal()
        mavenRepo ("http://nexus.ala.org.au/content/groups/public/") {
            updatePolicy 'always'
        }
    }

    dependencies {
        runtime 'postgresql:postgresql:9.1-901.jdbc4'
        runtime 'org.codehaus.groovy.modules.http-builder:http-builder:0.7.2'
        runtime 'org.apache.httpcomponents:httpclient:4.3.5'
        runtime 'org.apache.httpcomponents:httpmime:4.3.5'
        runtime 'au.org.ala:ala-name-matching:2.1'
        compile 'org.apache.commons:commons-math3:3.5'
    }

    plugins {
        build ":release:3.0.1"
        compile ':cache:1.1.1'
        // plugins needed at runtime but not for compilation
        runtime ":hibernate:3.6.10.7" // or ":hibernate4:4.3.4"
        runtime ":database-migration:1.3.8"
        runtime ":jquery:1.11.1"
        runtime ":jquery-ui:1.10.4"
        runtime ":csv:0.3.1"
        runtime ":resources:1.2.1"

        runtime ":ala-bootstrap2:2.2"
        runtime ":ala-auth:1.2"
        // plugins for the build system only
        build ":tomcat:7.0.50"
    }
}
