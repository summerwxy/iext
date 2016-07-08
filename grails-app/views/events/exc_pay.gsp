<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>iwill 爱维尔</title>
    <!-- Bootstrap Core CSS - Uses Bootswatch Flatly Theme: http://bootswatch.com/flatly/ -->
    <link href="/imis/bower_components/startbootstrap-freelancer/css/bootstrap.min.css" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="/imis/bower_components/startbootstrap-freelancer/css/freelancer.css" rel="stylesheet">
    <!-- Custom Fonts -->
    <link href="/imis/bower_components/startbootstrap-freelancer/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <!-- jQuery -->
    <script src="/imis/bower_components/startbootstrap-freelancer/js/jquery.js"></script>
    <!-- Bootstrap Core JavaScript -->
    <script src="/imis/bower_components/startbootstrap-freelancer/js/bootstrap.min.js"></script>

    <script type="text/javascript">
    $(function() {
        //json 数据
        var x_json = ${json};
        function onBridgeReady(){
            WeixinJSBridge.invoke(
               'getBrandWCPayRequest', x_json ,
                function(res){     
                    // 使用以下方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。 
                    if(res.err_msg == "get_brand_wcpay_request:ok" ) {
                        $('#contact').removeClass('hide');
                    }
                }
            ); 
        }
        if (typeof WeixinJSBridge == "undefined") {
            if( document.addEventListener ) {
                document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
            }else if (document.attachEvent) {
                document.attachEvent('WeixinJSBridgeReady', onBridgeReady); 
                document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
            }
        } else {
            onBridgeReady();
        }
    });
    </script>
</head>
<body id="page-top" class="index">

        <section id="contact" class="hide">
            <div class="container">
                <div class="row">
                    <div class="col-lg-12 text-center">
                        <h2>付款完成</h2>
                        <hr class="star-primary">
                        礼盒会尽快的送上门！
                    </div>
                </div>
            </div>
        </section>

</body>

</html>

