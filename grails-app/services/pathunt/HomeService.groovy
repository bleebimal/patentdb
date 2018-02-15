package pathunt

import grails.transaction.Transactional
import org.apache.commons.lang.StringUtils

@Transactional
class HomeService {
    //[TA:((virus OR viral) AND (separation OR clearance))] AND [IPC:(H01L41/00)] AND [AN:(halala)]
    //[[TA:(virus) OR TA:(viral)] AND [TA:(separation) OR TA:(clearance)]]
    //(((TA:|virus|) OR (TA:|viral|)) AND ((TA:|separation|) OR (TA:|clearance|)))
    //[[[TA:(virus) OR TA:(viral)] AND [TA:(separation) OR TA:(clearance)]] AND [PBD:(19170101_TO_20170202)]]
    private List<String> operands = new ArrayList<>();
    private List<String> operandExpressions = new ArrayList<>();

    def translate(String custQuery) {
//        println "custQuery = $custQuery"
        custQuery = custQuery.replace("( ","(")
                             .replace("[ ","[")
//                             .replace("(  ","(")
//                             .replace("(   ","(")
                             .replace(" )",")")
                             .replace(" ]","]")
//                             .replace("  )",")")
//                             .replace("   )",")")
//        println "custQuery = $custQuery"
        processBrackets(custQuery);
//        StringBuilder val = new StringBuilder();
//        for (String st: operandExpressions){
//            println("st = " + st)
//            val.append(" ").append(st)
//        }
//        println("val after processBracket = " + val);
        String separatedQuery = preProcessForPrefix();
//        println("val after prefixPreProcess = " + separatedQuery);
        String prefix = prefixConverter(separatedQuery)
        operands.clear();
        operandExpressions.clear();
//        println("val after prefix= " + prefix);
        return prefix.trim();
    }

    private String replaceSpace(String expression){
        int openBracket = 0;
        int closeBracket;
        char c;
        for (int i = 0; i < expression.length() ; i++) {

            c = expression.charAt(i);
            if(c == '~'){
                openBracket = i;
                //println(openBracket);
            }
            else if(c == '|'){
                closeBracket = i;
                //println(closeBracket);

                String value = expression.substring(openBracket+1,closeBracket);
                //println("value = " + value);
                String newValue = value.replace(" ", "^");
                //println("newValue = " + newValue);
                expression = expression.replace(value, newValue);
                //println("finalExpression = " + finalExpression);
            }

        }
//        println("Final " + expression);
        return expression;
    }

    private boolean isOperator(int index, char c, String queryPhrase) {
        boolean operator = false;
        int len = queryPhrase.length() - 1;


        if (c == 'A') {
//            System.out.println("index = " + index);
            try {
                if (index  + 3 <= len){
                    if ((queryPhrase.charAt(index + 2) == 'D') && (queryPhrase.charAt(index + 3) == ' ')) {
                        if (queryPhrase.charAt(index - 1) == ' ') {
                            operator = true;
                        }
                    }
                }
                else {
                    if ((queryPhrase.charAt(index + 1) == 'N') && (queryPhrase.charAt(index + 2) == 'D')) {
                        if (queryPhrase.charAt(index - 1) == ' ') {
                            operator = true;
                        }
                    }
                }

            }catch (StringIndexOutOfBoundsException e){
                operator = false;
            }
        }

        else if (c == 'N') {
            try {
                if (index  + 3 <= len){
                    if ((queryPhrase.charAt(index + 2) == 'T') && (queryPhrase.charAt(index + 3) == ' ')) {
                        if (queryPhrase.charAt(index - 1) == ' ') {
                            operator = true;
                        }
                    }
                }
                else {
                    if ((queryPhrase.charAt(index + 1) == 'O') && (queryPhrase.charAt(index + 2) == 'T')) {
                        if (queryPhrase.charAt(index - 1) == ' ') {
                            operator = true;
                        }
                    }
                }
            }catch (StringIndexOutOfBoundsException e){
                operator = false;
            }
        } else if (c == 'O') {
            try {
                if (index  + 2 <= len){
                    if ((queryPhrase.charAt(index + 1) == 'R') && (queryPhrase.charAt(index + 2) == ' ')) {
                        if (queryPhrase.charAt(index - 1) == ' ') {
                            operator = true;
                        }
                    }
                }
                else {
                    if (queryPhrase.charAt(index + 1) == 'R') {
                        if (queryPhrase.charAt(index - 1) == ' ') {
                            operator = true;
                        }
                    }
                }
            }catch (StringIndexOutOfBoundsException e){
                operator = false;
            }
        }
//        System.out.println("operator = " + operator);
        return operator;
    }

    private void processBrackets(String preQuery){
        int index = 0, index1, operandIndex, lastBracketChanged = 0;
        boolean bracketClosed = false;
        int len = preQuery.length();

        StringBuilder operandExpression = new StringBuilder();
        StringBuilder operand = new StringBuilder();

        while (index < len){
            //println("index = " + index);
            char c = preQuery.charAt(index);
            //println("c = " + c);
            operandExpression.setLength(0);
            if (c == ':'){
                operandIndex = index;
                while (c!= ' ' && operandIndex >= 0){
                    c = preQuery.charAt(operandIndex);
                    operandExpression.insert(0,c);
                    //println("operandExpression = " + operandExpression);
                    operandIndex--;
                    if (c!= '(' && c!=' '){
                        operand.insert(0,c);
                    }
                }
                //println("operand = " + operand);
                operands.add(operand.toString());

                index1 = index +1;
                c = preQuery.charAt(index1);

                while (index1 < len && (c != ':')){
                    c = preQuery.charAt(index1);
                    //println("index1 = " + index1);
                    char c1;
                    if ((c == ')') || (c == ']')){
                        bracketClosed = true;
                        //println(") before= " + c);
                        c1 =preQuery.charAt(index1 - 1);
                        //println(") after= " + c);
                        if ((c1 != ')') && (c1 != '|')){
                            operandExpression.append("|");
                            //println("operandExpression = " + operandExpression);
                            lastBracketChanged = index1;
                            preQuery = preQuery.substring(0, index1) + "|)" + preQuery.substring(index1 +1, len);
                            len = preQuery.length();
                            //println("preQuery = " + preQuery);
                        }
                        else {
                            operandExpression.append(')');
                            //println("operandExpression = " + operandExpression);
                        }
                        //println("preQuery = " + preQuery);
                    }
                    else if ((c == '(') || (c == '[')){
                        bracketClosed = false;
                        //println("( before= " + c);
                        c1 = preQuery.charAt(index1 + 1);
                        //println("( after= " + c);
                        if (c1 != '(' && c1 != '~'){
                            operandExpression.append("(~");
                            //println("operandExpression = " + operandExpression);
                            lastBracketChanged = index1;
                            preQuery = preQuery.substring(0, index1) + "(~" + preQuery.substring(index1 + 1, len);
                            len = preQuery.length();
                            index1++;
                            //println("preQuery = " + preQuery);
                        }
                        else {
                            operandExpression.append('(');
                            //println("operandExpression = " + operandExpression);
                        }
                        //println("preQuery = " + preQuery);
                    }
                    else {
                        operandExpression.append(c);
                        //println("operandExpression = " + operandExpression);
                    }
                    index1++;
                }
                //println("bracketClosed = " + bracketClosed);
                if (!bracketClosed){
                    //println("lastBracketChanged = " + lastBracketChanged);
                    //println("preQuery.substring(0, lastBracketChanged) = " + preQuery.substring(0, lastBracketChanged));
                    //println("preQuery.substring(0, lastBracketChanged) = " + preQuery.substring(lastBracketChanged + 2, len));
                    preQuery = preQuery.substring(0, lastBracketChanged) + "(" + preQuery.substring(lastBracketChanged + 2, len);
                    len = preQuery.length();
                    //println("preQuery = " + preQuery);
                }
                if (c == ':'){
                    int expressionLen = operandExpression.length() - 1;
                    //println("expressionLen = " + expressionLen);
                    while (expressionLen > 0){
                        c = operandExpression.charAt(expressionLen);
                        //println("c = " + c);
                        if (c != ' '){
                            operandExpression.deleteCharAt(expressionLen);
                        }
                        else {
                            break;
                        }
                        expressionLen--;
                    }
                }
                index = index1 - 2;
                //println("index = " + index);
                //println("operandExpression = " + operandExpression);
                operandExpressions.add(operandExpression.toString().trim().replace(operand.toString(),""));
                operand.setLength(0);

            }else {
                index++;
            }
            //println("index = " + index);
        }
    }

    private String preProcessForPrefix(){
        StringBuilder finalExpression = new StringBuilder("");
        int operandIndex = 0;
        for(String expression : operandExpressions){
            //println(" ");
            int j = 0, operandFirstIndex = -1, operatorCount = 0, len = expression.length();
            char c,c1;
            while (j < len){
                //println("expression = " + expression);
                //println("expression l = " + len);
                //println("j = " + j);
                //println(" ");
                c = expression.charAt(j);
                if (c == '~'){
                    c1 = expression.charAt(j-1);
                    if (c1 == '('){
                        operandFirstIndex = j;
                    }
                }
                //println("c = " + c);
                //println("operandFirstIndex = " + operandFirstIndex);
                if(isOperator(j,c,expression)){
                    operatorCount++;
                    //println("operatorCount = " + operatorCount);
                    if(j>2){
                        c1 = expression.charAt(j-2);
                        //println("c1 = " + c1);
                        if(c1 == ')') {
                            if (c == 'O') {
                                j += 3;
                            }

                            else if(c == 'A' || c == 'N') {
                                j += 4;
                            }
                        }

                        else {
//                            println("expression.substring(0,j-1) = " + expression.substring(0,j-1));
//                            println("expression.substring(j-1,len) = " + expression.substring(j-1,len));
//                            System.out.println("operatorCount = " + operatorCount);
                            if (operatorCount > 1){
                                expression = expression.substring(0,operandFirstIndex).trim() + "(" +
                                        expression.substring(operandFirstIndex,j-1).trim() + "|) " +
                                        expression.substring(j-1,len).trim();
                                len = expression.length();
                                j+=2;
                            }
                            else {
                                expression = expression.substring(0,j-1).trim() + "| " +
                                        expression.substring(j-1,len).trim();
                                len = expression.length();
                            }
                            //println("expression = " + expression);
                            if (c == 'O') {
//                                println("expression.substring(0, j +4) = " + expression.substring(0, j + 4));
//                                println("expression.substring(j +4, len) = " + expression.substring(j +4, len));
                                expression = (expression.substring(0, j + 4).trim() + " ~" ) +
                                        expression.substring(j + 4, len).trim();
                                //println("expression = " + expression);
                                len = expression.length();
                                j += 5;
                            }
                            else if(c == 'A' || c == 'N') {
//                                println("expression.substring(0, j+5) = " + expression.substring(0, j));
//                                println("expression.substring(j +5, len) = " + expression.substring(j, len));
                                expression = (expression.substring(0, j + 5) + " ~") +
                                        expression.substring(j + 5, len).trim();
                                //println("expression = " + expression);
                                len = expression.length();
                                j += 5;
                            }
                        }
                    }
                    else{
                        j++;
                    }

                }
                else{
                    j++;
                }

            }
            expression = replaceSpace(expression);

            expression = expression.replace("~",(operands.get(operandIndex) + "~"));
            finalExpression.append(expression.trim()).append(" ");
            operandIndex++;
        }


        return finalExpression.toString().trim();
    }

    private String prefixConverter(String queryExpression){
        Stack<String> operatorStack = new Stack<String>();
        Stack<String> bracesStack = new Stack<String>();
        StringBuilder prefix = new StringBuilder();
        int len = queryExpression.length() - 1;

        while(len >= 0){
            //println("len = " + len);
            char c = queryExpression.charAt(len);
            //println("c = " + c);

            if(c == ' '){
                queryExpression = queryExpression.substring(0,len);
                //println("queryExpression = " + queryExpression);
                len = queryExpression.length() - 1;
            }
            else if(c == '('){
                bracesStack.push("(");
                if (!operatorStack.isEmpty()){
                    while ((!operatorStack.isEmpty()) && (operatorStack.peek() != ")")){
                        prefix.insert(0, (operatorStack.pop()) + " ");
                    }
                    operatorStack.pop();
                }
                queryExpression = queryExpression.substring(0,len);
                //println("queryExpression = " + queryExpression);
                len = queryExpression.length() - 1;
            }
            else if(c == ')'){
                operatorStack.push(")");
                bracesStack.push(")");
                queryExpression = queryExpression.substring(0,len);
                //println("queryExpression = " + queryExpression);
                len = queryExpression.length() - 1;
            }
            else {
                String queryValue = getOperatorOrOperand(c,queryExpression);
                //println("queryValue = " + queryValue);

                queryExpression = queryExpression.substring(0,(len - (queryValue.length() - 1)));
                //println("queryExpression = " + queryExpression);
                len = queryExpression.length() - 1;

                if(queryValue == "AND" || queryValue == "OR" ||
                        queryValue == "TO" || queryValue == "NOT"){
                    operatorStack.push(queryValue);
                }
                else{
                    prefix.insert(0, queryValue + " ");
                }
            }
//            print("operatorStack ");
//            for (int i = 0; i <operatorStack.size() ; i++) {
//                print("\t" + operatorStack.get(i));
//            }
            //println("");
            //println("Ostack = " + operatorStack.size());
            //println("Bstack = " + bracesStack.size());
            //println("prefix = " + prefix);
        }
        while (!operatorStack.empty()){
            if (operatorStack.peek() != ")"){
                prefix.insert(0, (operatorStack.pop()) + " ");
            }
            else {
                operatorStack.pop();
            }
        }
        //println("Ostack = " + operatorStack.size());
        //println("Bstack = " + bracesStack.size());
        //println("final prefix = " + prefix);
        return prefix.toString();
    }

    private String getOperatorOrOperand(char c, String queryPhrase){
        String queryValue = "";
        int len = queryPhrase.length() - 1;

        //println("len = " + len);

        if (c == 'D'){
            if (queryPhrase.charAt(len - 1) == 'N'){
                if (queryPhrase.charAt(len - 2) == 'A'){
                    if (queryPhrase.charAt(len - 3) == ' '){
                        queryValue = "AND";
                    }
                }
            }
        }
        else if(c == 'R'){
            if (queryPhrase.charAt(len - 1) == 'O'){
                if (queryPhrase.charAt(len - 2) == ' '){
                    queryValue = "OR";
                }
            }
        }
        else if (c == 'T'){
            if (queryPhrase.charAt(len - 1) == 'O'){
                if (queryPhrase.charAt(len - 2) == 'N'){
                    if (queryPhrase.charAt(len - 3) == ' '){
                        queryValue = "NOT";
                    }
                }
            }
        }
        else {
            int index = len;
            char charValue = queryPhrase.charAt(index);
            while (charValue != '(' && charValue != ' ' && index >= 0) {
                //println("charValue = " + charValue);
                //println("");
                queryValue = queryPhrase.charAt(index--).toString() + queryValue;
                if (index >= 0){
                    charValue = queryPhrase.charAt(index);
                }
            }
        }
        return queryValue.trim();
    }
}
