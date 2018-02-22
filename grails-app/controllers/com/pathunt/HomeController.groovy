package com.pathunt

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured
import groovy.sql.Sql

@Secured('permitAll')
class HomeController {
    def springSecurityService
    def homeService
    def queryGeneratorService
    def dataSource
    def exportService
    def grailsApplication

    def input = ""
    def joinInventor = "INNER JOIN inventorfinal ir ON p.country = ir.patent_country AND p.id = ir.patent_id "
    def joinAssignee = "INNER JOIN assigneefinal a ON p.country = a.patent_country AND p.id = a.patent_id "
    def joinUPC = "INNER JOIN uspc u ON p.country = u.patent_country AND p.id = u.patent_id "
    def joinIPC = "INNER JOIN ipcr i ON p.country = i.patent_country AND p.id = i.patent_id "
    def joinCPC = "INNER JOIN cpc c ON p.country = c.patent_country AND p.id = c.patent_id "
    def joinApplication = "INNER JOIN application ap ON p.country = ap.patent_country AND p.id = ap.patent_id "
    def joinCitation = "INNER JOIN uspatentcitation ct ON p.country = ct.patent_country AND p.id = ct.patent_id "
    def joinLocation = "INNER JOIN location l ON p.country = l.patent_country AND p.id = l.patent_id "

//    Map<String,Query> userInput = new HashMap<>()
    //session.getAttribute(key) and session.setAttribute(key, value)
    ArrayList<Patent> lists = new ArrayList<Patent>()
    def index() {
        if(springSecurityService.isLoggedIn()) {
            if(SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')) {
                if(params?.extension == "csv"){
                    if(!params.max) params.max = 10

                    response.contentType = grailsApplication.config.grails.mime.types[params.extension]
                    response.setHeader("Content-disposition", "attachment; filename=output.${params.extension}")

                    List fields = ["patent_number", "title", "abs", "year", "date", "first_claim", "inventor", "assignee", "ipc", "upc", "cpc", "citedby3", "cites"]
                    Map labels = ["patent_number": "Publication Number", "title": "Title",
                                  "abs":"Abstract", "year":"Publication Year", "date":"Publication Date", "first_claim":"First Claim", "inventor":"Inventor(s)", "assignee":"Assignee(s)",
                                  "ipc":"IPC(s)", "upc":"UPC(s)", "cpc":"CPC(s)", "citedby3":"Cited By (3 Years)", "cites":"Cites"]
//                    def upperCase = { value ->
//                        return value.toUpperCase()
//                    }
//                    Map formatters = [patent_number: upperCase]
//                    Map parameters = [title: "Cool books"]

                    exportService.export(params?.extension, response.outputStream,lists, fields, labels, [:],[:])
                }
                else {
                    def currentUser = springSecurityService.getCurrentUser()
                    def data = !lists.isEmpty() ? 1 : 0

                    if (input != "" && data == 0){
                        flash.message = "query.running.message"
                        flash.args = ["Query is already being executed. Refresh after some time"]
                    }
                    else {
                        flash.clear()
                    }
                    render view: 'index', model: [currentUser:currentUser, sqlQuery:input, data:data]
                }
            }
        }
        else {
            redirect(controller: 'login', action: 'auth')
        }
    }

//    def download() {
//        if(springSecurityService.isLoggedIn()) {
//            if(SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')) {
//
//                }
//        }
//        else {
//            redirect(controller: 'login', action: 'auth')
//        }
//    }
    def clear(){
        input = ""
        lists.clear()
        flash.clear()
        redirect( action: 'index')
    }

    def parser(){
            //        homeService.parseQuery(params.query)
//        println "parser"
            def user = springSecurityService.getCurrentUser()
            input = params.query
//        println input
            String prefix = homeService.translate(input)
//        println "preQuery = $prefix"
            queryGeneratorService.setPrefixQuery(prefix)

            lists.clear()
            /*query += "SELECT concat(p.country,p.number) as 'Publication Number', " +
                    "p.title, p.abstract, p.first_claim, year(p.date), p.date, " +
                    "concat(ir.name_first, ' ', ir.name_last) as 'Inventor(s)', " +
                    "concat(a.organization,a.name_first, ' ', a.name_last) as 'Assignee(s)'," +
                    "u.subclass_id as 'All_UPC', " + "concat(i.section, i.ipc_class, " +
                    "i.subclass, i.main_group, '/', i.subgroup) as 'All_IPC', " +
                    "p.citation3 as 'cited_by_within_3_years' FROM patent p " +
                    "INNER JOIN inventor ir ON p.country = ir.patent_country AND p.id = ir.patent_id " +
                    "INNER JOIN assignee a ON p.country = a.patent_country AND p.id = a.patent_id " +
                    "INNER JOIN uspc u ON p.country = u.patent_country AND p.id = u.patent_id " +
                    "INNER JOIN ipcr i ON p.country = i.patent_country AND p.id = i.patent_id " +
                    "INNER JOIN cpc c ON p.country = c.patent_country AND p.id = c.patent_id " +
                    "INNER JOIN application ap ON p.country = ap.patent_country AND p.id = ap.patent_id " +
                    "INNER JOIN citation ct ON p.country = ct.patent_country AND p.id = ct.patent_id " +
                    "WHERE "*/
            def query = "SELECT concat(p.country,p.id) as 'publication_number', " +
                    "p.title, p.abstract, year(p.date) as year, p.date, " +
                    "p.first_claim, "+
                    "p.inventors, " +
                    "p.assignees, " +
                    "p.upc, " + "p.ipc, " + "p.cpc, " +
                    "p.citedby3, " + "p.cites " +
                    "FROM patentfinal p "
        println "query = $query"
            def whereClause = queryGeneratorService.parseQuery()
//        println "whereClause = $whereClause"
            if (whereClause.contains("ERROR")){
                def message = whereClause.split(":")
                println "Error message = " + message[1]
                flash.message = "query.invalid.message"
                flash.args = [message[1]]
                flash.default = "invalid query"
                queryGeneratorService.reset()
            }
            else {
                def whereQuery = "WHERE " + whereClause
                def tableList = queryGeneratorService.tables

                for (String table in tableList) {
                    if (table == "inventor") {
                        query += joinInventor
                        //                println query
                    } else if (table == "assignee") {
                        query += joinAssignee
                        //                println query
                    } else if (table == "upc") {
                        query += joinUPC
                        //                println query
                    } else if (table == "ipc") {
                        query += joinIPC
                        //                println query
                    } else if (table == "cpc") {
                        query += joinCPC
                        //                println query
                    } else if (table == "application") {
                        query += joinApplication
                        //                println query
                    } else if (table == "citation") {
                        query += joinCitation
                        //                println query
                    }
                }
                queryGeneratorService.reset()
                query += whereQuery
                println "query " + query
                def sql = new Sql(dataSource)
                def data = sql.rows(query)
                sql.close()
                println "data size " + data.size()
                if (data.size() == 0){
                    flash.message = "result.empty.message"
                    flash.default = "empty result"
                }
                data.each {
                    Patent patentdemo = new Patent()
                    patentdemo.patent_number = it.publication_number
                    patentdemo.title = it.title
                    patentdemo.abs = it.abstract
                    patentdemo.year = it.year
                    patentdemo.date = it.date
                    patentdemo.first_claim = it.first_claim
                    patentdemo.assignee = it.assignees
                    patentdemo.inventor = it.inventors
                    patentdemo.ipc = it.ipc
                    patentdemo.upc = it.upc
                    patentdemo.cpc = it.cpc
                    patentdemo.citedby3 = it.citedby3
                    patentdemo.cites = it.cites

                    lists.add(patentdemo)
                }
            }
/*//        println data
//        lists.clear()

        println "lists.size() = $lists.size"*/
            redirect( action: 'index')
    }

    def changePassword(){
        def user = springSecurityService.currentUser
        def status= validPassword(user,params.currentPassword,params.newPassword,params.repeatPassword)

        if(status.equals('valid')){
            status= applyUpdatePassword(user,params.newPassword)
            if (status.equals('error')){
                flash.message = "password.invalid.message"
                flash.args = ["Unable to change password"]
                flash.default = "Unable to change password."
            }
        }else {
            flash.message = "password.invalid.message"
            flash.args = [status]
            flash.default = "Unable to change password."
        }
        redirect( controller:"home", action: "index", model: [currentUser:user])
    }

    /*This method checks whether the new password that the user wants to change to is valid*/
    def validPassword(def user, def password, def newPassword, def newPassword2)
    {
        if (!springSecurityService.passwordEncoder.isPasswordValid(user.password, password, null /*salt*/)) {
            return 'Invalid current password'
        }

        if (springSecurityService.passwordEncoder.isPasswordValid(user.password, newPassword, null /*salt*/)) {
            return 'New password is same as current password'
        }
        return 'valid'
    }

    /*This method updates the password of the user to the new password*/
    def applyUpdatePassword(def user, def newPassword){
        user.password = newPassword
        if(!user.save(flush: true, failOnError: true)){
            return 'error'
        }
        else {
            return 'success'
        }
    }
}
