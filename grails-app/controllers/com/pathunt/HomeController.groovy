package com.pathunt

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured

@Secured('permitAll')
class HomeController {
    def springSecurityService
    def homeService
    def index() {
        if(springSecurityService.isLoggedIn()) {
            if(SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')) {
                def currentUser = springSecurityService.getCurrentUser()
                render view: 'index', model: [currentUser:currentUser]
            }
        }
        else {
            redirect(controller: 'login', action: 'auth')
        }
    }

    def parser(){
//        homeService.parseQuery(params.query)
        println "parser"
        homeService.prefixConverter(params.query)

    }
}
