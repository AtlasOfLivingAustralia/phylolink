grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6

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
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
        mavenLocal()
        mavenRepo "http://nexus.ala.org.au/content/groups/public/"
        mavenRepo "http://maven.ala.org.au/repository/"
    }

    dependencies {
        compile 'org.elasticsearch:elasticsearch:1.4.0'
        runtime 'postgresql:postgresql:9.1-901.jdbc4'
    }

    plugins {
        // plugins needed at runtime but not for compilation
        runtime ":hibernate:3.6.10.7" // or ":hibernate4:4.3.4"
        runtime ":database-migration:1.3.8"
        runtime ":jquery:1.8.3"
        runtime ":jquery-ui:1.10.4"
        runtime ":csv:0.3.1"
        runtime ":resources:1.2.1"
        runtime ":ala-web-theme:0.8.3"
//        compile ":webflow:2.0.8.1"
        // plugins for the build system only
        build ":tomcat:7.0.50"

    }
}
