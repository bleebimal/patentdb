package pathunt

import grails.transaction.Transactional
import org.apache.commons.lang.StringUtils

@Transactional
class HomeService {
//[[TA:(virus) OR TA:(viral)] AND [TA:(separation) OR TA:(clearance)]]
    def parseQuery(String query) {
        boolean patent = false
        boolean cpc = false
        boolean ipcr = false
        boolean uspc = false
        boolean inventor = false
        boolean assignee = false
        boolean first = true
        boolean publicationNumber = false

        String[] customQueries = query.split(" ")
        String sqlQuery = "SELECT "
        ArrayList<String> conditions = new ArrayList<String>()
        for (int i = 0; i < customQueries.length ; i+=2) {
            //String trimmedCustomQuery = customQueries[i].trim()
            if (customQueries[i].startsWith("PN:(")){
                patent = true
                String value = StringUtils.substringBetween(customQueries[i],"(",")")
                int valueLength = value.length()-1
                String condition = ""
                if (first){
                    condition = "country = " + value.substring(0,1) + " and " +
                            "id = " + value.substring(2,valueLength)
                }
                else{
                    condition = customQueries[i+1] + "country = " + value.substring(0,1) +
                                " AND " + "id = " + value.substring(2,valueLength)
                }
                conditions.add(condition)
            }
        }
    }

    def prefixConverter(String queryExpression){
        Stack stack = new Stack()
        String prefix = ""
        int len = queryExpression.length() - 1



        while(len >= 0){
            println "len = $len"
            char c = queryExpression.charAt(len)
            println "c = $c"

            if(c == ' '){
                queryExpression = queryExpression.substring(0,len)
                println "queryExpression = $queryExpression"
                len = queryExpression.length() - 1
                continue
            }
            else if(c == '['){
                prefix = ((String)stack.pop()) + " " + prefix
                queryExpression = queryExpression.substring(0,len)
                println "queryExpression = $queryExpression"
                len = queryExpression.length() - 1
            }
            else if(c == ']'){
                queryExpression = queryExpression.substring(0,len)
                println "queryExpression = $queryExpression"
                len = queryExpression.length() - 1
                continue
            }
            else {
                String queryValue = getOperatorOrOperand(c,queryExpression)
                println "queryValue = $queryValue"

                queryExpression = queryExpression.substring(0,(len - (queryValue.length() - 1)))
                println "queryExpression = $queryExpression"

                len = queryExpression.length() - 1

                if(queryValue.equals("AND") || queryValue.equals("OR") ||
                        queryValue.equals("TO") || queryValue.equals("NOT")){
                    stack.push(queryValue)
                }
                else{
                    prefix = queryValue + " " + prefix
                }
            }
            println "prefix = $prefix"
        }
        println prefix
    }

    def getOperatorOrOperand(char c, String queryPhrase){
        String queryValue = ""
        int len = queryPhrase.length() - 1

        println "len = $len"

        if (c == 'D'){
            if (queryPhrase.charAt(len - 1) == 'N'){
                if (queryPhrase.charAt(len - 2) == 'A'){
                    if (queryPhrase.charAt(len - 3) == ' '){
                        queryValue = "AND"
                    }
                }
            }
        }
        else if(c == 'R'){
            if (queryPhrase.charAt(len - 1) == 'O'){
                if (queryPhrase.charAt(len - 2) == ' '){
                    queryValue = "OR"
                }
            }
        }
        else if(c == 'O'){
            if (queryPhrase.charAt(len - 1) == 'T'){
                if (queryPhrase.charAt(len - 2) == ' '){
                    queryValue = "TO"
                }
            }
        }
        else if (c == 'T'){
            if (queryPhrase.charAt(len - 1) == 'O'){
                if (queryPhrase.charAt(len - 2) == 'N'){
                    if (queryPhrase.charAt(len - 3) == ' '){
                        queryValue = "NOT"
                    }
                }
            }
        }
        else {
            int index = len
            char charValue = queryPhrase.charAt(index)
            while (charValue != '[' && charValue != ' ') {
                println "charValue = $charValue"
                println ""
                queryValue = queryPhrase.charAt(index--).toString() + queryValue
                charValue = queryPhrase.charAt(index)
            }
        }
        return queryValue.trim()
    }
}
