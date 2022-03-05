package com.example.pay.service.impl;

import com.example.pay.dao.PayInfoMapper;
import com.example.pay.enums.PayPlatformEnum;
import com.example.pay.pojo.PayInfo;
import com.example.pay.service.IPayService;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class PayService implements IPayService  {

    @Autowired
    private BestPayService bestPayService;

    @Autowired
    private PayInfoMapper payInfoMapper;

    /**
     * 创建/发起支付
     *
     * @param orderId
     * @param amount
     */
    @Override
    public PayResponse create(String orderId, BigDecimal amount,BestPayTypeEnum bestPayTypeEnum) {
        // config 内容
//        WxPayConfig wxPayConfig = new WxPayConfig();
//        wxPayConfig.setAppId("wxd898fcb01713c658");
//        wxPayConfig.setMchId("1483469312");
//        wxPayConfig.setMchKey("7mdApPMfXddfWWbbP4DUaVYm2wjyh3v3");
//
//        // notifyurl 是接受微信平台异步通知的地方
//        wxPayConfig.setNotifyUrl("http://zongpenglin.natapp1.cc/pay/notify");
//
//
//        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
//        bestPayService.setWxPayConfig(wxPayConfig);

        /**
         *  两种支付方式
         */

//        if(bestPayTypeEnum != BestPayTypeEnum.WXPAY_NATIVE
//                && bestPayTypeEnum!=BestPayTypeEnum.ALIPAY_PC){
//            throw new RuntimeException("暂不支持的支付类型");
//        }

        // 写入数据库
        PayInfo payInfo = new PayInfo(Long.parseLong(orderId),
                PayPlatformEnum.getByBestPayTypeEnum(bestPayTypeEnum).getCode(),
                OrderStatusEnum.NOTPAY.name() ,
                amount);

        payInfoMapper.insertSelective(payInfo);

        PayRequest request = new PayRequest();
        request.setOrderName("10561828-林的小仓库");
        request.setOrderId(orderId);
        request.setOrderAmount(amount.doubleValue());
       // request.setPayTypeEnum(BestPayTypeEnum.WXPAY_NATIVE);
        request.setPayTypeEnum(bestPayTypeEnum);

        PayResponse response = bestPayService.pay(request);

        log.info("发起支付 response={}" , response);

        return response;
    }

    /**
     * 异步通知处理
     *
     * @param notifyData
     */
    @Override
    public String asyncNotify(String notifyData) {
        //1. 签名校验
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("异步通知 Response={}", payResponse);

        //2. 金额校验（从数据库查订单）
        // 需要手写一个xml方法定义
//        payInfoMapper.selectByPrimaryKey()
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(payResponse.getOrderId()));

        if(payInfo == null){
            // 比较严重（正常情况下不会发生的） ，建议发出告警：钉钉，短信
            throw new RuntimeException("通过orderNo查询到的结果为null");
        }
        // 判断支付状态
        //  如果订单支付状态不是"已支付"
        if( !payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.name())){
            // Double类型的比较大小，精度不好控制
            if(payInfo.getPayAmount().compareTo(BigDecimal.valueOf(payResponse.getOrderAmount())) != 0){
                //告警
                throw new RuntimeException("异步通知中的金额和数据库的不一致，orderNo = "+payResponse.getOrderId());
            }

            //3. 修改订单的支付状态
            // 先进行支付状态成功的标记
            payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
            // 设置一个交易流水号
            payInfo.setPlat(payResponse.getOutTradeNo());
            payInfo.setUpdateTime(null);
            payInfoMapper.updateByPrimaryKeySelective(payInfo);

        }



        if(payResponse.getPayPlatformEnum() == BestPayPlatformEnum.WX) {
            //4. 告诉微信不要再通知了
            return "<xml>\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        }else if(payResponse.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY){
            return "success";
        }
        throw new RuntimeException("异步通知中错误的支付平台");
    }

    @Override
    public PayInfo queryByOrderId(String orderId) {
       return  payInfoMapper.selectByOrderNo(Long.parseLong(orderId));
    }

}
