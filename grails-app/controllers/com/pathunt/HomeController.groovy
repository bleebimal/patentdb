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

    def joinInventor = "INNER JOIN inventorfinal ir ON p.country = ir.patent_country AND p.id = ir.patent_id "
    def joinAssignee = "INNER JOIN assigneefinal a ON p.country = a.patent_country AND p.id = a.patent_id "
    def joinUPC = "INNER JOIN uspc u ON p.country = u.patent_country AND p.id = u.patent_id "
    def joinIPC = "INNER JOIN ipcr i ON p.country = i.patent_country AND p.id = i.patent_id "
    def joinCPC = "INNER JOIN cpc c ON p.country = c.patent_country AND p.id = c.patent_id "
    def joinApplication = "INNER JOIN application ap ON p.country = ap.patent_country AND p.id = ap.patent_id "
    def joinCitation = "INNER JOIN uspatentcitation ct ON p.country = ct.patent_country AND p.id = ct.patent_id "
//    def joinLocation = "INNER JOIN location l ON p.country = l.patent_country AND p.id = l.patent_id "

    def static userInput = new HashMap<String,Query>()

    def index() {
        if(springSecurityService.isLoggedIn()) {
            if(SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')) {
                def currentUser = springSecurityService.getCurrentUser()
                def inputQuery = userInput.get(currentUser)
                if(params?.extension == "csv"){
                    if(!params.max) params.max = 10

                    response.contentType = grailsApplication.config.grails.mime.types[params.extension]
                    response.setHeader("Content-disposition", "attachment; filename=output.${params.extension}")

                    List fields = ["patent_number", "title", "abs", "year", "date", "first_claim", "inventor", "assignee", "ipc", "upc", "cpc", "citedby3", "cites"]
                    Map labels = ["patent_number": "Publication Number", "title": "Title",
                                  "abs":"Abstract", "year":"Publication Year", "date":"Publication Date", "first_claim":"First Claim", "inventor":"Inventor(s)", "assignee":"Assignee(s)",
                                  "ipc":"IPC(s)", "upc":"UPC(s)", "cpc":"CPC(s)", "citedby3":"Cited By (3 Years)", "cites":"Cites"]

                    def lists = inputQuery.result
                    exportService.export(params?.extension, response.outputStream,lists, fields, labels, [:],[:])
                }
                else {
                    def data = 0
                    def input = ""
                    if (inputQuery != null){
                        data = inputQuery.result != null ? 1 : 0
                        input = inputQuery.query
                        if (inputQuery.isActive){
                            flash.message = "query.running.message"
                            flash.args = ["Query is already being executed. Refresh after some time"]
                        }
                        else {
                            flash.clear()
                        }
                    }
                    render view: 'index', model: [currentUser:currentUser, sqlQuery:input, data:data]
                }
            }
        }
        else {
            redirect(controller: 'login', action: 'auth')
        }
    }

    def clear(){
        def currentUser = springSecurityService.getCurrentUser()
        userInput.remove(currentUser)
        flash.clear()
        redirect( action: 'index')
    }

    def parser(){
        def lists = new ArrayList<Patent>()
        def data = []
        def user = springSecurityService.getCurrentUser()
        def userQuery = new Query()
        userQuery.query = params.query
        userQuery.isActive = true
        userInput.put(user,userQuery)
        String prefix = homeService.translate(userQuery.query)
//        println "preQuery = $prefix"
        queryGeneratorService.setPrefixQuery(prefix)

        def query = "SELECT concat(p.country,p.id) as 'publication_number', " +
                "p.title, p.abstract, year(p.date) as year, p.date, " +
                "p.first_claim, "+
                "p.inventors, " +
                "p.assignees, " +
                "p.upc, " + "p.ipc, " + "p.cpc, " +
                "p.citedby3, " + "p.cites " +
                "FROM patentfinal p "
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
                } else if (table == "assignee") {
                    query += joinAssignee
                } else if (table == "upc") {
                    query += joinUPC
                } else if (table == "ipc") {
                    query += joinIPC
                } else if (table == "cpc") {
                    query += joinCPC
                } else if (table == "application") {
                    query += joinApplication
                } else if (table == "citation") {
                    query += joinCitation
                }
            }
            queryGeneratorService.reset()
            query += whereQuery
            println "query " + query
            def sql = new Sql(dataSource)

            try {
                data = sql.rows(query)
            }catch (OutOfMemoryError e){
                flash.message = "memory.exceeded.message"
                flash.args = ["Out of Memory. Query is too vague"]
                flash.default = "Resultset exceeded memory size."
                userQuery.isActive = false
                userInput.put(user,userQuery)
                redirect( action: 'index')
            }finally{
                sql.close()
            }
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
        userQuery.result = lists
        userQuery.isActive = false
        userInput.put(user,userQuery)
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
