package com.pathunt

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured
import groovy.sql.Sql

@Secured('permitAll')
class HomeController {
    def springSecurityService
    def homeService
    def queryGeneratorService
    def query = ""
    def joinInventor = "INNER JOIN inventor ir ON p.country = ir.patent_country AND p.id = ir.patent_id "
    def joinAssignee = "INNER JOIN assignee a ON p.country = a.patent_country AND p.id = a.patent_id "
    def joinUPC = "INNER JOIN uspc u ON p.country = u.patent_country AND p.id = u.patent_id "
    def joinIPC = "INNER JOIN inventor ir ON p.country = ir.patent_country AND p.id = ir.patent_id "
    def joinCPC = "INNER JOIN cpc c ON p.country = c.patent_country AND p.id = c.patent_id "
    def joinApplication = "INNER JOIN application ap ON p.country = ap.patent_country AND p.id = ap.patent_id "
    def joinCitation = "INNER JOIN citation ct ON p.country = ct.patent_country AND p.id = ct.patent_id "
    def joinLocation = "INNER JOIN location l ON p.country = l.patent_country AND p.id = l.patent_id "
    def data = []
    def dataSource
    def exportService
    def grailsApplication

    ArrayList<Patentdemo> lists = new ArrayList<Patentdemo>()
    def index() {
        if(springSecurityService.isLoggedIn()) {
            if(SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')) {
                if(params?.extension == "csv"){
                    if(!params.max) params.max = 10


                    response.contentType = grailsApplication.config.grails.mime.types[params.extension]
                    response.setHeader("Content-disposition", "attachment; filename=output.${params.extension}")
                    println "size of lists" + lists.size()

                    List fields = ["patent_number", "title", "abs", "year", "date"]
                    Map labels = ["patent_number": "Publication Number", "title": "Title", "abs":"Abstract", "year":"Publication Year", "date":"Publication Date"]
//                    def upperCase = { value ->
//                        return value.toUpperCase()
//                    }
//                    Map formatters = [patent_number: upperCase]
//                    Map parameters = [title: "Cool books"]

                    exportService.export(params?.extension, response.outputStream,lists, fields, labels, [:],[:])
                    query = ""
                    data = []
                    lists.clear()
                }
                else {
                    def currentUser = springSecurityService.getCurrentUser()
                    render view: 'index', model: [currentUser:currentUser, sqlQuery:query, data:data]
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

    def parser(){
//        homeService.parseQuery(params.query)
//        println "parser"
        String preQuery = "[" + homeService.translate(params.query) + "]"
        println "preQuery = $preQuery"
        String prefix = homeService.prefixConverter(preQuery)
        queryGeneratorService.setPrefixQuery(prefix)
        query = ""
        data = []
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
        query += "SELECT p.patent_number as 'publication_number', " +
                 "p.title, p.abstract, year(p.date) as year, p.date " +
//                 "p.inventors as 'Inventor(s)', " +
//                 "p.assignees as 'Assignee(s)'," +
//                 "p.upcs as 'All_UPC', " + "p.ipcs as 'All_IPC', " +
//                 "p.citation3 as 'cited_by_within_3_years' " +
                "FROM patentdemo p "

        def whereQuery = "WHERE " + queryGeneratorService.parseQuery()
        def tableList = queryGeneratorService.tables

        for (String table in tableList) {
            if (table == "inventor") {
                query += joinInventor
                println query
            } else if (table == "assignee") {
                query += joinAssignee
                println query
            } else if (table == "upc") {
                query += joinUPC
                println query
            } else if (table == "ipc") {
                query += joinIPC
                println query
            } else if (table == "cpc") {
                query += joinCPC
                println query
            } else if (table == "application") {
                query += joinApplication
                println query
            } else if (table == "citation") {
                query += joinCitation
                println query
            }
        }
        queryGeneratorService.reset()
        query += whereQuery
        println query
        def sql = new Sql(dataSource)
        data = sql.rows(query)
        data.each {
            Patentdemo patentdemo = new Patentdemo()
            patentdemo.patent_number = it.publication_number
            patentdemo.title = it.title
            patentdemo.abs = it.abstract
            patentdemo.year = it.year
            patentdemo.date = it.date
            lists.add(patentdemo)
        }
/*//        println data
//        lists.clear()

        println "lists.size() = $lists.size"*/
        redirect(action: 'index')
    }
}
