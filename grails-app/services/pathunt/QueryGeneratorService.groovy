package pathunt

import grails.transaction.Transactional

@Transactional
class QueryGeneratorService {
    String prefixQuery = ""

    def setPrefixQuery(String prefixQuery){
        this.prefixQuery = prefixQuery
    }

    def parseQuery() {
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
            String firstQueryPart = prefixQuery.substring(0, spaceIndex)
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
                    condition2 = condition2.replace("BETWEEN","NOT BETWEEN")
//                println "(" + condition1 + ")" + " AND " + "(" + condition2 + ")"
                    return "(" + condition1 + ")" + " AND " + "(" + condition2 + ")"
                default:
                    return generateWhereCondition(firstQueryPart)
            }
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

//        println "operand = $operand"
//        println "queryField = $queryField"
//        println "valueField = $valueField"

        switch (queryField){
            case "TA":
                return "p.TITLE LIKE " + valueField + " OR " + "p.ABSTRACT LIKE " + valueField

            case "PBD":
                valueField = valueField.replace("%","")
                valueField = generateDate(valueField)
//                println "valueField = $valueField"
                if (valueField.length() > 12){
                    valueField = valueField.replace(" TO ","' AND '")

                    return "p.DATE BETWEEN " + valueField
                }
                else {
                    return "p.DATE = " + valueField
                }
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
}
