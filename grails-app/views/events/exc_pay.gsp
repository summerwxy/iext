<%@ defaultCodec="none" %>  
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>iwill 爱维尔</title>
    <!-- Bootstrap Core CSS - Uses Bootswatch Flatly Theme: http://bootswatch.com/flatly/ -->
    <link href="${resource(dir: '/bower_components/startbootstrap-freelancer/vendor/bootstrap/css', file: 'bootstrap.min.css')}" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="${resource(dir: '/bower_components/startbootstrap-freelancer/css', file: 'freelancer.min.css')}" rel="stylesheet">
    <!-- Custom Fonts -->
    <link href="${resource(dir: '/bower_components/startbootstrap-freelancer/vendor/font-awesome/css', file: 'font-awesome.min.css')}" rel="stylesheet">
    <!-- jQuery -->
    <script src="${resource(dir: '/bower_components/startbootstrap-freelancer/vendor/jquery', file: 'jquery.min.js')}"></script>
    <!-- Bootstrap Core JavaScript -->
    <script src="${resource(dir: '/bower_components/startbootstrap-freelancer/vendor/bootstrap/js', file: 'bootstrap.min.js')}"></script>

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
                        $('#waiting').addClass('hide');
                        $('#pay_okay').removeClass('hide');
                    } else if (res.err_msg == 'get_brand_wcpay_request:fail') {
                        var msg = '支付出現錯誤, get_brand_wcpay_request:fail';
                        msg += ' - appId: ' + x_json.appId;
                        msg += ' - timeStamp: ' + x_json.timeStamp;
                        msg += ' - nonceStr: ' + x_json.nonceStr;
                        msg += ' - package: ' + x_json.package;
                        msg += ' - signType: ' + x_json.signType;
                        msg += ' - paySign: ' + x_json.paySign;
                        alert(msg);
                    } else if (res.err_msg == 'get_brand_wcpay_request:cancel') { // 用戶取消, 系統繁忙
                        window.close();
                    } else {
                        alert(res.err_msg);
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

        <section id="waiting">
            <div class="container">
                <div class="row">
                    <div class="col-lg-12 text-center">
                        正在支付，请稍等！
                    </div>
                </div>
            </div>
        </section>

        <section id="pay_okay" class="hide">
            <div class="container">
                <div class="row">
                    <div class="col-lg-12 text-center">
                        <h2>付款完成</h2>
                        <hr class="star-primary">
                        礼盒安排出货后, 可再次扫码查询送货进度！
                            
                    </div>
                </div>
            </div>
        </section>

</body>

</html>

