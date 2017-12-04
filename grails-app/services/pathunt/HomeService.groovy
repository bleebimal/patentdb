package pathunt

import grails.transaction.Transactional
import org.apache.commons.lang.StringUtils

@Transactional
class HomeService {
    //[[TA:(virus) OR TA:(viral)] AND [TA:(separation) OR TA:(clearance)]]
    //[[[TA:(virus) OR TA:(viral)] AND [TA:(separation) OR TA:(clearance)]] AND [PBD:(19170101_TO_20170202)]]

    def prefixConverter(String queryExpression){
        Stack stack = new Stack()
        String prefix = ""
        int len = queryExpression.length() - 1

        while(len >= 0){
            // println "len = $len"
            char c = queryExpression.charAt(len)
            // println "c = $c"

            if(c == ' '){
                queryExpression = queryExpression.substring(0,len)
                // println "queryExpression = $queryExpression"
                len = queryExpression.length() - 1
            }
            else if(c == '['){
                if (!stack.isEmpty()){
                    prefix = ((String)stack.pop()) + " " + prefix
                }
                queryExpression = queryExpression.substring(0,len)
                // println "queryExpression = $queryExpression"
                len = queryExpression.length() - 1
            }
            else if(c == ']'){
                queryExpression = queryExpression.substring(0,len)
                // println "queryExpression = $queryExpression"
                len = queryExpression.length() - 1
            }
            else {
                String queryValue = getOperatorOrOperand(c,queryExpression)
                // println "queryValue = $queryValue"

                queryExpression = queryExpression.substring(0,(len - (queryValue.length() - 1)))
                // println "queryExpression = $queryExpression"

                len = queryExpression.length() - 1

                if(queryValue == "AND" || queryValue == "OR" ||
                        queryValue == "TO" || queryValue == "NOT"){
                    stack.push(queryValue)
                }
                else{
                    prefix = queryValue + " " + prefix
                }
            }
            // println "prefix = $prefix"
        }
        return prefix
    }

    def getOperatorOrOperand(char c, String queryPhrase){
        String queryValue = ""
        int len = queryPhrase.length() - 1

        // println "len = $len"

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
            while (charValue != '[' && charValue != ' ' && index >= 0) {
//                 println "charValue = $charValue"
//                 println ""
                queryValue = queryPhrase.charAt(index--).toString() + queryValue
                if (index >= 0){
                    charValue = queryPhrase.charAt(index)
                }
            }
        }
        return queryValue.trim()
    }
}
