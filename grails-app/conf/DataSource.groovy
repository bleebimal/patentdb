dataSource {
    pooled = true
    driverClassName = "com.mysql.jdbc.Driver"
    username = "root"
    password = "iam@Dwit1"
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
//    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
}

// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:mysql://localhost/pathuntcurrent"
//            url = "jdbc:mysql://localhost/pathuntcurrent"
        }
    }
    test {
        dataSource {
            dbCreate = ""
            url = "jdbc:mysql://localhost/pathuntcurrent"
        }
    }
    production {
        dataSource {
            dbCreate = ""
            url = "jdbc:mysql://localhost/pathuntcurrent"
            properties {
               maxActive = -1
               minEvictableIdleTimeMillis=1800000
               timeBetweenEvictionRunsMillis=1800000
               numTestsPerEvictionRun=3
               testOnBorrow=true
               testWhileIdle=true
               testOnReturn=false
               validationQuery="SELECT 1"
               jdbcInterceptors="ConnectionState"
            }
        }
    }
}
