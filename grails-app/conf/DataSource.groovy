hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
    singleSession = true // configure OSIV singleSession mode
}

// environment specific settings
environments {
    development {
    }
    test {
        dataSource {
            pooled = true
            jmxExport = true
            driverClassName = "org.h2.Driver"
            username = "sa"
            password = ""
            dialect = "org.hibernate.dialect.H2Dialect"
            dbCreate = "create-drop"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;MODE=MYSQL;DB_CLOSE_ON_EXIT=FALSE;"
        }
    }
    production {
    }
}
