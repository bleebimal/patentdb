<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
	</head>
	<body>
	<span style="color:#000000;">Welcome ${currentUser}</span>
		<g:form controller="home" action="parser">
			<g:textArea name="query" required="" value="$sqlQuery"/>
			<br/>
			<g:submitButton name="run" value="Run"/>
		</g:form>
		<g:link controller="logout" action="index">Logout</g:link>
	</body>
</html>
