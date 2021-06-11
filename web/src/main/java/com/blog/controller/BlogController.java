package com.blog.controller;

import com.alibaba.fastjson.JSON;
import com.blog.domain.bo.CommentBo;
import com.blog.domain.entity.Comment;
import com.blog.domain.entity.TBlog;
import com.blog.domain.vo.BlogVo;
import com.blog.enums.ResultMsg;
import com.blog.service.BlogService;
import com.blog.util.JwtUtils;
import com.blog.util.PageBo;
import com.blog.util.PageVo;
import com.blog.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@Api(description = "博客列表控制器")
@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RefreshScope
@RequestMapping("/api")
public class BlogController {

    @Autowired
    BlogService blogService;

    @ApiOperation(value = "条件查询带分页",produces = "application/json; charset=utf-8")
    @PostMapping("/post/list")
    public R list(@RequestBody PageBo<TBlog> pageBo,@RequestHeader(value = "token",required = false)String token){
        String process = "条件查询带分页";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info("{} 入参：body={},token={}", process, JSON.toJSONString(pageBo),token);
        PageVo<BlogVo> page;
        try {
            page = blogService.findAllAndPage(pageBo,token);
        } catch (Exception e) {
            return R.error().message("请稍后再试");
        }
        log.info("{}耗时{}毫秒",process,sw.getTime());
        //log.info("{} 出参：body={}", process, JSON.toJSONString(page));
        return R.ok(page);
    }

    @ApiOperation(value = "根据id获取博客内容",produces = "application/json; charset=utf-8")
    @GetMapping("/articles/getBlogById")
    public R getBlogById(@RequestParam("id") Long id){
        String process = "根据id获取博客内容";
        log.info("{} 入参：id={}", process, id);
        BlogVo blogVo = blogService.getBlogById(id);
        log.info("{} 出参：blog={}", process, JSON.toJSONString(blogVo));
        return R.ok(blogVo);
    }
    @ApiOperation(value = "保存",produces = "application/json; charset=utf-8")
    @PostMapping("/articles/save")
    public R save(@RequestBody TBlog blog,@RequestHeader("token")String token){
        String process = "保存";
        log.info("{} 入参：blog={},token={}", process, JSON.toJSONString(blog),token);
        if (!JwtUtils.checkToken(token)) return R.error().message(ResultMsg.NOT_LOGIN).code(403);
        R result = blogService.save(blog);
        log.info("{} 出参：result={}", process, result);
        return result;
    }
    @ApiOperation(value = "删除",produces = "application/json; charset=utf-8")
    @DeleteMapping("/articles/delById/{id}")
    public R delById(@RequestHeader("token")String token,@PathVariable("id")Long id){
        String process = "删除";
        log.info("{} 入参：id={},token={}", process, id,token);
        if (!JwtUtils.checkToken(token)) return R.error().message(ResultMsg.NOT_LOGIN).code(403);
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

    @ApiOperation(value = "留言评论",produces = "application/json; charset=utf-8")
    @PostMapping("/about/comment")
    public R comment(@RequestBody Comment comment){
        log.info("{} 入参：comment={}", "留言评论",JSON.toJSONString(comment));
        return blogService.comment(comment);
    }

    @ApiOperation(value = "查询留言",produces = "application/json; charset=utf-8")
    @PostMapping("/about/commentList")
    public R commentList(@RequestHeader("token")String token,@RequestBody PageBo<CommentBo> bo){
        log.info("{} 入参：commentList={},token:{}", "查询留言",JSON.toJSONString(bo),token);
        return blogService.commentList(bo,token);
    }

    @ApiOperation(value = "修改留言状态 已读",produces = "application/json; charset=utf-8")
    @PostMapping("/about/haveRead/{id}")
    public R haveRead(@RequestHeader("token")String token,@PathVariable("id")Long id){
        log.info("{} 入参：修改留言状态id={},token={}", "查询留言",id,token);
        return blogService.haveRead(id,token);
    }

    @ApiOperation(value = "删除留言",produces = "application/json; charset=utf-8")
    @PostMapping("/about/delCommentById/{id}")
    public R delCommentById(@RequestHeader("token")String token,@PathVariable("id")Long id){
        log.info("{} 入参：删除留言id={},token={}", "删除留言",id,token);
        return blogService.delCommentById(id,token);
    }

}
