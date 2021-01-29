package com.blog.service;

import com.alibaba.fastjson.JSON;
import com.blog.domain.bo.BlogBo;
import com.blog.domain.entity.TBlog;
import com.blog.domain.vo.BlogVo;
import com.blog.mapper.BlogReponsitory;
import com.blog.util.CommUtils;
import com.blog.util.DateUtil;
import com.blog.util.PageBo;
import com.blog.util.PageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huang hao
 * @version 1.0
 * @date 2021/1/25 9:53
 */
@Slf4j
@Service
public class BlogService {
    @Autowired
    private BlogReponsitory blogReponsitory;


    public PageVo<BlogVo> findAllAndPage(PageBo<TBlog> pageBo) {

        String process = "业务层处理";
        log.info("{} 入参：body={}", process, JSON.toJSONString(pageBo));
        Page<TBlog> all = blogReponsitory.findAll((r, cq, cb) ->{
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(pageBo.getParam().getTitle())) predicates.add(cb.like(r.get("title").as(String.class),"%" + pageBo.getParam().getTitle() +"%"));
            //cq.orderBy(cb.desc(r.get("id")));
            cq.orderBy(cb.desc(r.get("updateTime")));
            Predicate[] pred = new Predicate[predicates.size()];
            return cb.and(predicates.toArray(pred));
        }, PageRequest.of(pageBo.getPage() - 1, pageBo.getSize()));
        //log.info("{} 出参：all={}", process, JSON.toJSONString(all));
        PageVo<BlogVo> pageVo = new PageVo<>();
        pageVo.setPage(all.getTotalPages());
        pageVo.setSize(all.getSize());
        pageVo.setTotal(all.getTotalElements());
        pageVo.setHasNextPage(all.hasNext());
        List<BlogVo> reslut = all.getContent().stream().map(c-> {
            BlogVo blogVo = new BlogVo();
            blogVo.setId(c.getId());
            blogVo.setTop(c.getIsTop());
            blogVo.setBanner(c.getBanner());
            blogVo.setHot(c.getIsHot());
            blogVo.setPubTime(DateUtil.formatDate(c.getUpdateTime(),DateUtil.FMT2));
            blogVo.setTitle(c.getTitle());
            blogVo.setSummary(c.getSummary());
            blogVo.setContent(c.getContent());
            blogVo.setViewsCount(c.getViewsCount());
            blogVo.setCommentsCount(c.getCommentsCount());
            return blogVo;
                }
        ).filter(c-> CommUtils.isNotNull(c)).collect(Collectors.toList());
        pageVo.setItems(reslut);
        //log.info("{} 最终结果：body={}", process, JSON.toJSONString(pageVo));
        return pageVo;
    }

    public TBlog getBlogById(Long id) {
        Optional<TBlog> byId = blogReponsitory.findById(id);
        TBlog blog = byId.get();
        if (ObjectUtils.isEmpty(blog)) return null;
        log.info("{} 数据库查询结果：blog={}", JSON.toJSONString(blog));
        return blog;
    }
}
