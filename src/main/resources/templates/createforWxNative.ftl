<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>支付</title>
</head>
<body>
<div id="myQrcode"></div>
<div id="orderId" hidden>${orderId}</div>
<div id="returnUrl" hidden>${returnUrl}</div>

<script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.5.1/jquery.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/jquery.qrcode/1.0/jquery.qrcode.min.js"></script>
<script>
    jQuery('#myQrcode').qrcode({
        text    : "${codeUrl}"
    });

    $(function () {
       // 定时器，请求后端api
        setInterval(function(){
            console.log('开始查询支付状态...')
            $.ajax({
                url: '/pay/queryByOrderId',
                data: {
                    //如何获取这个orderId ,参考 ${codeUrl},使用模版渲染的方式;
                    'orderId':$('#orderId').text()
                },
                success: function (result){
                    console.log(result)
                    if(result.platformStatus != null
                       && result.platformStatus === 'SUCCESS'){
                        //发生跳转的地址！
                        location.href = $('#returnUrl').text()
                    }
                },
                error: function (result){
                    alert(result)
                }
            })
        },2000)
    });
</script>
</body>
</html>