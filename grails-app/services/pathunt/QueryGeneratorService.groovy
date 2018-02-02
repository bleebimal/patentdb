package pathunt

import grails.transaction.Transactional

@Transactional
class QueryGeneratorService {
    String prefixQuery = ""
    String firstQueryPart = ""
    boolean inventor = false
    boolean assignee = false
    boolean ipc = false
    boolean upc = false
    boolean cpc = false
    boolean application = false
    boolean citation = false
    boolean claim = false
    boolean error = false
    ArrayList<String> tables = new ArrayList<String>()

    def setPrefixQuery(String prefixQuery){
        this.prefixQuery = prefixQuery
    }

    def parseQuery() {
        println "error = $error"
        if (! error){
            //        println "prefixQuery = $prefixQuery"
            int spaceIndex = prefixQuery.indexOf(" ")
//        println "spaceIndex = $spaceIndex"
            int len = prefixQuery.length()
            if (spaceIndex == -1 && len > 0) {
//            println " EMPTY -----------"
                spaceIndex = len
            }
//        println "prefixQuery.length = " + len
            if (len > 0){
                firstQueryPart = prefixQuery.substring(0, spaceIndex)
                prefixQuery = prefixQuery.substring(spaceIndex + 1, len)
//            println "firstQueryPart = $firstQueryPart"

                switch (firstQueryPart){
                    case "AND":
                        String condition1 = parseQuery()
                        String condition2 = parseQuery()
//                println "(" + condition1 + ")" + " AND " + "(" + condition2 + ")"
                        return "(" + condition1 + ")" + " AND " + "(" + condition2 + ")"

                    case "OR":
                        String condition1 = parseQuery()
                        String condition2 = parseQuery()
//                println "(" + condition1 + ")" + " OR " + "(" + condition2 + ")"
                        return "(" + condition1 + ")" + " OR " + "(" + condition2 + ")"

                    case "NOT":
                        String condition1 = parseQuery()
                        String condition2 = parseQuery().replace("LIKE","NOT LIKE")
                        condition2 = parseQuery().replace("=","<>")
                        condition2 = condition2.replace("BETWEEN","NOT BETWEEN")
//                println "(" + condition1 + ")" + " AND " + "(" + condition2 + ")"
                        return "(" + condition1 + ")" + " AND " + "(" + condition2 + ")"

                    default:
                        return generateWhereCondition(firstQueryPart)
                }
            }
        }
        else {
            println "Error:$firstQueryPart"
            return "ERROR:" + firstQueryPart
        }
    }

    def generateWhereCondition(String operand){
        //TODO: error handling for queryField:(valueFiled) not in operand

        String[] fields = operand.split(":")
        String queryField = fields[0]
        String valueField = fields[1]

        valueField = valueField.replace("(","'%")
        valueField = valueField.replace(")","%'")
        valueField = valueField.replace("_"," ")

        println "operand = $operand"
        println "queryField = $queryField"
        println "valueField = $valueField"

        switch (queryField){
            case "TTL":
                return "p.title LIKE " + valueField

            case "ABST":
                return "p.abstract LIKE " + valueField

            case "ACLM":
                return "p.first_claim LIKE " + valueField

            case "IPC":
                /*if (!ipc){
                    ipc = true
                }*/
                if (!tables.contains("ipc")){
                    tables.add("ipc")
                    println "size of table: " + tables.size()
                }
                valueField = valueField.replace("%","")
                return "concat(i.section, i.ipc_class, " +
                        "i.subclass, i.main_group, '/', i.subgroup) = " + valueField
            case "UPC":
                /*if (!upc){
                    upc = true
                }*/
                if (!tables.contains("upc")){
                    tables.add("upc")
                }
                valueField = valueField.replace("%","")
                return "u.subclass_id = " + valueField

            case "CPC":
                /*if (!cpc){
                    cpc = true
                }*/
                if (!tables.contains("cpc")){
                    tables.add("cpc")
                }
                valueField = valueField.replace("%","")
                return "c.subclass_id = " + valueField

            case "AN":
                /*if (!assignee){
                    assignee = true
                }*/
                if (!tables.contains("assignee")){
                    tables.add("assignee")
                    println "size of table: " + tables.size()
                }
                return "coalesce(a.organization,nullif(concat(" +
                        "a.name_first, ' ', a.name_last), ' ')) LIKE " + valueField

            case "IN":
                /*if (!inventor){
                    inventor = true
                }*/
                if (!tables.contains("inventor")){
                    tables.add("inventor")
                }
                return "concat(ir.name_first, ' ', ir.name_last) LIKE " + valueField

            case "TA":
                return "p.title LIKE " + valueField + " OR " + "p.abstract LIKE " + valueField

            case "ISD":
                valueField = valueField.replace("%","")
                valueField = generateDate(valueField)
//                println "valueField = $valueField"
                if (valueField.length() > 12){
                    valueField = valueField.replace(" TO ","' AND '")

                    return "p.date BETWEEN " + valueField
                }
                else {
                    return "p.date = " + valueField
                }

            case "PBD":
                valueField = valueField.replace("%","")
                valueField = generateDate(valueField)
//                println "valueField = $valueField"
                if (valueField.length() > 12){
                    valueField = valueField.replace(" TO ","' AND '")

                    return "p.date BETWEEN " + valueField
                }
                else {
                    return "p.date = " + valueField
                }

            case "PN":
                valueField = valueField.replace("%","")
                return "concat(p.country, p.number) = " + valueField

            case "APN":
                /*if(!application){
                    application = true
                }*/
                if (!tables.contains("application")){
                    tables.add("application")
                }
                valueField = valueField.replace("%","")
                return "concat(ap.country, ap.series_code, '/', RIGHT(ap.number,6)) = " + valueField

            case "APD":
                /*if(!application){
                    application = true
                }*/
                if (!tables.contains("application")){
                    tables.add("application")
                }
                valueField = valueField.replace("%","")
                valueField = generateDate(valueField)
//                println "valueField = $valueField"
                if (valueField.length() > 12){
                    valueField = valueField.replace(" TO ","' AND '")

                    return "ap.date BETWEEN " + valueField
                }
                else {
                    return "ap.date = " + valueField
                }
            case "APDY":
                /*if(!application){
                    application = true
                }*/
                if (!tables.contains("application")){
                    tables.add("application")
                }
                valueField = valueField.replace("%","")
                valueField = generateDate(valueField)
                return "year(ap.date) = " + valueField

            case "PBDY":
                valueField = valueField.replace("%","")
                valueField = generateDate(valueField)
                return "year(p.date) = " + valueField

            case "MIPC":
                /*if (!ipc){
                    ipc = true
                }*/
                if (!tables.contains("ipc")){
                    tables.add("ipc")
                }
                valueField = valueField.replace("%","")
                return "concat(i.section, i.ipc_class, " +
                        "i.subclass, i.main_group, '/', i.subgroup) = " + valueField + "AND i.sequence = 0"

            case "MUPC":
                /*if (!upc){
                    upc = true
                }*/
                if (!tables.contains("upc")){
                    tables.add("upc")
                }
                valueField = valueField.replace("%","")
                return "u.subclass_id = " + valueField + "AND u.sequence = 0"

            case "REF":
                /*if (!citation){
                    citation = true
                }*/
                if (!tables.contains("citation")){
                    tables.add("citation")
                }
                valueField = valueField.replace("%","")
                return "concat(ct.patent_country, ct.citation_id) = " + valueField

            case "CITEDBY_COUNT":
                valueField = valueField.replace("%","")
                return "p.citedby = " + valueField

            case "CITES":
                /*if (!citation){
                    citation = true
                }*/
                if (!tables.contains("citation")){
                    tables.add("citation")
                }
                valueField = valueField.replace("%","")
                return "concat(ct.patent_country, ct.patent_id) = " + valueField

            case "CITES_COUNT":
                valueField = valueField.replace("%","")
                return "p.cites = " + valueField

            case "INS":
                /*if (!inventor){
                    inventor = true
                }*/
                if (!tables.contains("inventor")){
                    tables.add("inventor")
                }
                return "concat(ir.name_first, ' ', ir.name_last) LIKE " + valueField

            case "CITEDBY_COUNT3":
                valueField = valueField.replace("%","")
                return "p.citedby3 = " + valueField

            case "CITEDBY_COUNT5":
                valueField = valueField.replace("%","")
                return "p.citedby5 = " + valueField

            case "CAN":
                /*if (!assignee){
                    assignee = true
                }*/
                if (!tables.contains("assignee")){
                    tables.add("assignee")
                }
                return "coalesce(a.organization,nullif(concat(" +
                        "a.name_first, ' ', a.name_last), ' ')) LIKE " + valueField

            default:
                error = true
                return "ERROR:$firstQueryPart"
        }
    }

    def generateDate(String dateField){
        if (dateField.length() > 10){

            int indexOfT = dateField.indexOf("T")

            String date1 = dateField.substring((indexOfT - 9), (indexOfT - 1))
            String date2 = dateField.substring((indexOfT + 3), (indexOfT + 11))

            String newDate1 = date1.substring(0,4) + "-" + date1.substring(4,6) + "-" +
                    date1.substring(6,8)
            String newDate2 = date2.substring(0,4) + "-" + date2.substring(4,6) + "-" +
                    date2.substring(6,8)

            dateField = dateField.replace(date1,newDate1)
            dateField = dateField.replace(date2,newDate2)
        }
        else {
            String date = dateField.substring(1,9)
            String newDate = date.substring(0,4) + "-" + date.substring(4,6) + "-" +
                    date.substring(6,8)

            dateField = dateField.replace(date,newDate)
        }

        return dateField
    }

    def reset(){
        inventor = false
        assignee = false
        ipc = false
        upc = false
        cpc = false
        application = false
        citation = false
        claim = false
        tables.clear()
        error = false
    }
}
