package com.example.pay.service.impl;

import com.example.pay.PayApplicationTests;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import org.junit.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class PayServiceTest extends PayApplicationTests {

    @Autowired
    private PayServiceImpl payservice;

    @Autowired //amqp 是一种协议
    private AmqpTemplate amqpTemplate;

    @Test
    public void create() {
        //new Decimal("0.01") = BigDecimal.valueOf(0.06)
        payservice.create("12134567", BigDecimal.valueOf(0.05), BestPayTypeEnum.WXPAY_NATIVE);
    }

    @Test
    public void sendMQMsg(){
        amqpTemplate.convertAndSend("payNotify","hello word");
    }
}