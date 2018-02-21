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
	.textAreaHeight {
        height: 300px;
    }

    .dropbtn {
        background-color: #ffffff;
        color: #0056b3;
        padding: 16px;
        font-size: 16px;
        border: none;
    }

    .dropdown {
        position: relative;
        display: inline-block;
    }

    .dropdown-content {
        display: none;
        position: absolute;
        background-color: #f1f1f1;
        min-width: 160px;
        box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
        z-index: 1;
    }

    .dropdown-content a {
        color: black;
        padding: 8px 16px;
        text-decoration: none;
        display: block;
    }

    .dropdown-content a:hover {background-color: #ddd}

    .dropdown:hover .dropdown-content {
        display: block;
    }

    .dropdown:hover .dropbtn {
        background-color: #9e9e9e;
	}
	</style>
	<script type="application/javascript">

		function beforeSubmit() {
			removeMultSpace();
			return update();
        }
        function update() {
            $input = $('#input').val();
//            console.log($input);
            try {
                balanced.matches({source: $input, open: ['{', '(', '['], close: ['}', ')', ']'], balance: true, exceptions: true});
                return true;
            } catch (error) {
                $errors = error.message;
                $errors = $errors.split(":");
                console.log($errors);
                $errorMessage = $errors[1] + ' (char at ' + $errors[2].split("\n")[0] + ')';
                $output = $('#errorMessage').text($errorMessage || '');
				return false;
            }
        }
        function removeMultSpace() {
            $str = $('#input').val();
//            console.log('first: '+ $str);
            $str = $str.replace(/ {2,}/g,' ');
            $('#input').val($str);
//            console.log('second: '+ $str);
        }
        function changePassword(){
            $("#changePassword").modal("show");
        }
        function checkMatch(){
            var npassword1=$("#newPassword").val();
            var npassword2=$("#repeatPassword").val();
            console.log(npassword1 + "..." + npassword2);
            if (npassword1!=npassword2){
                $("#errorPwd").text("Re-enter the password correctly!");
                return false;
            }else{
                $("#changePwd").submit();
            }
        }
	</script>
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

			<div class="dropdown" style="float:right;">
				<button class="dropbtn">Options <asset:image src="icon.png"/></button>

				    <div class="dropdown-content">
					    <span class="navbar-text white-text">
                            <a> <g:link onclick="changePassword();return false;">Change Password</g:link> </a>
					    </span>
					    <span class="navbar-text white-text">
						    <g:link controller="logout" action="index">Logout</g:link>
					    </span>
				    </div>
            </div>


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
		<div class="col-md-6" style="color: #B22222;" id="errorMessage">
            <g:message code="${flash.message}" args="${flash.args}"
                       default="${flash.default}"/>
		</div>
	</div>
</div>
<div class="container-fluid">
	<div class="form-group basic-textarea rounded-corners shadow-textarea">
		<g:form controller="home" action="parser" onsubmit="return beforeSubmit();">
			<g:textArea id="input" class="form-control z-depth-1 textAreaHeight" name="query" value="${sqlQuery}" placeholder="Enter Query" required="" rows="10" style="padding:4px;"/>
			<br/>
			<g:link controller="home" class="btn btn-teal accent-1 btn-sm" action="clear" style="font-size: 18px;"> Clear </g:link>
			<g:submitButton name="run" class="btn btn-teal accent-1 btn-sm" value="Run" style="font-size: 18px;"/>
		</g:form>
	</div>
</div>

<div class="modal fade" id="changePassword" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title text-center">Change Password</h4>
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			</div>
			<div class="modal-body">
				<g:form url="[controller:'home', action:'changePassword']" id="changePwd">
					<div class="row">
						<div class="col-md-12" style="color: #B22222;" id="errorPwd">
							<g:message code="${flash.message}" args="${flash.args}"
									   default="${flash.default}"/>
						</div>
					</div>
					<fieldset class="form">
						<div class="row">
							<div class="col-sm-12">
								<div class="form-group">
									<label for="currentPassword">Current Password</label>
									<g:passwordField name="currentPassword" class="form-control" required=""/>
								</div>
							</div>
						</div>

						<div class="row">
							<div class="col-sm-12">
								<div class="form-group">
									<label for="newPassword">New Password</label>
									<g:passwordField name="newPassword" class="form-control" required=""/>
								</div>
							</div>
						</div>

						<div class="row">
							<div class="col-sm-12">
								<div class="form-group">
									<label for="repeatPassword">Re-enter Password</label>
									<g:passwordField name="repeatPassword" class="form-control" required=""/>
								</div>
							</div>
						</div>
					</fieldset>
					<input type="reset" name="clear" class="btn btn-teal accent-1 btn-sm" value="Clear" style="font-size: 18px;"/>
					<g:actionSubmit class="btn btn-teal accent-1 btn-sm" name="changePwd" onclick="checkMatch();return false;" value="${message(code: 'default.button.changePassword.label', default: 'Change Password')}" style="font-size: 18px;"/>
				</g:form>
			</div>
		</div>
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
