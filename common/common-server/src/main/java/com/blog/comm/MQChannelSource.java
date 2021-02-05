package com.blog.comm;

import com.blog.enums.MQConst;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author huang hao
 * @version 1.0
 * @date 2021/1/14 9:49
 */
public interface MQChannelSource {
    //生产者通道
    @Output(MQConst.BLOG_SMS_OUTPUT)
    MessageChannel blogSmsOutput();

    //消费者通道
    @Input(MQConst.BLOG_SMS_INPUT)
    SubscribableChannel blogSmsInput();
}
