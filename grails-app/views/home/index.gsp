<!DOCTYPE html>
<html>
<head>
	<meta name="layout" content="main"/>
	<r:require module="export"/>
	<style>
	.shadow-textarea textarea.form-control::placeholder {
		font-weight: 300;
	}
	.shadow-textarea textarea.form-control {
		padding-left: 0.8rem;
	}
	.textAreaHeight{
		height:300px;
	}
	</style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark">
	<a class="navbar-brand" href="#">Welcome ${currentUser}</a>
	<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarText" aria-controls="navbarText"
			aria-expanded="false" aria-label="Toggle navigation">
		<span class="navbar-toggler-icon"></span>
	</button>
	<div class="collapse navbar-collapse" id="navbarText">
		<ul class="navbar-nav mr-auto">
			<li class="nav-item active">
			</li>
			<li class="nav-item text-center">

			</li>
			<li class="nav-item">

			</li>
		</ul>
		<span class="navbar-text white-text">
			<g:link controller="logout" action="index">Logout</g:link>
		</span>
	</div>
</nav>
%{--<g:form controller="home" action="parser">
	<g:textArea name="query" required="" /> --}%%{--value="$sqlQuery"--}%%{--
	<br/>
	<g:submitButton name="run" value="Run"/>
</g:form>--}%
<br/>
<br/>
<br/>
<div class="container-fluid">
	<div class="row">
		<div class="col-md-1">
			<g:if test="${data}">
				<export:formats formats="['csv']" />
			</g:if>
		</div>
	</div>
    <div class="row">
		<div class="col-md-4" style="color: #B22222;">
            <g:message code="${flash.message}" args="${flash.args}"
                       default="${flash.default}"/>
		</div>
	</div>
</div>
<div class="container-fluid">
	<div class="form-group basic-textarea rounded-corners shadow-textarea">
		<g:form controller="home" action="parser">
			<g:textArea class="form-control z-depth-1 textAreaHeight" name="query" value="${sqlQuery}" placeholder="Enter Query" required="" rows="10" style="padding:4px;"/>
			<br/>
			<g:submitButton name="run" class="btn btn-teal accent-1 btn-sm" value="Run" style="font-size: 18px;"/>
		</g:form>
	</div>

</div>

%{--${data.size()}
        <g:if test="${data}">
            <table border="1 px" width="90%">
                <thead>
                <th>Publication Number</th>
                <th>Title</th>
                <th>Abstract</th>
                <th>Year</th>
                <th>Date</th>
                </thead>
                <tbody>
                <g:each in="${data}" var="row" status="i">
                    <tr>
                        <td>${row.publication_number}</td>
                        <td>${row.title}</td>
                        <td>${row.abstract}</td>
                        <td>${row.year}</td>
                        <td>${row.date}</td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </g:if>--}%

</body>
</html>
