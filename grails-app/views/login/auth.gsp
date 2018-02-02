<%--
  Created by IntelliJ IDEA.
  User: Sumit Shrestha
  Date: 1/26/2018
  Time: 5:08 PM
--%>

<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title> Login </title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
    <asset:stylesheet src="bootstrap.min.css"/>
    <asset:stylesheet src="mdb.min.css"/>
    <asset:stylesheet src="style.css"/>
    <asset:javascript src="jquery-3.2.1.min.js"/>
    <asset:javascript src="popper.min.js"/>
    <asset:javascript src="bootstrap.min.js"/>
    <asset:javascript src="mdb.js"/>
    <asset:javascript src="mdb.min.js"/>
    <asset:link rel="icon" href="deerwalk.png" type="image/png"/>
    <style>
    body{
        color:#060d13;
    }

    h1{
        color:#060d13;
        text-align:center;

    }
    .loginBox{
        position: relative;
        top:200px;
    }
    .innerLoginBox{
        background-color: #74AFAD;
    }
    .background{
        /*background-image: url("https://wallpaper.wiki/wp-content/uploads/2017/04/wallpaper.wiki-Rainning-wallpaper-hd-background-PIC-WPD0013375.jpg"); */
        background-color: #558C89;
        background-size: cover;
        height:100%;
    }
    .loginBox{

    }
    </style>
</head>

<body class="background">
<h1>PATHUNT</h1>
<div class="container">
    <div class="row loginBox">
        <div class="col-md-2"> </div>
        <div class="col-md-8 innerLoginBox">
            <form action="${postUrl}" method="POST" autocomplete="off">
                <p class="h5 text-center mb-4">Login</p>
                <div class="md-form">
                    <i class="fa fa-user prefix white-text"></i>
                    <input type='text' class='form-control white-text' name='j_username' placeholder="Username" id='username'/>
                </div>
                <div class="md-form">
                    <i class="fa fa-key prefix white-text"></i>
                    <input type='password' class='text_ white-text' name='j_password' id='password' placeholder="Password"/>
                </div>
                <div class="text-center">
                    <button class="btn btn-blue">Submit <i class="fa fa-paper-plane-o"></i></button>
                </div>
            </form>
        </div>
        <div class="col-md-2"> </div>
    </div>
</div>

</body>
</html>
