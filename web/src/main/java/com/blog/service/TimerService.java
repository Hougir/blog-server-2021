package com.blog.service;

import com.alibaba.fastjson.JSONObject;
import com.blog.enums.CacheKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author huang hao
 * @version 1.0
 * @date 2021/5/27 9:54
 */
@Slf4j
@Component
public class TimerService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void delCacheForPageList(){

        Long delete = null;
        try {
            delete = stringRedisTemplate.opsForHash().delete(CacheKey.BLOG_PAGE_LIST.getKey(), "one");
            if (delete == 1){
                log.info("---删除缓存成功---");
            }else {
                log.info("------删除缓存失败------");
            }
        } catch (Exception e) {
            log.error("------删除缓存失败------{}", JSONObject.toJSONString(e));
        }


    }
}
