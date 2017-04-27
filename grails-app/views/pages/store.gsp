<%@ defaultCodec="none" %>  
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>爱维尔</title>
    <link href="${resource(dir: '/bower_components/weui/dist/style', file: 'weui.min.css')}" rel="stylesheet">
    <link href="${resource(dir: '/bower_components/weui/dist/example', file: 'example.css')}" rel="stylesheet">
    <style>
        .hide {
            display: none;
        } 
    </style>
</head>
<body ontouchstart="">

    <div class="container" id="container"></div>

    <script type="text/html" id="tpl_home">
<div class="page">
    <div class="page__hd">
        <h1 class="page__title">
            <img src="${resource(dir: '/static', file: 'logo_store.gif')}" alt="iwill" height="80px" />
        </h1>
        <p class="page__desc">你附近的爱维尔门店</p>
        <div class="hide" id="amap" style="width: 800px; height: 600px"></div>
    </div>
    <div class="page__bd page__bd_spacing">
        <ul id="stores">


            <li id="loading">
                <div class="weui-flex">
                    <p class="weui-flex__item">资料读取中...</p>
                </div>
            </li>

            <li id="store" class="hide">
                <div class="weui-flex js_category">
                    <p class="weui-flex__item branch_name"><!-- branch_name --></p>
                    <p class="weui-flex__item dist"><!-- dist --></p>
                    <img src="${resource(dir: '/bower_components/weui/dist/example/images', file: 'icon_nav_form.png')}" alt="">
                </div>
                <div class="page__category js_categoryInner">
                    <div class="weui-panel weui-panel_access">
                        <div class="weui-panel__bd">
                            <div class="weui-media-box weui-media-box_appmsg">
                                <div class="weui-media-box__bd">
                                    <div class="weui-media-box__title address"><!-- 地址 city district address --></div>
                                    <div class="weui-media-box__title telephone"><!-- 电话 telephone --></div>
                                    <div class="weui-media-box__title open_time"><!-- 营业时间 open_time --></div>
                                    <div class="weui-media-box__desc recommend"><!-- 推荐 recommend --></div>
                                    <div class="weui-media-box__desc introduction"><!-- 简介 introduction --></div>
                                </div>
                            </div>
                        </div>
                        <div class="weui-panel__ft">
                            <a href="#" class="weui-cell weui-cell_access weui-cell_link open_map">
                                <div class="weui-cell__bd">打开地图</div>
                                <span class="weui-cell__ft"></span>
                            </a>    
                        </div>

                    </div>
                </div>
            </li>

        </ul>
    </div>
    <div class="page__ft">
    </div>
</div>
<script type="text/javascript">
    $(function(){

        // use AMap get location 
        var map = new AMap.Map('amap');
        map.plugin('AMap.Geolocation', function () {
            geolocation = new AMap.Geolocation({
                enableHighAccuracy: true,//是否使用高精度定位，默认:true
                timeout: 10000,          //超过10秒后停止定位，默认：无穷大
                maximumAge: 0,           //定位结果缓存0毫秒，默认：0
                convert: true           //自动偏移坐标，偏移后的坐标为高德坐标，默认：true
            });
            geolocation.getCurrentPosition(amapGetPosition);
        });
            
        function amapGetPosition(status, result) {
            if (status == 'complete') {
                showStore(result);
            } else if (status == 'error') {
                showStore();
            }
        }
            
        //function loadData() {
        //    if (navigator.geolocation) {
        //        navigator.geolocation.getCurrentPosition(showStore, errorLocation);
        //    } else { 
        //        showStore();
        //    }
        //}

        //function errorLocation(e) {
        //    showStore(); // 正常顯示清單
        //}

        function showStore(position) {
            // for loadData()
            //var lat = (position) ? position.coords.latitude : null;
            //var lng = (position) ? position.coords.longitude : null;
            
            // for AMap
            var lat = (position) ? position.position.lat : null;
            var lng = (position) ? position.position.lng : null;
            
            $.ajax({
                type: 'GET',
                url: 'getStore',
                data: { lat: lat, lng: lng },
                dataType: 'json',
                timeout: 2000,
                success: function(data){
                    $('#loading').addClass('hide');
                    var store_tpl = $('#store');
                    var stores = $('#stores');
                    for (var i = 0; i < data.length; i++) {
                        var row = data[i];
                        var foo = store_tpl.clone();
                        foo.removeClass('hide');
                        foo.attr('id', '');
                        foo.find('.branch_name')[0].innerHTML = row.branch_name;
                        foo.find('.dist')[0].innerHTML = (row.dist == 'NA') ? '' : row.dist;
                        foo.find('.address')[0].innerHTML = '地址：' + row.city + row.district + row.address;
                        foo.find('.telephone')[0].innerHTML = '电话：' + row.telephone;
                        foo.find('.open_time')[0].innerHTML = '营业时间：' + row.open_time;
                        foo.find('.recommend')[0].innerHTML = '推荐：' + row.recommend;
                        foo.find('.introduction')[0].innerHTML = '简介:' + row.introduction;
                        foo.find('.open_map')[0].href = 'http://m.amap.com/navi/?start=' + lng + ',' + lat + '&dest=' + row.longitude + ',' + row.latitude + '&naviBy=car&key=569692b2e5970ff60c2fe8d347a44252';
                        if (row.dist == 'NA') {
                            $(foo.find('.open_map')[0]).addClass('hide');
                        }
                        stores.append(foo);
                    }
                },
                error: function(xhr, type){
                    alert('Ajax error!')
                }
            });

        }

        // loadData();


        // ui code
        var winH = $(window).height();
        var categorySpace = 10;

        $('.page').on('click', '.js_category', function(){
            var $this = $(this),
                $inner = $this.next('.js_categoryInner'),
                $page = $this.parents('.page'),
                $parent = $(this).parent('li');
            var innerH = $inner.data('height');
            bear = $page;

            if(!innerH){
                $inner.css('height', 'auto');
                innerH = $inner.height();
                $inner.removeAttr('style');
                $inner.data('height', innerH);
            }

            if($parent.hasClass('js_show')){
                $parent.removeClass('js_show');
            }else{
                $parent.siblings().removeClass('js_show');

                $parent.addClass('js_show');
                if(this.offsetTop + this.offsetHeight + innerH > $page.scrollTop() + winH){
                    var scrollTop = this.offsetTop + this.offsetHeight + innerH - winH + categorySpace;

                    if(scrollTop > this.offsetTop){
                        scrollTop = this.offsetTop - categorySpace;
                    }

                    $page.scrollTop(scrollTop);
                }
            }
        });

    });
</script>
</script>


    <script src="${resource(dir: '/bower_components/weui/dist/example', file: 'zepto.min.js')}"></script>
    <!-- script type="text/javascript" src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script -->
    <script src="https://res.wx.qq.com/open/libs/weuijs/1.0.0/weui.min.js"></script>
    <script src="${resource(dir: '/bower_components/weui/dist/example', file: 'example.js')}"></script>
    <!-- script type="text/javascript" src="https://tajs.qq.com/stats?sId=60520182" charset="UTF-8"></script -->
    <script type="text/javascript" src="https://webapi.amap.com/maps?v=1.3&key=569692b2e5970ff60c2fe8d347a44252"></script>
</body>
</html>













