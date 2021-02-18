package com.blog.controller;

import com.alibaba.fastjson.JSON;
import com.blog.comm.CacheComponent;
import com.blog.domain.bo.AuthBo;
import com.blog.domain.entity.TUser;
import com.blog.enums.CacheKey;
import com.blog.enums.CommConst;
import com.blog.enums.ResultMsg;
import com.blog.service.Userservice;
import com.blog.util.*;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author huang hao
 * @version 1.0
 * @date 2021/1/28 16:45
 */
@Slf4j
@CrossOrigin(origins = "*",maxAge = 3600)
@Controller
@Api(description = "博客后台管理")
public class AdminController {

    static final String REDIRECT_URL = "redirect:http://localhost/#/admin/wx/";

    static final String REGEXP = "^((13[0-9])|(15[^4,\\D])|(18[0,3-9]))\\d{8}$";
    @Autowired
    private Userservice userservice;

    @Autowired
    private CacheComponent cacheComponent;

    @ApiOperation(value = "发送短信验证码",produces = "application/json; charset=utf-8")
    @GetMapping("/api/login/sendSms/{phone}")
    public @ResponseBody R sendSms(@PathVariable("phone") String phone){
        log.info("发送短信phone：{}",phone);
        Pattern pattern = Pattern.compile(REGEXP);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(phone);
        if (!matcher.matches()) return R.error().message(ResultMsg.INVALID_MOBILE_PHONE_NUMBER);
        return userservice.sendSms(phone);
    }

    //2 获取扫描人信息，添加数据
    @GetMapping("/api/ucenter/wx/callback")
    public String callback(String code, String state) {

        log.info("获取扫描人信息，添加数据:{}  {}",code,state);
        try {
            //1 获取code值，临时票据，类似于验证码
            //2 拿着code请求 微信固定的地址，得到两个值 accsess_token 和 openid
            String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                    "?appid=%s" +
                    "&secret=%s" +
                    "&code=%s" +
                    "&grant_type=authorization_code";
            //拼接三个参数 ：id  秘钥 和 code值
            String accessTokenUrl = String.format(
                    baseAccessTokenUrl,
                    ConstantWxUtils.WX_OPEN_APP_ID,
                    ConstantWxUtils.WX_OPEN_APP_SECRET,
                    code
            );
            //请求这个拼接好的地址，得到返回两个值 accsess_token 和 openid
            //使用httpclient发送请求，得到返回结果
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);

            //从accessTokenInfo字符串获取出来两个值 accsess_token 和 openid
            //把accessTokenInfo字符串转换map集合，根据map里面key获取对应值
            //使用json转换工具 Gson
            Gson gson = new Gson();
            HashMap mapAccessToken = gson.fromJson(accessTokenInfo, HashMap.class);
            String access_token = (String)mapAccessToken.get("access_token");
            String openid = (String)mapAccessToken.get("openid");

            //把扫描人信息添加数据库里面
            //判断数据表里面是否存在相同微信信息，根据openid判断
            TUser member = userservice.getByOpenid(openid);

            //3 拿着得到accsess_token 和 openid，再去请求微信提供固定的地址，获取到扫描人信息
            //访问微信的资源服务器，获取用户信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            //拼接两个参数
            String userInfoUrl = String.format(
                    baseUserInfoUrl,
                    access_token,
                    openid
            );
            //发送请求
            String userInfo = HttpClientUtils.get(userInfoUrl);
            //获取返回userinfo字符串扫描人信息
            HashMap userInfoMap = gson.fromJson(userInfo, HashMap.class);
            String nickname = (String)userInfoMap.get("nickname");//昵称
            String headimgurl = (String)userInfoMap.get("headimgurl");//头像
            Date date = new Date();
            if(member == null) {//memeber是空，表没有相同微信数据，进行添加
                member = new TUser();
                member.setOpenid(openid);
                member.setNickname(nickname);
                member.setAvatar(headimgurl);
                member.setCreateTime(date);
                userservice.save(member);
                String jwtToken = JwtUtils.getJwtToken(member.getId(), member.getNickname());
                cacheComponent.add(CacheKey.LOAN_USER_LOGIN_TOKEN.getKey(member.getId().toString()),jwtToken, CommConst.EXPIRED_TIME);
                return REDIRECT_URL + jwtToken;
            }
            if (!headimgurl.equals(member.getAvatar())){
                member.setAvatar(headimgurl);
            }
            if (!nickname.equals(member.getNickname())){
                member.setNickname(nickname);
            }
            member.setUpdateTime(date);
            userservice.save(member);
            //使用jwt根据member对象生成token字符串
            String jwtToken = JwtUtils.getJwtToken(member.getId(), member.getNickname());
            //token存入redis
            cacheComponent.add(CacheKey.LOAN_USER_LOGIN_TOKEN.getKey(member.getId().toString()),jwtToken, CommConst.EXPIRED_TIME);
            //最后：返回首页面，通过路径传递token字符串
            return REDIRECT_URL + jwtToken;
        }catch(Exception e) {
            log.error("登录失败,{}",e);
            return "redirect:http://localhost/";
        }
    }

    @ApiOperation(value = "获取登录者信息",produces = "application/json; charset=utf-8")
    @GetMapping("/api/admin/user/getMemberInfo/{token}")
    public @ResponseBody R  getMemberInfo(@PathVariable("token")String token){
        log.info("getMemberInfo=====>token= {}",token);
        //调用jwt工具类的方法，返回用户id
        Long memberId = JwtUtils.getMemberIdByJwtToken(token);
        if (ObjectUtils.isEmpty(memberId)) return R.error().message(ResultMsg.NOT_LOGIN).code(403);
        //查询数据库根据用户id获取用户信息
        TUser member = userservice.getById(memberId);
        //验证token是否过期
        String redisToken = (String)cacheComponent.get(CacheKey.LOAN_USER_LOGIN_TOKEN.getKey(member.getId().toString()));
        //把用户信息存入redis
        if (CommUtils.isNull(redisToken)) return R.error().message(ResultMsg.LOGINH_HAS_EXPIRED).code(401);
        cacheComponent.add(CacheKey.LOAN_USER_LOGIN_TOKEN_USER.getKey(token),member,CommConst.EXPIRED_TIME);
        log.info("TUser====> {}", JSON.toJSONString(member));
        return R.ok(member);
    }

    @ApiOperation(value = "账号密码登录",produces = "application/json; charset=utf-8")
    @PostMapping("/api/admin/user/login")
    public @ResponseBody R  login(@RequestBody AuthBo bo){
        log.info("login: bo==> {}",JSON.toJSONString(bo));
        R r = userservice.login(bo);
        log.info("token====> {}", r.getData());
        return r;
    }
    @ApiOperation(value = "登出",produces = "application/json; charset=utf-8")
    @GetMapping("/api/admin/user/logout/{token}")
    public @ResponseBody R logout(@PathVariable("token") String token){
        log.info("登出:{}",token);
        return userservice.logout(token);
    }
}
