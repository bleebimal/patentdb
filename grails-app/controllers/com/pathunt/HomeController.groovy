package com.pathunt

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured

@Secured('permitAll')
class HomeController {
    def springSecurityService
    def homeService
    def sqlQuery = ""
    def queryGeneratorService
    def index() {
        if(springSecurityService.isLoggedIn()) {
            if(SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')) {
                def currentUser = springSecurityService.getCurrentUser()
                render view: 'index', model: [currentUser:currentUser, sqlQuery:sqlQuery]
                sqlQuery = ""
            }
        }
        else {
            redirect(controller: 'login', action: 'auth')
        }
    }

    def parser(){
//        homeService.parseQuery(params.query)
//        println "parser"
        String prefix = homeService.prefixConverter(params.query)
        queryGeneratorService.setPrefixQuery(prefix)
        sqlQuery += "SELECT * FROM patent WHERE " + queryGeneratorService.parseQuery()
        println sqlQuery
        redirect(action: 'index')
    }
}
