package com.blog.controller;

import com.alibaba.fastjson.JSON;
import com.blog.domain.entity.TUser;
import com.blog.service.Userservice;
import com.blog.util.ConstantWxUtils;
import com.blog.util.HttpClientUtils;
import com.blog.util.JwtUtils;
import com.blog.util.R;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;

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
    @Autowired
    private Userservice userservice;
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
                return "redirect:http://localhost:8888?token="+jwtToken;
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
            //最后：返回首页面，通过路径传递token字符串
            return "redirect:http://localhost:8888?token="+jwtToken;
        }catch(Exception e) {
            log.info("20001,登录失败");
            e.printStackTrace();
            return "redirect:http://localhost:8888?msg=faild";
        }
    }

    @ApiOperation(value = "获取登录者信息",produces = "application/json; charset=utf-8")
    @GetMapping("/api/admin/user/getMemberInfo")
    public @ResponseBody R  getMemberInfo(HttpServletRequest request){
        log.info("getMemberInfo");
        //调用jwt工具类的方法。根据request对象获取头信息，返回用户id
        Long memberId = JwtUtils.getMemberIdByJwtToken(request);
        if (null == memberId) return R.error().message("暂未登录");
        //查询数据库根据用户id获取用户信息
        TUser member = userservice.getById(memberId);
        log.info("TUser====> {}", JSON.toJSONString(member));
        return R.ok(member);
    }

    @ApiOperation(value = "登出 ",produces = "application/json; charset=utf-8")
    @GetMapping("/api/admin/user/logout")
    public @ResponseBody R logout(@RequestHeader("token") String token){
        log.info("logout: {}",token);
        boolean checkToken = JwtUtils.checkToken(token);
        if (!checkToken) return R.error().message("暂未登录");
        //调用jwt工具类的方法。根据request对象获取头信息，返回用户id
        //Long memberId = JwtUtils.getMemberIdByJwtToken(request);
        //if (null == memberId) return R.error().message("暂未登录");
        //查询数据库根据用户id获取用户信息
        //删除redis缓存token值
        log.info("logout====>");
        return R.ok();
    }
}
