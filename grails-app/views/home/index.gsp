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
		height: 70px;
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
        $(document).ready(function () {
            $('.clrBtn').hide();
            $('.data').hide();
            $('#loading').hide();
            $('.csv').attr("onclick","return false;").addClass("disabled");
            $('#sample').DataTable();
            $('#sample_length').hide();
            $('#sample_filter').hide();
            $('#sample_info').hide();
			<g:if test="${active}">
            	$('.runBtn').attr("disabled","disabled");
				var URL="${createLink(controller:'home',action:'taskComplete')}";
				$.ajax({
					url:URL,
					success: function(resp){
						console.log("hereerer " + resp);
						if(resp[0]){
                            $('.stpBtn').hide();
                            $('.clrBtn').show();
                            $('.runBtn').removeAttr("disabled","disabled");
                            $('.csv').removeAttr("onclick").removeClass("disabled");
                            $('#errorMessage').text("Click CSV to download.");
                            $('.data').show();
                            $('#count').text(resp[1]);
                            $('#duration').text(resp[2]);
                        }
					}
				});
			</g:if>
			<g:else>
            	$('.clrBtn').show();
				<g:if test="${duration}">
                    $('.data').show();
                    <g:if test="${data}">
                        $('.csv').removeAttr("onclick").removeClass("disabled");
                    </g:if>
				</g:if>
			</g:else>
        });

        function toggle() {
            location.reload(true);
        }

		function beforeSubmit() {
            $('#loading').show();
            $('#errorMessage').text("");
            $('.clrBtn').attr("onclick","return false;").addClass("disabled");
            $('.runBtn').attr("disabled","disabled");
            $('.csv').attr("onclick","return false;").addClass("disabled");
            $('.data').hide();
            $('#sampleTable').hide();
            removeMultSpace();
			if(update()){
			    $valid = validate();
			    if(!$valid){
                    $('#errorMessage').text('Syntax error in Query. Click on HELP menu in OPTIONS to view examples.');
                    $('.clrBtn').removeAttr("onclick").removeClass("disabled");
                    $('.runBtn').removeAttr("disabled");
                    $('#loading').hide();
                }
				return $valid;
			}
			else {
			    return false;
			}
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
//                console.log($errors);
				$errorMessage = $errors[1] + ' (char at ' + $errors[2].split("\n")[0] + ')';
				$('#errorMessage').text($errorMessage || '');
                $('.clrBtn').removeAttr("onclick").removeClass("disabled");
                $('.runBtn').removeAttr("disabled");
                $('#loading').hide();
                return false;
			}
		}

		function removeMultSpace() {
			$str = $('#input').val();
//            console.log('first: '+ $str);
			$str = $str.replace(/ {2,}/g,' ');
			$str = $str.replace(/\)A/g,') A');
			$str = $str.replace(/\)O/g,') O');
			$str = $str.replace(/\)N/g,') N');
			$('#input').val($str);
//            console.log('second: '+ $str);
        }
        function validate(){
            $str = $('#input').val();
            $patt = /^((([( \[])*[A-Z]+:)*(([ (\[])+([^~|(\[)\]^:\n\r])+([ )\]])+ *(AND |OR |NOT )*)+\)*)+$/;
            return $patt.test($str);
		}

		function changePassword(){
			$("#changePassword").modal("show");
		}

		function openHelp(){
		    $("#openHelp").modal("show");
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
				<div>
					<span class="navbar-text white-text">
					<g:link onclick="changePassword();return false;">Change Password</g:link>
					</span>
				</div>
				<div>
					<span class = "navbar-text white-text">
					<g:link onclick="openHelp();return false;">Help</g:link>
					</span>
				</div>
				<div>
					<span class="navbar-text white-text">
					<g:link controller="logout" action="index">Logout</g:link>
					</span>
				</div>
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
		<div class="col-md-1" id="loading" style="width: 30px; height: 30px;">
			<asset:image src="loading.gif"/>
		</div>
        <div class="col-md-1 data">
            <export:formats formats="['csv']" />
            %{--<g:hiddenField name="active" id="active" value="${active}"/>
            <g:hiddenField name="first" id="first" value="0"/>--}%
        </div>
        <div class="col-md-3 data" style="margin-top: 10px;">
            <span> Total No. of Patents: <span id="count">${data}</span></span>
        </div>
		<div class="col-md-3 data" style="margin-top: 10px;">
			<span> Time taken: <span id="duration">${duration}</span></span>
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
			<g:submitButton name="run" class="btn btn-teal accent-1 btn-sm runBtn" value="Run" style="font-size: 18px;"/>
			<g:if test="${active}">
				<g:link controller="home" class="btn btn-teal accent-1 btn-sm stpBtn" action="stop" style="font-size: 18px;"> Stop </g:link>
			</g:if>
			<g:link controller="home" class="btn btn-teal accent-1 btn-sm clrBtn" action="clear" style="font-size: 18px;"> Clear </g:link>
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

<div class="modal fade" id="openHelp" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title text-center">Help</h4>
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			</div>
			<div class="modal-body">
				<asset:image src="help.png" style="width: 100%;"/>
				<span>Example</span>
				<ol>
					<li>TTL:(virus)</li>
					<li>((UPC:(435/6.12 OR 435/455) AND CPC:(C07K2319/81)) AND PBD:[19700101 TO 20170630])</li>
					<li>(TA:((select OR screen AND detect OR classify) NOT (protein)) AND PBD:(20020115))</li>
				</ol>
			</div>
		</div>
	</div>
</div>


<g:if test="${sample}">
	<div class="container-fluid">
		<div id="sampleTable" style="width:80%; margin: 0 auto;">
			<h3 style="text-align: center;">Preview</h3>
			<table id="sample" border="1px" class="display responsive no-wrap " style="width:100%">
				<thead>
				<tr>
					<th>Publication Number</th>
					<th>Year</th>
					<th>Date</th>
					<th>Assignee</th>
					<th>Inventor</th>
					<th>All_IPC(s)</th>
					<th>All_UPC(s)</th>
					<th>All_CPC(s)</th>
					<th>Cited By</th>
					<th>Cites Count</th>
					<th>Title</th>
					<th>Abstract</th>
					<th>First Claim</th>
				</tr>
				</thead>
				<tbody>
				<g:each in="${sample}" var="row" status="i">
					<tr>
						<td>${row.patent_number}</td>
						<td>${row.year}</td>
						<td>${row.date}</td>
						<td>
							<span class="viewData">${row.assignee.length() > 20 ?  (row.assignee.indexOf('|') != -1 ? row.assignee.substring(0, row.assignee.indexOf('|')) : row.assignee.substring(0, 20)) : row.assignee}</span>
							<span class="more"> more... </span>
							<span class="expanding"> ${row.assignee.length() > 20 ? (row.assignee.indexOf('|') != -1 ? row.assignee.substring(row.assignee.indexOf('|'), row.assignee.length()) : row.assignee.substring(20, row.assignee.length())) : " "} </span>
						</td>
						<td>
							<span class="viewData">${row.inventor.length() > 20 ?  (row.inventor.indexOf('|') != -1 ? row.inventor.substring(0, row.inventor.indexOf('|')) : row.inventor.substring(0, 20)) : row.inventor}</span>
							<span class="more"> more... </span>
							<span class="expanding"> ${row.inventor.length() > 20 ? (row.inventor.indexOf('|') != -1 ? row.inventor.substring(row.inventor.indexOf('|'), row.inventor.length()) : row.inventor.substring(20, row.inventor.length())) : " "} </span>
						</td>
						<td>
							<span class="viewData">${row.ipc.length() > 20 ?  (row.ipc.indexOf(' ') != -1 ? row.ipc.substring(0, row.ipc.indexOf(' ')) : row.ipc.substring(0, 20)) : row.ipc}</span>
							<span class="more"> more... </span>
							<span class="expanding"> ${row.ipc.length() > 20 ? (row.ipc.indexOf(' ') != -1 ? row.ipc.substring(row.ipc.indexOf(' '), row.ipc.length()) : row.ipc.substring(20, row.ipc.length())) : " "} </span>
						</td>
						<td>
							<span class="viewData">${row.upc.length() > 20 ?  (row.upc.indexOf(' ') != -1 ? row.upc.substring(0, row.upc.indexOf(' ')) : row.upc.substring(0, 20)) : row.upc}</span>
							<span class="more"> more... </span>
							<span class="expanding"> ${row.upc.length() > 20 ? (row.upc.indexOf(' ') != -1 ? row.upc.substring(row.upc.indexOf(' '), row.upc.length()) : row.upc.substring(20, row.upc.length())) : " "} </span>
						</td>
						<td>
							<span class="viewData">${row.cpc.length() > 20 ?  (row.cpc.indexOf(' ') != -1 ? row.cpc.substring(0, row.cpc.indexOf(' ')) : row.cpc.substring(0, 20)) : row.cpc}</span>
							<span class="more"> more... </span>
							<span class="expanding"> ${row.cpc.length() > 20 ? (row.cpc.indexOf(' ') != -1 ? row.cpc.substring(row.cpc.indexOf(' '), row.cpc.length()) : row.cpc.substring(20, row.cpc.length())) : " "} </span>
						</td>
						<td>${row.citedby3}</td>
						<td>${row.cites}</td>
						<td>${row.title}</td>
						<td>${row.abs}</td>
						<td>${row.first_claim}</td>
					</tr>
				</g:each>
				</tbody>
			</table>
		</div>
	</div>
</g:if>
<script>
	$(".more").toggle(function(){
		$(this).text("less..").siblings(".complete").show();
		$(".viewData").css("display","none");
	}, function(){
		$(this).text("more..").siblings(".complete").hide();
	});
</script>
<br>
</body>
</html>
