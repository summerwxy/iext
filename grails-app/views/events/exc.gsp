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
        var fee = ${fee};

        $('#addBox').on('click', function() {
            if ($('[name=vid]').size() >= 5) {
                alert('您好！一次提货最多５张礼券哦！');
                return;
            }
            $('#checkIcon').removeClass('hide');
            $('#addSection').removeClass('hide');
            $('#addBox').addClass('hide');
        });
        $('#checkIcon').on('click', function() {
            var tno = $('[name=tno]');
            var vcode = $('[name=vcode]');
            var hid = ${h.id};
            $(this).addClass('hide');
            $.ajax({
                url: 'exc_check',
                type: 'get',
                data: {hid: hid, tno: tno.val(), vcode: vcode.val()}, 
                dataType: 'json'
            }).done(function(json) {
                if (!json.msg) {
                    alert('添加成功！');
                    var foo = $('.rows:eq(0)').clone();
                    $(foo.find('.name')).text(json.name);
                    $(foo.find('.tno')).text(json.tno);
                    $(foo.find('.vcode')).text(json.vcode);
                    $(foo.find('[name=vid]')).val(json.vid);
                    $(foo.find('[name=weight]')).val(json.weight);
                    $('#addSection').before(foo);
                    calcFee(); // 算运费
                } else {
                    alert(json.msg);
                }
                tno.val('');
                vcode.val('');
                $('#addSection').addClass('hide');
                $('#addBox').removeClass('hide');
            }).fail(function(json) {
                alert('AJAX FAIL!');    
            });   
        });
        $('section').on('click', '.delIcon', function() {
            if ($('[name=vid]').size() == -1) { // 某些場景需要可以刪除, 先不做這個限制
                alert('最后一盒礼盒不可删除！');
            } else if (confirm('是否删除？')) {
                var temp = $(this)
                temp.addClass('hide');
                var vid = $(this).parent().find('[name=vid]').val();
                $.ajax({
                    url: 'exc_del',
                    type: 'get',
                    data: {vid: vid}, 
                    dataType: 'json'
                }).done(function(json) {
                    temp.parent().parent().parent().remove();
                    calcFee(); // 算运费
                }).fail(function(json) {
                    alert('AJAX FAIL!');    
                });   
            }
        });

        $('#lv1').on('change', function() {
            calcFee();
            $('#lv2 option:gt(0)').remove();
            $('#lv3 option:gt(0)').remove();
            $.ajax({
                url: 'exc_zone',
                type: 'get',
                data: {plv: 'lv1', clv: 'lv2', val: this.value}, 
                dataType: 'json'
            }).done(function(json) {

                console.log(json)

                $(json).each(function(i, it) {
                    $('#lv2').append('<option>' + it.name + '</option>')
                });
            }).fail(function(json) {
                alert('AJAX FAIL!');    
            });       
        });

        $('#lv2').on('change', function() {
            $('#lv3 option:gt(0)').remove();
            $.ajax({
                url: 'exc_zone',
                type: 'get',
                data: {plv: 'lv2', clv: 'lv3', val: this.value}, 
                dataType: 'json'
            }).done(function(json) {
                $(json).each(function(i, it) {
                    $('#lv3').append('<option>' + it.name + '</option>')
                });
            }).fail(function(json) {
                alert('AJAX FAIL!');    
            });       
        });

        function calcFee() {
            var msg = '';
            var w = $('[name=weight]');
            var total = 500; // 外箱重量
            w.each(function(i, it) {
                total += parseInt(it.value, 10);
            });
            var showW = Math.round(total / 100) / 10;
            var feeW = Math.ceil(total / 1000);
            // TODO: 显示价格 按钮控制
            msg += '共 ' + showW + ' 公斤';
            var lv1 = $('#lv1').val();
            if (lv1.length == 0) {
                msg += '。';
                $('#kg').val("0");
                $('#fee').val("0");
            } else {
                var f = fee[lv1]['first'];
                var a = fee[lv1]['additional'];
                var m = f + (feeW - 1) * a;
                msg += '，提货服务费 ' + m + ' 元。'
                $('#kg').val(showW);
                $('#fee').val(m);
            }
            $('#infoFee').text(msg);
        }

        $('#payForm').on('submit', function() {
            var msg = new Array();
            if ($('#name').val().length == 0) {
                msg.push('请填写收件人！');
            }
            if ($('#phone').val().length == 0) {
                msg.push('请填写联系电话！');
            }
            if ($('#lv1').val().length == 0) {
                msg.push('请选择【省】！');
            }
            if ($('#lv2').val().length == 0) {
                msg.push('请选择【市】！');
            }
            if ($('#lv3').val().length == 0) {
                msg.push('请选择【区】！');
            }
            if ($('#address').val().length == 0) {
                msg.push('请填写收货地址！');
            }
            if ($('[name=vid]').size() == 0) {
                msg.push('请添加礼盒！');
            }
            if (msg.length > 0) {
                alert(msg.join("\r\n"));
                return false;
            }
        });

        // onload 算一次
        calcFee();
        // block ui when ajax request
        $.blockUI.defaults.message = '处理中...';
        $(document).ajaxStart($.blockUI).ajaxStop($.unblockUI);

    });
    </script>
</head>

<body id="page-top" class="index">
    <!-- Header -->
    <g:if test="${h.status in ['']}">
    <header>
        <div class="container" style="padding-top: 60px; padding-bottom: 60px;">
            <div class="row">
                <asset:image src="events/exc_top.jpg" class="img-responsive" style="margin-bottom: 0px;" alt=""/>
            </div>
        </div>
    </header>
    </g:if>

    <g:if test="${h.status != 'paid' && !isThisActno}">
        
        <div style="text-align: center;">
            <h1>活动结束！</h1> 
            还有未提货的礼券请致电服务热线：0512-65687148 询问        
        </div>
        <br/><br/>

    </g:if>
    <g:elseif test="${h.status != 'paid'}">
        <!-- Contact Section -->
        <section style="padding-top: 30px; padding-bottom: 10px;">
            <div class="container">
                <div class="row">
                    <div class="col-lg-12 text-center">
                        <h2>礼盒信息</h2>
                        <hr class="star-primary">
                    </div>
                </div>

                <div class="row">
                    <div class="col-lg-8 col-lg-offset-2">
                        <g:each in="${ds}" status="i" var="it">
                            <div class="rows">
                                <div class="row control-group">
                                    <div class="form-group1 col-xs-10 controls name">
                                        ${it.P_NAME}
                                    </div>
                                    <div class="form-group1 col-xs-2 controls" style="top: 0px;">
                                        <input type="hidden" name="vid" value="${it.vid}"/>
                                        <input type="hidden" name="weight" value="${it.weight}"/>
                                        <i class="fa fa-fw fa-times-circle delIcon" style="color: red;"></i>
                                    </div>
                                    <div class="form-group1 col-xs-8 controls tno">
                                        ${it.GT_NO}
                                    </div>
                                    <div class="form-group1 col-xs-4 controls vcode">
                                        ${it.VCODE}
                                    </div>
                                </div>
                                <hr style="margin-top: 0px; margin-bottom: 0px;"/>
                            </div>
                        </g:each>
                        <div id="addSection" class="row control-group hide">
                            <div class="form-group col-xs-8 controls">
                                <input type="number" class="form-control" name="tno" placeholder="提货券券号">
                            </div>
                            <div class="form-group col-xs-4 controls">
                                <input type="text" class="form-control" name="vcode" placeholder="验证码">
                            </div>
                            <div class="form-group col-xs-12 controls">
                                <button type="button" id="checkIcon" class="btn btn-success btn-block">
                                    验证
                                </button>
                            </div>
                        </div>
                        <div class="row">
                            <div class="form-group col-xs-12" style="text-align: right;">
                                <button type="button" id="addBox" class="btn btn-info btn-block">
                                    点击添加礼券
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>


        <!-- Contact Section -->
        <section style="padding-top: 30px; padding-bottom: 10px;">
            <div class="container">
                <div class="row">
                    <div class="col-lg-12 text-center">
                        <h2>收货信息</h2>
                        <hr class="star-primary">
                    </div>
                </div>

                <g:if test="${flash.msg}">
                    <div class='alert alert-info'>
                      <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>
                      <strong>${flash.msg}</strong>
                    </div>
                </g:if>
                
                <div class="row">
                    <div class="col-lg-8 col-lg-offset-2">
                        <form name="sentMessage" id="payForm" novalidate>
                            <div class="row control-group">
                                <div class="form-group col-xs-12 floating-label-form-group controls">
                                    <label>收件人</label>
                                    <input type="text" class="form-control" placeholder="例：王先生" id="name" name="name" required data-validation-required-message="请输入收件人名称" value="${h.name}">
                                    <p class="help-block text-danger"></p>
                                </div>
                            </div>
                            <div class="row control-group">
                                <div class="form-group col-xs-12 floating-label-form-group controls">
                                    <label>联系电话</label>
                                    <input type="tel" class="form-control" placeholder="例:18112345678" id="phone" name="phone" required data-validation-required-message="请输入联系电话" value="${h.phone}">
                                    <p class="help-block text-danger"></p>
                                </div>
                            </div>
                            <div class="row control-group">
                                <div class="form-group col-xs-12 floating-label-form-group controls">
                                    <label>收货地址</label>
                                    <select name="lv1" id="lv1" class="form-control">
                                        <option value="">【省】请选择</option>    
                                        <g:each in="${lv1s}" status="i" var="it">
                                            <option>${it.lv1}</option> 
                                        </g:each>
                                    </select>
                                    <select name="lv2" id="lv2" class="form-control">
                                        <option value="">【市】请选择</option>    
                                    </select>
                                    <select name="lv3" id="lv3" class="form-control">
                                        <option value="">【区】请选择</option>    
                                    </select> 
                                    <textarea rows="5" class="form-control" placeholder="例：石湖西路159号" id="address" name="address" required data-validation-required-message="请输入送货地址">${h.address}</textarea>
                                    <p class="help-block text-danger"></p>
                                </div>
                            </div>
                            <div class="row control-group">
                                <div class="form-group col-xs-12 floating-label-form-group controls">
                                    <label>代客提货服务费</label>
                                    <div id="infoFee"></div>
                                </div>
                            </div>
                            <br>
                            <div id="success"></div>
                            <div class="row">
                                <div class="form-group col-xs-12">
                                    <input type="hidden" name="hid" value="${h.id}" /><br/>
                                    <input type="hidden" id="kg" name="kg" value="0" /><br/>
                                    <input type="hidden" id="fee" name="fee" value="0" /><br/>
                                    <button type="submit" name="act" value="pay" class="btn btn-success btn-block">
                                        支付 
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </section>
    </g:elseif>
    <g:else>

        <!-- 付款后 -->
        <!-- Contact Section -->
        <section style="padding-top: 30px; padding-bottom: 10px;">
            <div class="container">
                <div class="row">
                    <div class="col-lg-12 text-center">
                        <h2>礼盒信息</h2>
                        <hr class="star-primary">
                    </div>
                </div>
                <div class="row">
                    <div class="col-lg-8 col-lg-offset-2">
                        <g:each in="${ds}" status="i" var="it">
                            <div class="rows">
                                <div class="row control-group">
                                    <div class="form-group1 col-xs-10 controls name">
                                        ${it.P_NAME}
                                    </div>
                                    <div class="form-group1 col-xs-2 controls" style="top: 0px;">
                                    </div>
                                    <div class="form-group1 col-xs-8 controls tno">
                                        ${it.GT_NO}
                                    </div>
                                    <div class="form-group1 col-xs-4 controls vcode">
                                        ${it.VCODE}
                                    </div>
                                </div>
                                <hr style="margin-top: 0px; margin-bottom: 0px;"/>
                            </div>
                        </g:each>
                    </div>
                </div>
            </div>
        </section>


        <!-- Contact Section -->
        <section style="padding-top: 30px; padding-bottom: 10px;">
            <div class="container">
                <div class="row">
                    <div class="col-lg-12 text-center">
                        <h2>收货信息</h2>
                        <hr class="star-primary">
                    </div>
                </div>

                <g:if test="${flash.msg}">
                    <div class='alert alert-info'>
                      <button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>
                      <strong>${flash.msg}</strong>
                    </div>
                </g:if>
                
                <div class="row">
                    <div class="col-lg-8 col-lg-offset-2">
                        <div class="row control-group">
                            <div class="form-group col-xs-12 floating-label-form-group controls">
                                <h5>收件人：</h5>
                                ${h.name}
                            </div>
                        </div>
                        <div class="row control-group">
                            <div class="form-group col-xs-12 floating-label-form-group controls">
                                <h5>联系电话：<h5>
                                ${h.phone}
                            </div>
                        </div>
                        <div class="row control-group">
                            <div class="form-group col-xs-12 floating-label-form-group controls">
                                <h5>收货地址：</h5> 
                                ${h.lv1} ${h.lv2} ${h.lv3} <br/>
                                ${h.address}
                            </div>
                        </div>
                        <div class="row control-group">
                            <div class="form-group col-xs-12 floating-label-form-group controls">
                                <h5>代客提货服务费：</h5>
                                共 ${Math.round(h.kg * 1000) / 1000} 公斤，提货服务费 ${h.fee.toInteger()} 元。
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        <section id="contact">
            <div class="container">
                <div class="row">
                    <div class="col-lg-12 text-center">
                        <h2>快递信息</h2>
                        <hr class="star-primary">
                    </div>
                </div>
                <div class="row">
                    <div class="col-lg-8 col-lg-offset-2">
                        <h4>快递单号：${h.express_no}</h4>
                        <table class="table table-striped">
                            <tr>
                                <th>时间</th>
                                <th>记录</th>
                            </tr>
                            <g:each in="${express}" status="i" var="it">
                                <tr>
                                    <td>${it.datetime}</td>
                                    <td>${it.remark}</td>
                                </tr>
                            </g:each>
                            <g:if test="${!express}">
                                <tr>
                                    <td colspan="2">还未安排出货, 请耐心等候</td>
                                </tr>
                            </g:if>
                        </table>
                    </div>
                </div>
            </div>
        </section>
    </g:else>

    <!-- Footer -->
    <footer class="text-center">
        <div class="footer-above">
            <div class="container">
                <div class="row">
                    <div class="footer-col col-md-12">
                        <h3>注意事项</h3>
                        <ol>
                            <li>本提货系统只限领指定中秋月饼礼盒代客提货，不作它用。</li>
                            <li>本提货系统只限于提货功能，恕不找零及兑换。</li>
                            <li>本提货系统不参加门店其他优惠活动，不与其他优惠券、卡同时使用。</li>
                            <li>如存在其他问题，请按照券面上的说明进行咨询。</li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <div class="footer-below">
            <div class="container">
                <div class="row">
                    <div class="col-lg-12">
                        Copyright &copy; iwill 2015
                    </div>
                </div>
            </div>
        </div>
    </footer>
    <!-- Contact Form JavaScript -->
    <script src="${resource(dir: '/bower_components/startbootstrap-freelancer/js', file: 'jqBootstrapValidation.js')}"></script>
    <script src="${resource(dir: '/bower_components/blockui', file: 'jquery.blockUI.js')}"></script>
    <asset:javascript src="exc.js"/>
</body>
</html>

