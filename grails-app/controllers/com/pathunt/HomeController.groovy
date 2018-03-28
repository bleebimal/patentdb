package com.pathunt

import com.mysql.jdbc.exceptions.MySQLQueryInterruptedException
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured
import groovy.sql.Sql
import groovy.time.TimeCategory
import static grails.async.Promises.*

import java.sql.SQLException

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
//    def joinUPC = "INNER JOIN uspc u ON p.country = u.patent_country AND p.id = u.patent_id "
    def joinUPC = "INNER JOIN uspcnew u ON p.id = u.patent_id "
//    def joinIPC = "INNER JOIN ipcr i ON p.country = i.patent_country AND p.id = i.patent_id "
    def joinIPC = "INNER JOIN ipcrnew i ON p.id = i.patent_id "
//    def joinCPC = "INNER JOIN cpc c ON p.country = c.patent_country AND p.id = c.patent_id "
    def joinCPC = "INNER JOIN cpcnew c ON p.id = c.patent_id "
    def joinApplication = "INNER JOIN application ap ON p.country = ap.patent_country AND p.id = ap.patent_id "
    def joinCitation = "INNER JOIN uspatentcitation ct ON p.country = ct.patent_country AND p.id = ct.patent_id "
//    def joinLocation = "INNER JOIN location l ON p.country = l.patent_country AND p.id = l.patent_id "
    def killQuery = "Kill query "
    def getProcessIdQuery = "SELECT id FROM INFORMATION_SCHEMA.processlist where Info LIKE '%"
    def static userInput = new HashMap<String,Query>()
    def selectQuery = "SELECT distinct concat(p.country,p.id) as 'publication_number', " +
            "p.title, p.abstract, year(p.date) as year, p.date, " +
            "p.first_claim, "+
            "p.inventors, " +
            "p.assignees, " +
            "p.upc, " + "p.ipc, " + "p.cpc, " +
            "p.citedby3, " + "p.cites " +
            "FROM patentfinal p "
    def countQuery = "SELECT distinct count(p.id) as 'total' " +
            "FROM patentfinal p "

    def index() {
        if(springSecurityService.isLoggedIn()) {
            if(SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')) {
                def currentUser = springSecurityService.getCurrentUser()
                def inputQuery = userInput.get(currentUser)
                if(params?.extension == "csv"){
                    def export = true
                    if(!params.max) params.max = 10
                    if (inputQuery != null){
                        if (!inputQuery.sample.isEmpty()){
                            exportCSV(currentUser, inputQuery)
                            try {
                                redirect(action: 'home')
                            }catch (Exception e){
                                e.printStackTrace()
                            }
                        }
                        else {
                            flash.message = "query.running.message"
                            flash.args = ["No results to download."]
                            export = false
                        }
                    }
                    else {
                        flash.message = "query.running.message"
                        flash.args = ["No results to download. First run a query."]
                        export = false
                    }
                    if (!export){
                        render view: 'index', model: [currentUser:currentUser]
                    }
                }
                else {
                    def data = 0
                    def input = ""
                    def active = false
                    def runBackgroundTask = false
                    def duration = 0
                    def sample = new ArrayList<Patent>()

                    if (inputQuery != null){
                        def empty = inputQuery.sample != null ? inputQuery.sample.isEmpty() : true
                        data = !empty ? inputQuery.totalResultCount : 0
                        input = inputQuery.query
                        active = inputQuery.isActive
                        duration = inputQuery.duration
                        sample = inputQuery.sample != null ? inputQuery.sample : sample
                        if (active){
                            flash.message = "query.running.message"
                            flash.args = ["Click CSV to download OR click STOP to run new query."]
                            if (!inputQuery.error && (inputQuery.result == null || inputQuery?.result?.isEmpty())){
                                runBackgroundTask = true
                            }
                        }
                        else if(!empty){
                            flash.clear()
                        }
                        else {
                            if (inputQuery.interrupted){
                                flash.message = "sql.interrupt.message"
                                flash.args = ["Query Execution Interrupted"]
                            }
                            else if (inputQuery.error){
                                flash.message = "sql.invalid.message"
                                flash.args = ["Syntax error! Please check the query again"]

                            }
                        }
                    }
                    if (runBackgroundTask){
                        def a = task {
                            def success = false
                            try {
                                println "Background task Started"
                                success = backgroundTask(currentUser, inputQuery)
                                println "Background task Stopped"
                            }catch (Exception e){
//                                println "Error: "
                                println e.printStackTrace()
                            }finally{
//                                render view: 'index', model: [currentUser: currentUser, sqlQuery: input, data: data, duration: duration, active: active, sample: sample]
                            }
                        }
                    }
                    render view: 'index', model: [currentUser:currentUser, sqlQuery:input, data:data, duration:duration, active:active, sample:sample]
                }
            }
        }
        else {
            redirect(controller: 'login', action: 'auth')
        }
    }

    def exportCSV(def user, Query inputQuery){
        response.contentType = grailsApplication.config.grails.mime.types[params.extension]
        response.setHeader("Content-disposition", "attachment; filename=output.${params.extension}")

        List fields = ["patent_number", "title", "abs", "year", "date", "first_claim", "inventor", "assignee", "ipc", "upc", "cpc", "citedby3", "cites"]
        Map labels = ["patent_number": "Publication Number", "title": "Title",
                      "abs":"Abstract", "year":"Publication Year", "date":"Publication Date", "first_claim":"First Claim", "inventor":"Inventor(s)", "assignee":"Assignee(s)",
                      "ipc":"IPC(s)", "upc":"UPC(s)", "cpc":"CPC(s)", "citedby3":"Cited By (3 Years)", "cites":"Cites"]
        while (true){
            inputQuery = userInput.get(user)
            if (!inputQuery.isActive){
                break
            }
        }
        def lists = inputQuery.result
        exportService.export(params?.extension, response.outputStream,lists, fields, labels, [:],[:])
    }

    def clear(){
        def currentUser = springSecurityService.getCurrentUser()
        userInput.remove(currentUser)
        flash.clear()
        redirect( action: 'index')
    }

    def stop(){
        def currentUser = springSecurityService.getCurrentUser()
        def runningQuery = userInput.get(currentUser)
        println runningQuery
        def sql = new Sql(dataSource)
        def query = killQuery + runningQuery?.queryProcessId
        try {
            sql.execute(query)
        }catch (SQLException e){
            flash.message = "sql.invalid.message"
            flash.args = ["Unable to stop the query execution. Please, refresh and try again."]
            flash.default = "Unable to stop the query execution."
            runningQuery.error = true
            userInput.put(currentUser, runningQuery)
        }
        finally{
            sql.close()
        }

        redirect( action: 'index')
    }

    def parser(){
        def user = springSecurityService.getCurrentUser()
        def userQuery = userInput.get(user)
        def active = false
        if (userQuery != null){
            active = userQuery.isActive
            if (!active){
                userQuery.reset()
            }
        }
        else {
            userQuery = new Query()
        }

        if (active){
            redirect( action: 'index')
        }
        else {
            userQuery.query = params.query
            userQuery.isActive = true
            userInput.put(user,userQuery)
            def whereQuery = getQuery(userQuery.query)
            if (whereQuery == "ERROR"){
                redirect( action: 'index')
            }
            else {
                userQuery.whereSQLQuery = whereQuery
                userInput.put(user,userQuery)
                getResult(user, userQuery)
            }
        }
    }

    def getQuery(String query){
        def c1 = new Date()
        def prefixError = false
        def whereQuery = ""
        def joinQuery = ""

        String prefix = homeService.translate(query)
//        println "prefix = $prefix"
        def prefixVal = prefix.split(":")
//        println "prefixVal = $prefixVal"

        if (prefixVal[0] == "Error"){
            prefixError = true
            flash.message = "cpc.invalid.message"
            flash.args = [prefixVal[1]]
            flash.default = "Incorrect CPC value"
        }
        else {
            println "preQuery = $prefix"
            queryGeneratorService.setPrefixQuery(prefix)
            def whereClause = queryGeneratorService.parseQuery()

            def c2 = new Date()
//                println "d2 = " + (d2)
            def cduration = TimeCategory.minus( c2, c1 )
            println "Time Duration where query = " + cduration

//        println "whereClause = $whereClause"
            if (whereClause.contains("ERROR")){
                prefixError = true
                def message = whereClause.split(":")
                println "Error message = " + message[1]
                flash.message = "query.invalid.message"
                flash.args = [message[1]]
                flash.default = "invalid query"
                queryGeneratorService.reset()
            }
            else {
                whereQuery = "WHERE " + whereClause
                def tableList = queryGeneratorService.tables

                for (String table in tableList) {
                    if (table == "inventor") {
                        joinQuery += joinInventor
                    } else if (table == "assignee") {
                        joinQuery += joinAssignee
                    } else if (table == "upc") {
                        joinQuery += joinUPC
                    } else if (table == "ipc") {
                        joinQuery += joinIPC
                    } else if (table == "cpc") {
                        joinQuery += joinCPC
                    } else if (table == "application") {
                        joinQuery += joinApplication
                    } else if (table == "citation") {
                        joinQuery += joinCitation
                    }
                }
                queryGeneratorService.reset()
            }
        }
        if (!prefixError){
            return joinQuery + whereQuery
        }
        else {
            return "ERROR"
        }
    }

    def getResult(def user, Query userQuery){
        def sqlError = false
        def error = false
        def emptyResult = false

        def p1 = task {

            println " Task 1 started"
            def data = []

            def query = countQuery + userQuery.whereSQLQuery + " -- count " + user
            println "count query " + query

            def d1, d2
            d1 = new Date()
            def sql = new Sql(dataSource)

            try {
//                println "d1 = " + (d1)
                data = sql.rows(query)
            }catch (OutOfMemoryError e){
                userQuery.error = true
                error = true
            }catch (SQLException a){
                userQuery.error = true
                sqlError = true
            }finally{
                sql.close()
                d2 = new Date()
//                println "d2 = " + (d2)
                def duration = TimeCategory.minus( d2, d1 )
                println "Time Duration count = " + duration
            }

//            println "data size " + data.size()
            if (!error && !sqlError /*&& !interruptedError*/){
                if (data.size() == 0){
                    flash.message = "result.empty.message"
                    flash.default = "empty result"
                }
                else {
                    data.each {
                        userQuery.totalResultCount = it.total
                    }
                }
            }
            else if (error){
                flash.message = "memory.exceeded.message"
                flash.args = ["Out of Memory. Query is too vague"]
                flash.default = "Resultset exceeded memory size."
            }
            else if (sqlError){
                flash.message = "sql.invalid.message"
                flash.args = ["Syntax error! Please check the query again"]
                flash.default = "Syntax error!"
            }
            userInput.put(user,userQuery)
            println "userQuery.totalResultCount = $userQuery.totalResultCount"
            println " Task 1 stopped"
        }

        def p2 = task {
            println " Task 2 started"
            def samples = new ArrayList<Patent>()

            if (!userQuery.error) {
                Thread.sleep(50)
                def sql = new Sql(dataSource)
                def data = []
                def query = selectQuery + userQuery.whereSQLQuery + " LIMIT 100 -- limit " + user
                println "limit query = $query"
                def a1, a2
                a1 = new Date()
                try {
                    data = sql.rows(query)
                } catch (SQLException a) {
                    sqlError = true
                    userQuery.error = true
                } finally {
                    sql.close()
                    a2 = new Date()
//                println "d2 = " + (d2)
                    def duration = TimeCategory.minus( a2, a1 )
                    println "Time Duration limit = " + duration
                    userQuery.duration = duration
                }
                if (!userQuery.error){
                    if (data.size() == 0){
                        emptyResult = true
                        flash.message = "result.empty.message"
                        flash.default = "empty result"
                    }
                    else {
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

                            samples.add(patentdemo)
                        }
                    }
                }
                else {
                    flash.message = "sql.invalid.message"
                    flash.args = ["Syntax error! Please check the query again"]
                    flash.default = "Syntax error!"
                }
            }

            userQuery.sample = samples
//            userQuery.isActive = false
            userInput.put(user,userQuery)
            println " Task 2 stopped"
            redirect( action: 'index')
        }

        onError([p1,p2]) { Throwable t ->
            println "An error occured ${t.message}"
        }
        waitAll(p1, p2)
    }

    def backgroundTask(def user, Query userQuery){
        def success = false
        try {
            def p3 = task {
                println " Task 3 started"
                def results = new ArrayList<Patent>()
                def interruptedError = false
                def sql = new Sql(dataSource)
                def data = []
                def query = selectQuery + userQuery.whereSQLQuery + " -- " + user
                println "full query = $query"
                def a1, a2
                a1 = new Date()
                try {
                    data = sql.rows(query)
                    success = true
                }catch(MySQLQueryInterruptedException c){
                    userQuery.interrupted = true
                    userQuery.error = true
                }
                catch (SQLException a) {
                    userQuery.error = true
                } finally {
                    sql.close()
                    a2 = new Date()
//                println "d2 = " + (d2)
                    def duration = TimeCategory.minus( a2, a1 )
                    println "Time Duration full query = " + duration
                }
                if (!userQuery.error && !userQuery.interrupted) {
                    if (data.size() == 0) {
                        flash.message = "result.empty.message"
                        flash.default = "empty result"
                    } else {
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

                            results.add(patentdemo)
                        }
                    }
                }

                userQuery.result = results
                userQuery.isActive = false
                userInput.put(user, userQuery)
                println " Task 3 stopped"
            }

            def p4 = task {
                println " Task 4 started"

                if (!userQuery.error) {
                    Thread.sleep(500)
                    def sql = new Sql(dataSource)
                    def data = []
                    def whClause = userQuery.whereSQLQuery.replace("'","\\'") +
                            " -- " + user + "'"
                    def query = getProcessIdQuery + whClause
                    def a1, a2
                    a1 = new Date()
                    try {
//                println "getProcessIdQuery = $query"
                        data = sql.rows(query)
                        success = true
                    }catch (SQLException a){
                        userQuery.error = true
                    }finally{
                        sql.close()
                        a2 = new Date()
//                println "d2 = " + (d2)
                        def duration = TimeCategory.minus( a2, a1 )
                        println "Time Duration query id = " + duration
                    }
                    if (!userQuery.error){
                        if (data.size() != 0){

                            data.each {
                                userQuery.queryProcessId = it.id
                            }
                        }
                    }
                }
                userInput.put(user,userQuery)
                println "userQuery.queryProcessId = $userQuery.queryProcessId"
                println " Task 4 stopped"
            }
            waitAll(p3, p4)
        }catch (Exception e){
//            println e.printStackTrace()
        }
        return success
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
