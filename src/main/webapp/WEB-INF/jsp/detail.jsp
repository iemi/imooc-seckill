<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <%@include file="common/head.jsp"%>
    <title>秒杀详情页</title>
</head>
<body>
<input type="hidden" id="basePath" value="${basePath}" />
<div class="container">
    <div class="panel panel-default text-center">
        <div class="panel-heading">
            <h1>${seckill.name}</h1>
        </div>
        <div class="panel-body">
            <h2 class="text-danger">
                <!-- 显示time图标 -->
                <span class="glyphicon glyphicon-time"></span>
                <!-- 展示倒计时 -->
                <span class="glyphicon" id="seckillBox"></span>
            </h2>
        </div>
    </div>
</div>

<!-- 登录弹出层，输入电话 -->
<div id="killPhoneModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title text-center">
                    <span class="glyphicon glyphicon-phone"></span>秒杀电话：
                </h3>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-xs-8 col-xs-offset-2">
                        <input type="text" name="killphone" id="killphoneKey"
                               placeholder="填手机号^O^" class="form-control" />
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <span id="killphoneMessage" class="glyphicon"></span>
                <button type="button" id="killPhoneBtn" class="btn btn-success">
                    <span class="glyphicon glyphicon-phone"></span> Submit
                </button>
            </div>
        </div>
    </div>
</div>

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="//cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>
<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="//cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<!-- jQuery cookie操作插件 -->
<script src="//cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<!-- jQery countDonw倒计时插件  -->
<script src="//cdn.bootcss.com/jquery.countdown/2.1.0/jquery.countdown.min.js"></script>
<!-- 开始编写交互逻辑 -->
<%--<script src="./js/seckill.js"  type="text/javascript"></script>--%>
<script type="text/javascript">
    $(function(){
        //使用EL表达式传入参数
        seckill.detail.init({
            seckillId : ${seckill.seckillId},
            startTime : ${seckill.startTime.time},//毫秒
            endTime : ${seckill.endTime.time}
        });
    });
    var seckill = {
        // 封装秒杀相关ajax的url
        URL : {
            basePath : function() {
                return $('#basePath').val();
            },
            now : function() {
                return seckill.URL.basePath() + 'seckill/time/now';
            },
            exposer : function(seckillId) {
                return seckill.URL.basePath() + 'seckill/' + seckillId + '/exposer';
            },
            execution : function(seckillId, md5) {
                return seckill.URL.basePath() + 'seckill/' + seckillId + '/' + md5 + '/execution';
            }
        },
        // 处理秒杀逻辑
        handleSeckill : function(seckillId, node) {
            // 获取秒杀地址，控制显示逻辑，执行秒杀
            node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
            console.log('exposerUrl=' + seckill.URL.exposer(seckillId));//TODO
            $.get(seckill.URL.exposer(seckillId), {}, function(result) {
                // 在回调函数中，执行交互流程
                if (result && result['success']) {
                    var exposer = result['data'];
                    if (exposer['exposed']) {
                        // 开启秒杀
                        var md5 = exposer['md5'];
                        var killUrl = seckill.URL.execution(seckillId, md5);
                        console.log('killUrl=' + killUrl);//TODO
                        $('#killBtn').one('click', function() {
                            // 执行秒杀请求
                            // 1.先禁用按钮
                            $(this).addClass('disabled');
                            // 2.发送秒杀请求
                            $.post(killUrl, {}, function(result) {
                                if (result && result['success']) {
                                    var killResult = result['data'];
                                    var state = killResult['state'];
                                    var stateInfo = killResult['stateInfo'];
                                    console.log(stateInfo)
                                    // 3.显示秒杀结果
                                    node.html('<span class="label label-success">' + stateInfo + '</span>');
                                }
                            });
                        });
                        node.show();
                    } else {
                        // 未开启秒杀
                        var now = exposer['now'];
                        var start = exposer['start'];
                        var end = exposer['end'];
                        // 重新计算计时逻辑
                        seckill.countdown(seckillId, now, start, end);
                    }
                } else {
                    console.log('result=' + result);
                }
            });
        },
        // 验证手机号
        validatePhone : function(phone) {
            if (phone && phone.length == 11 && !isNaN(phone)) {
                return true;
            } else {
                return false;
            }
        },
        // 倒计时
        countdown : function(seckillId, nowTime, startTime, endTime) {
            // 时间判断
            var seckillBox = $('#seckillBox');
            if (nowTime > endTime) {
                // 秒杀结束
                seckillBox.html('秒杀结束!');
            } else if (nowTime < startTime) {
                // 秒杀未开始，计时事件绑定
                var killTime = new Date(startTime + 1000);
                seckillBox.countdown(killTime, function(event) {
                    // 时间格式
                    var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                    seckillBox.html(format);
                    // 时间完成后回调事件
                }).on('finish.countdown', function() {
                    // 获取秒杀地址，控制显示逻辑，执行秒杀
                    seckill.handleSeckill(seckillId, seckillBox);
                });
            } else {
                // 秒杀开始
                seckill.handleSeckill(seckillId ,seckillBox);
            }
        },
        // 详情页秒杀逻辑
        detail : {
            // 详情页初始化
            init : function(params) {
                // 用户手机验证和登录，计时交互
                // 规划我们的交互流程
                // 在cookie中查找手机号
                var killPhone = $.cookie('killPhone');
                var startTime = params['startTime'];
                var endTime = params['endTime'];
                var seckillId = params['seckillId'];
                // 验证手机号
                if (!seckill.validatePhone(killPhone)) {
                    // 绑定phone
                    // 控制输出
                    var killPhoneModal = $('#killPhoneModal');
                    killPhoneModal.modal({
                        show : true,// 显示弹出层
                        backdrop : 'static',// 禁止位置关闭
                        keyboard : false
                        // 关闭键盘事件
                    })
                    $('#killPhoneBtn').click(function() {
                        var inputPhone = $('#killphoneKey').val();
                        console.log('inputPhone='+inputPhone);//TODO
                        if (seckill.validatePhone(inputPhone)) {
                            // 电话写入cookie
                            $.cookie('killPhone', inputPhone, {
                                expires : 7,
                                path : '/seckill'
                            });
                            // 刷新页面
                            window.location.reload();
                        } else {
                            $('#killphoneMessage').hide().html('<label class="label label-danger">手机号错误!</label>').show(300);
                        }
                    });
                }
                // 已经登录
                // 计时交互
                var startTime = params['startTime'];
                var endTime = params['endTime'];
                var seckillId = params['seckillId'];
                $.get(seckill.URL.now(), {}, function(result) {
                    if (result && result['success']) {
                        var nowTime = result['data'];
                        // 时间判断，计时交互
                        seckill.countdown(seckillId, nowTime, startTime, endTime);
                    } else {
                        console.log(result['reult:'] + result);
                    }
                });
            }
        }
    }
</script>
</body>
</html>