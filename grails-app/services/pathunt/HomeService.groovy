package pathunt

import grails.transaction.Transactional
import org.apache.commons.lang.StringUtils

@Transactional
class HomeService {
    //[TA:((virus infection OR viral) AND (separation OR clearance))] AND [PBD:(19700101 TO 20170205)]
    //[[TA:(virus) OR TA:(viral)] AND [TA:(separation) OR TA:(clearance)]]
    //[[[TA:(virus) OR TA:(viral)] AND [TA:(separation) OR TA:(clearance)]] AND [PBD:(19170101_TO_20170202)]]

    def translate(String custQuery) {

        ArrayList<String> operands = new ArrayList<String>()
        String finalExpression = ""
        int index = 0, index1 = 0

        while(index < custQuery.length()){
            char c = custQuery.charAt(index)
            if(c == ']'){
                String operand = custQuery.substring(index1,index + 1)
                operands.add(operand)
                index = index1 = index + 1
            }
            else {
                index++
            }
        }

        for(String expression : operands){
            String operand = " "
            int charIndex = expression.indexOf(":")
            int i = charIndex
            //println "charIndex = $charIndex"
            //println "i = $i"

            while (expression.charAt(i) != ' ' && expression.charAt(i) != '[') {

                operand = expression.charAt(i).toString() + operand
                //println "operand i = $operand"
                i--
                //println "i change = $i"
            }
            operand = operand.trim()

            //replace the brackets
            expression = expression.replace(operand,"");
            expression = expression.replace("((", "[(");
            expression = expression.replace("))", ")]");

            int bracketIndex = expression.indexOf('(')
            int len = expression.length()
            expression = expression.substring(0,bracketIndex) + operand +
                    expression.substring(bracketIndex,len)


            int j = 0
            boolean closeBracket = false
            while (j < expression.length()){
                //System.out.println("expression = " + expression);
                //System.out.println("expression l = " + expression.length());
                // System.out.println("j = " + j);
                // System.out.println(" ");
                char c = expression.charAt(j)
                if(isOperator(j,c,expression)){
                    if(j>2){
                        if(expression.charAt(j-2)==')') {
                            expression = expression.substring(0,j-1) + "]" +
                                    expression.substring(j-1,expression.length())

                            if (c == 'O') {
                                expression = (expression.substring(0, j + 4) + "[" + operand ) +
                                        expression.substring(j + 4, expression.length())


                                j = j + 2 + operand.length()

                            }

                            else if(c == 'A' || c == 'N') {
                                expression = (expression.substring(0, j + 5) + "[" + operand ) +
                                        expression.substring(j + 5, expression.length())
                                j = j + 3 + operand.length()
                            }
                        }

                        else {
                            expression = expression.substring(0,j-1) + ")" +
                                    expression.substring(j-1,expression.length())

                            if (c == 'O') {
                                expression = (expression.substring(0, j + 4) + operand + "(" ) +
                                        expression.substring(j + 4, expression.length())

                                j = j + 2 + operand.length()
                            }
                            else if(c == 'A' || c == 'N') {
                                expression = (expression.substring(0, j + 5) + operand + "(") +
                                        expression.substring(j + 5, expression.length())
                                j = j + 3 + operand.length()
                            }
                        }
                        //System.out.println("expression after = " + expression);
                    }
                    else{
                        j++
                    }

                }
                else{
                    j++
                }

            }
            finalExpression += expression
        }
        int openBracket = 0
        int closeBracket = 0
        char c
        for (int i = 0; i < finalExpression.length() ; i++) {
            c = finalExpression.charAt(i)
            if(c == '('){
                openBracket = i
                //System.out.println(openBracket)
            }
            else if(c == ')'){
                closeBracket = i
                //System.out.println(closeBracket)

                String value = finalExpression.substring(openBracket+1,closeBracket)
                //println("value = " + value)
                String newValue = value.replace(" ", "_")
                //println("newValue = " + newValue);
                finalExpression = finalExpression.replace(value,newValue)
                //println("finalExpression = " + finalExpression)
            }

        }

        //println("Final" + finalExpression)
        return finalExpression
    }


     def isOperator(int index, char c, String queryPhrase) {
        boolean operator = false
        //int len = queryPhrase.length() - 1


        if (c == 'A') {
            if (queryPhrase.charAt(index - 1) == ' ') {
                if (queryPhrase.charAt(index + 3) == ' ') {
                    operator = true
                }
            }
        }

        else if (c == 'N') {
            if (queryPhrase.charAt(index - 1) == ' ') {
                if (queryPhrase.charAt(index + 3) == ' ') {
                    operator = true
                }
            }
        } else if (c == 'O') {
            if (queryPhrase.charAt(index - 1) == ' ') {
                if (queryPhrase.charAt(index + 2) == ' ') {
                    operator = true
                }
            }
        }
        return operator
    }


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
