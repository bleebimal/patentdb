<html>
<head>
	<meta name='layout' content='main'/>
	<title><g:message code="springSecurity.login.title"/></title>
	<style>
		.loginBox{
			position: relative;
			top:200px;
		}
	</style>
</head>

<body style="background-image: url('https://www.hdwallpapers.in/walls/abstract_color_background_picture_8016-wide.jpg');background-size: cover;">
<div class="container">
	<div class="row loginBox">
		<div class="col-md-2"> </div>
		<div class="col-md-8">
			<form action="${postUrl}" method="POST" autocomplete="off">
				<p class="h5 text-center mb-4">Login</p>
				<div class="md-form">
					<i class="fa fa-user prefix grey-text"></i>
					<input type='text' class='form-control' name='j_username' placeholder="Username" id='username'/>
				</div>
				<div class="md-form">
					<i class="fa fa-user prefix grey-text"></i>
					<input type='password' class='text_' name='j_password' id='password' placeholder="Password"/>
				</div>
				<div class="text-center">
					<button class="btn btn-blue">Send <i class="fa fa-paper-plane-o"></i></button>
				</div>
			</form>
		</div>
		<div class="col-md-2"> </div>
	</div>
</div>
<script type='text/javascript'>
	(function() {
		document.forms['loginForm'].elements['j_username'].focus();
	})();
</script>
</body>
</html>
