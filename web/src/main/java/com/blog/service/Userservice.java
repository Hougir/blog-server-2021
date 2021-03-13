package com.blog.service;

import com.alibaba.fastjson.JSON;
import com.blog.comm.CacheComponent;
import com.blog.comm.MQChannelSource;
import com.blog.domain.bo.AuthBo;
import com.blog.domain.dto.Sms;
import com.blog.domain.entity.TUser;
import com.blog.enums.CacheKey;
import com.blog.enums.CommConst;
import com.blog.enums.ResultMsg;
import com.blog.mapper.UserRepository;
import com.blog.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;

/**
 * @author huang hao
 * @version 1.0
 * @date 2021/1/28 16:50
 */
@Slf4j
@Service
public class Userservice {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheComponent cacheComponent;

    @Autowired
    private MQChannelSource mqChannelSource;

    @Autowired
    private MQService mqService;

    public TUser getByOpenid(String openid) {
        return userRepository.findByOpenid(openid);
    }

    public void save(TUser member) {
        if (member != null){
            userRepository.saveAndFlush(member);
        }
    }

    public TUser getById(Long memberId) {
        if (null == memberId) return null;

        return userRepository.findById(memberId).get();
    }

    public R login(AuthBo bo) {
        if (StringUtils.isEmpty(bo.getUsername()) || StringUtils.isEmpty(bo.getPassword()) || StringUtils.isEmpty(bo.getSmsCode())) return R.error().message(ResultMsg.PARMS_NOT_NULL);
        log.info("数据库校验");
        //redis获取短信验证码验证
        String content = (String)cacheComponent.get(CacheKey.BLOG_USER_SMS_CODE_LOGIN.getKey(bo.getPhone()));
        if (!CommUtils.equals(content,bo.getSmsCode())) return R.error().message(ResultMsg.SMS_VERIFICATION_CODE_ERROR);
        TUser user = userRepository.findByUsernameAndPassword(bo.getUsername(), MD5Utils.code(bo.getPassword()));
        log.info("数据库校验结果： {}",user);
        if (null == user) return R.error().message(ResultMsg.INCORRECT_ACCOUNT_PASSWORD);
        //登录成功，存入redis,返回token
        String token = JwtUtils.getJwtToken(user.getId(),user.getNickname());
        cacheComponent.add(CacheKey.BLOG_USER_LOGIN_TOKEN.getKey(user.getId().toString()),token, CommConst.EXPIRED_TIME);
        return R.ok(token);
    }

    public R logout(String token) {
        log.info("logout: {}",token);
        TUser user = (TUser) cacheComponent.get(CacheKey.BLOG_USER_LOGIN_TOKEN_USER.getKey(token));
        if (null != user && null != user.getId()){
            //删除redis缓存token值
            boolean b = cacheComponent.remove(CacheKey.BLOG_USER_LOGIN_TOKEN.getKey(user.getId().toString()));
            if (!b) return R.error().message(ResultMsg.FAILED_TO_DELETE_TOKEN);
        }
        //删除用户
        Boolean b = cacheComponent.remove(CacheKey.BLOG_USER_LOGIN_TOKEN_USER.getKey(token));
        if (!b) return R.error().message(ResultMsg.FAILED_TO_DELETE_USER_INFORMATION);
        log.info("logout====> 成功");
        return R.ok();
    }

    public R sendSms(String phone) {
        //查看redis是否存在该号码的验证码，不能频繁重复发送
        String content = (String)cacheComponent.get(CacheKey.BLOG_USER_SMS_CODE_LOGIN.getKey(phone));
        if (CommUtils.isNotNull(content)) return R.ok().message(ResultMsg.MESSAGE_HAS_SENT_PLEASE_TRY_AGAIN_IN_ONE_MINUTE);
        content = GetGenerateCode.generateCode(6);
        Sms sms = new Sms(phone,content);

        log.info("sms===>{}", JSON.toJSONString(sms));
        boolean send = mqChannelSource.blogSmsOutput().send(
                MessageBuilder.withPayload(sms)
                        .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build()
        );

        log.info("send===>{}", send);
        //boolean send = mqService.sendSms(sms);
        if (!send) return R.error().message(ResultMsg.MESSAGE_FAILED_TO_SEND);

        return R.ok().message(content);
    }
}
