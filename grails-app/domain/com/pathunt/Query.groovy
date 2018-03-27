package com.pathunt

class Query {

    String query
    String whereSQLQuery = ""
    String duration = ""
    String queryProcessId = ""
    int totalResultCount
    boolean isActive
    boolean error
    boolean interrupted
    ArrayList<Patent> result
    ArrayList<Patent> sample

    static mapping = {
        query defaultValue: ""
        whereSQLQuery defaultValue: ""
        isActive defaultValue: "false"
        error defaultValue: "false"
        interrupted defaultValue: "false"
        duration defaultValue: ""
        queryProcessId defaultValue: ""
        totalResultCount defaultValue: 0
    }

    def reset(){
        query = ""
        whereSQLQuery = ""
        duration = ""
        queryProcessId = ""
        totalResultCount = 0
        isActive = false
        error = false
        interrupted = false
        result.clear()
        sample.clear()
    }
}
