package com.example.pay;

import com.example.pay.service.impl.PayServiceImpl;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PayApplicationTests {
    @Autowired
    private PayServiceImpl payServiceImpl;

    @Test
    public void contextLoads() {
        payServiceImpl.create("11212121", BigDecimal.valueOf(0.01), BestPayTypeEnum.WXPAY_NATIVE);
    }
}