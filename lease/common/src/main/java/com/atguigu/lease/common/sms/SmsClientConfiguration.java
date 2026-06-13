package com.atguigu.lease.common.sms;

import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SmsProperties.class)
@ConditionalOnProperty(name = "aliyun.sms.endpoint")
public class SmsClientConfiguration {


    @Autowired
    private SmsProperties properties;

    @Bean
    public Client createClient() {
        //com.aliyun.credentials.Client client = new com.aliyun.credentials.Client();
        Config config = new Config();
        config.setEndpoint(properties.getEndpoint());
        config.setAccessKeyId(properties.getAccessKeyId());
        config.setAccessKeySecret(properties.getAccessKeySecret());
        try {
            return new Client(config);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
