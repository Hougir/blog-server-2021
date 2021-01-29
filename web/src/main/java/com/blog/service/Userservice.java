package com.blog.service;

import com.blog.domain.entity.TUser;
import com.blog.mapper.UserRepository;
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
}
