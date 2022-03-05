package com.example.pay.controller;

import com.example.pay.pojo.PayInfo;
import com.example.pay.service.impl.PayService;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/pay")
@Slf4j
public class PayController {
    @Autowired
    private PayService payService;

    @Autowired
    private WxPayConfig wxPayConfig;

    @GetMapping("/create")
    public ModelAndView create (@RequestParam("orderId") String orderId ,
                                @RequestParam("amount") BigDecimal amount,
                                @RequestParam("payType") BestPayTypeEnum bestPayTypeEnum
                                ){
//        return new ModelAndView("create");
        PayResponse response = payService.create(orderId,amount,bestPayTypeEnum);

        //渲染 ，自动化生成
        // 支付方式不同，渲染就不同，WXPAY_NATIVE使用codeUrl，ALIPAY_PC 使用body；
        Map<String, String> map=new HashMap<>();
        if(bestPayTypeEnum == BestPayTypeEnum.WXPAY_NATIVE) {
            map.put("codeUrl", response.getCodeUrl());

            map.put("orderId", orderId);
            map.put("returnUrl",wxPayConfig.getReturnUrl() );
            //模版渲染处
            return new ModelAndView("createforWxNative",map);
        }else if(bestPayTypeEnum == BestPayTypeEnum.ALIPAY_PC){
            map.put("body", response.getBody());

            //模版渲染处
            return new ModelAndView("createforAlipayPC",map);
        }
        throw new RuntimeException("暂不支持的支付类型");
    }

    //异步通知是向我的微信地址发送一个post请求
    @PostMapping("/notify")
    @ResponseBody
    public String asyncNotify( @RequestBody String notifyData){
        return payService.asyncNotify(notifyData);
    }

    // 支付跳转实现
    @GetMapping("/queryByOrderId")
    @ResponseBody
    public PayInfo queryByOrderId(@RequestParam String orderId) {
        log.info("查询支付记录中...");
        return payService.queryByOrderId(orderId);
    }
}
