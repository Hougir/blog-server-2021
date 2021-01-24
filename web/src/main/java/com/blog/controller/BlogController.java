package com.blog.controller;

import com.blog.domain.vo.BlogVo;
import com.blog.util.PageVo;
import com.blog.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(description = "博客列表控制器")
@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/api")
public class BlogController {

    @ApiOperation(value = "条件查询带分页",produces = "application/json; charset=utf-8")
    @PostMapping("/post/list")
    public R list(){
        System.out.println("进来了");
        BlogVo blogVo = new BlogVo();
        List<BlogVo> list = new ArrayList<>();
        list.add(blogVo);
        PageVo<BlogVo> page = new PageVo<>();
        page.setItems(list);
        page.setTotal(1L);
        page.setHasNextPage(false);
        return R.ok(page);
    }
}
