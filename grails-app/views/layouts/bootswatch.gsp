<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>
        <g:layoutTitle default="untitled"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
    <!-- css section -->
    <link rel="stylesheet" href="${resource(dir: '/bower_components/bootstrap/dist/css', file: 'bootstrap.min.css')}" media="screen"> 
    <link rel="stylesheet" href="${resource(dir: '/bower_components/bootswatch/flatly', file: 'bootstrap.min.css')}" media="screen"> 
    <asset:stylesheet src="bootswatch.css" media="screen"/>

    <!-- TODO: install html5shiv and respond -->
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="../bower_components/html5shiv/dist/html5shiv.js"></script>
      <script src="../bower_components/respond/dest/respond.min.js"></script>
    <![endif]-->
    <!-- js section -->
    <script type="text/javascript" src="${resource(dir: '/bower_components/jquery/dist', file: 'jquery.min.js')}"></script>
    <script type="text/javascript" src="${resource(dir: '/bower_components/bootstrap/dist/js', file: 'bootstrap.min.js')}"></script>
    <asset:javascript src="bootswatch.js" />
    <g:layoutHead/>
  </head>
  <body>
    <g:layoutBody/>
  </body>
</html>

