package com.pathunt

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured

@Secured('permitAll')
class HomeController {
    def springSecurityService
    def homeService
    def queryGeneratorService
    def sqlQuery = "SELECT concat(p.country,p.number) as 'Publication Number', " +
                   "p.title, p.abstract, p.first_claim, year(p.date), p.date, " +
                   "concat(ir.name_first, ' ', ir.name_last) as 'Inventor(s), " +
                   "concat(a.organization,a.name_first, ' ', a.name_last) as 'Assignee(s)," +
                   "u.subclass_id as 'All_UPC'" + "concat(i.section, i.ipc_class, " +
                   "i.subclass, i.main_group, '/', i.subgroup as 'All_IPC'" +
                   "p.citation3 as 'cited_by_within_3_years' FROM patent p " +
                   "INNER JOIN inventor ir ON p.country = ir.country AND p.id = ir.patent_id " +
                   "INNER JOIN assignee a ON p.country = a.country AND p.id = a.patent_id " +
                   "INNER JOIN uspc u ON p.country = u.country AND p.id = u.patent_id " +
                   "INNER JOIN ipcr i ON p.country = i.country AND p.id = i.patent_id " +
                   "WHERE "
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
        sqlQuery += queryGeneratorService.parseQuery()
        println sqlQuery
        redirect(action: 'index')
    }
}
