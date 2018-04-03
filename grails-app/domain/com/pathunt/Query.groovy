package com.pathunt

class Query {

    String query = ""
    String whereSQLQuery = ""
    String duration = ""
    String queryProcessId = ""
    String errorQuery = ""
    int totalResultCount = 0
    boolean isActive = false
    boolean outOfMemory = false
    boolean error = false
    boolean interrupted = false
    boolean stopError = false
    ArrayList<Patent> result = new ArrayList<>()
    ArrayList<Patent> sample = new ArrayList<>()

    def reset(){
        query = ""
        whereSQLQuery = ""
        duration = ""
        queryProcessId = ""
        errorQuery = ""
        totalResultCount = 0
        isActive = false
        error = false
        interrupted = false
        stopError = false
        outOfMemory = false
        result.clear()
        sample.clear()
    }
}
