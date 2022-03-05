package com.example.pay.service.impl;

import com.example.pay.PayApplicationTests;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class PayServiceTest extends PayApplicationTests {

    @Autowired
    private PayService payservice;

    @Test
    public void create() {
        //new Decimal("0.01") = BigDecimal.valueOf(0.06)
        payservice.create("12134567", BigDecimal.valueOf(0.05), BestPayTypeEnum.WXPAY_NATIVE);
    }
}