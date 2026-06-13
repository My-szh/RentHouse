package com.atguigu.lease.web.app.service.impl;

import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.atguigu.lease.web.app.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private Client client;

    @Override
    public void sendSms(String phone, String code) {
        SendSmsVerifyCodeRequest sendSmsVerifyCodeRequest = new SendSmsVerifyCodeRequest();
        sendSmsVerifyCodeRequest.setPhoneNumber(phone);
        sendSmsVerifyCodeRequest.setSignName("速通互联验证码");
        sendSmsVerifyCodeRequest.setTemplateCode("100001");
        sendSmsVerifyCodeRequest.setTemplateParam("{\"code\":\""+code+"\",\"min\":\"1\"}");
        try {
            client.sendSmsVerifyCode(sendSmsVerifyCodeRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
