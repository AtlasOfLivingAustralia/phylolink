dataSource {
    pooled = true
    jmxExport = true
    driverClassName ="org.postgresql.Driver"
    username = "phylo"
    password = "h#1%&4"
}

hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
//    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
}

// environment specific settings
environments {
    development {
    }
    test {
       dataSource {
            dialect = "org.hibernate.dialect.H2Dialect"
            dbCreate = "create-drop"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;MODE=MYSQL;DB_CLOSE_ON_EXIT=FALSE;"
        }
    }
    production {
    }
}
