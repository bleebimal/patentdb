<%--
  Created by IntelliJ IDEA.
  User: sameer
  Date: 1/10/18
  Time: 12:17 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <r:require module="export"/>
    <r:layoutResources/>
</head>

<body>
<div class="paginateButtons">
    <g:paginate total="${Book.count()}" />
</div>
<export:formats formats="['csv']" />
<r:layoutResources/>
</body>
</html>