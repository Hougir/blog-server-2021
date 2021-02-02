package com.blog.controller;

import com.alibaba.fastjson.JSON;
import com.blog.domain.bo.BlogBo;
import com.blog.domain.entity.TBlog;
import com.blog.domain.vo.BlogVo;
import com.blog.service.BlogService;
import com.blog.util.JwtUtils;
import com.blog.util.PageBo;
import com.blog.util.PageVo;
import com.blog.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Api(description = "博客列表控制器")
@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/api")
public class BlogController {

    @Autowired
    BlogService blogService;

    @ApiOperation(value = "条件查询带分页",produces = "application/json; charset=utf-8")
    @PostMapping("/post/list")
    public R list(@RequestBody PageBo<TBlog> pageBo,@RequestHeader(value = "token",required = false)String token){
        String process = "条件查询带分页";
        log.info("{} 入参：body={},token={}", process, JSON.toJSONString(pageBo),token);
        PageVo<BlogVo> page = blogService.findAllAndPage(pageBo,token);
        //log.info("{} 出参：body={}", process, JSON.toJSONString(page));
        return R.ok(page);
    }

    @ApiOperation(value = "根据id获取博客内容",produces = "application/json; charset=utf-8")
    @GetMapping("/articles/getBlogById")
    public R getBlogById(@RequestParam("id") Long id){
        String process = "根据id获取博客内容";
        log.info("{} 入参：id={}", process, id);
        TBlog blog = blogService.getBlogById(id);
        log.info("{} 出参：blog={}", process, JSON.toJSONString(blog));
        return R.ok(blog);
    }
    @ApiOperation(value = "保存",produces = "application/json; charset=utf-8")
    @PostMapping("/articles/save")
    public R save(@RequestBody TBlog blog,@RequestHeader("token")String token){
        String process = "保存";
        log.info("{} 入参：blog={},token={}", process, JSON.toJSONString(blog),token);
        if (!JwtUtils.checkToken(token)) return R.error().message("未登录").code(403);
        R result = blogService.save(blog);
        log.info("{} 出参：result={}", process, result);
        return result;
    }
    @ApiOperation(value = "删除",produces = "application/json; charset=utf-8")
    @DeleteMapping("/articles/delById/{id}")
    public R delById(@RequestHeader("token")String token,@PathVariable("id")Long id){
        String process = "删除";
        log.info("{} 入参：id={},token={}", process, id,token);
        if (!JwtUtils.checkToken(token)) return R.error().message("未登录").code(403);
        R result = blogService.delById(id);
        log.info("{} 出参：result={}", process, result);
        return result;
    }


    @ApiOperation(value = "焦点",produces = "application/json; charset=utf-8")
    @GetMapping("/focus/list")
    public R focusList(){
        List<BlogVo> result = blogService.getFocusList();
        log.info("{} 出参：result={}", result);
        return R.ok(result);
    }

}
