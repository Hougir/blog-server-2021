package com.blog.mapper;

import com.blog.domain.entity.TUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author huang hao
 * @version 1.0
 * @date 2021/1/28 16:51
 */
public interface UserRepository extends JpaRepository<TUser,Long>, JpaSpecificationExecutor<TUser> {
    TUser findByOpenid(String openid);
}
