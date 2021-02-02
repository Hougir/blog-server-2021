package com.blog.service;

import com.blog.domain.bo.AuthBo;
import com.blog.domain.entity.TUser;
import com.blog.mapper.UserRepository;
import com.blog.util.JwtUtils;
import com.blog.util.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public String login(AuthBo bo) {
        log.info("数据库校验");
        //redis获取短信验证码验证


        TUser user = userRepository.findByUsernameAndPassword(bo.getUsername(), MD5Utils.code(bo.getPassword()));
        log.info("数据库校验结果： {}",user);
        if (null == user) return null;
        //登录成功，返回token
        return JwtUtils.getJwtToken(user.getId(), user.getNickname());
    }
}
