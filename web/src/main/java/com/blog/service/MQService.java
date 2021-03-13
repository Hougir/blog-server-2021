package com.blog.service;

import com.alibaba.fastjson.JSON;
import com.blog.api.SmsAPI;
import com.blog.comm.CacheComponent;
import com.blog.comm.MQChannelSource;
import com.blog.domain.dto.Sms;
import com.blog.entity.SmsResult;
import com.blog.enums.CacheKey;
import com.blog.enums.CommConst;
import com.blog.enums.MQConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

/**
 * @author huang hao
 * @version 1.0
 * @date 2021/2/3 17:31
 */
@Slf4j
@Service
public class MQService {
    @Autowired
    private MQChannelSource mqChannelSource;

    @Autowired
    private CacheComponent cacheComponent;

   // @Autowired
    //private SmsAPI smsAPI;

    @StreamListener(MQConst.BLOG_SMS_INPUT)
    public void receiveTransactionalMsg(String transactionMsg) {

        System.out.println("收到消息");


        log.info("收到消息=========> {}", JSON.toJSONString(transactionMsg));
        Sms sms = JSON.parseObject(transactionMsg, Sms.class);
        if (null != sms) {
            log.info("封装短信对象=========> {}", JSON.toJSONString(sms));
            boolean b = this.sendSms(sms);
            if (!b) this.sendSms(sms);
        }
    }
    public boolean sendSms(Sms sms){
        //SmsResult result = smsAPI.send(smsMsg.getAppkey(),smsMsg.getMobile(),smsMsg.getContent());
        //log.info("发送结果===> {}",JSON.toJSONString(result));
        //发送成功，存入redis
        Boolean b = cacheComponent.add(CacheKey.BLOG_USER_SMS_CODE_LOGIN.getKey(sms.getPhone()), sms.getContent(), CommConst.SMS_EXPIRED_TIME);
        if (!b) return false;
        return true;
    }
}
