package com.blog.api;

import com.blog.entity.SmsResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author huang hao
 * @version 1.0
 * @date 2021/1/20 9:35
 */
@FeignClient(name = "chuangxin-dxjk",url = "https://way.jd.com/chuangxin")
public interface SmsAPI {

    @RequestMapping("/dxjk")
    SmsResult send(@RequestParam("appkey") String appkey,
                   @RequestParam("mobile") String mobile,
                   @RequestParam("content") String content);
}
