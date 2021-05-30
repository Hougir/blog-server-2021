package com.blog.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.blog.domain.bo.BlogBo;
import com.blog.domain.bo.CommentBo;
import com.blog.domain.entity.Comment;
import com.blog.domain.entity.TBlog;
import com.blog.domain.vo.BlogVo;
import com.blog.domain.vo.CommentVo;
import com.blog.enums.CacheKey;
import com.blog.enums.ResultMsg;
import com.blog.mapper.BlogReponsitory;
import com.blog.mapper.CommentRepository;
import com.blog.util.*;
//import com.netflix.hystrix.contrib.javanica.utils.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public PageVo<BlogVo> findAllAndPage(PageBo<TBlog> pageBo, String token) {
        String process = "业务层处理";
        log.info("{} 入参：body={}", process, JSON.toJSONString(pageBo));
        PageVo<BlogVo> pageVo = new PageVo<>();
        List<BlogVo> blogVos;
        List<TBlog> blogList;
        if (null == pageBo || pageBo.getPage() < 1 || pageBo.getSize() < 10) {
            pageBo = PageBo.init(pageBo);
        }
        //如果只获取10条数据先从redis获取，没有再查db放入redis
        if (pageBo.getSize() == 10 && pageBo.getPage() == 1){
            blogList = getDataForCache(pageBo, token, pageVo);
        } else {
            blogList = getDataForDb(pageBo, token, pageVo);
        }
        blogVos = blogList.stream().map(c -> {
                    BlogVo blogVo = new BlogVo();
                    blogVo.setId(c.getId());
                    blogVo.setTop(c.getIsTop());
                    blogVo.setBanner(c.getBanner());
                    blogVo.setHot(c.getIsHot());
                    blogVo.setPubTime(DateUtil.formatDate(c.getUpdateTime(), DateUtil.FMT2));
                    blogVo.setTitle(c.getTitle());
                    blogVo.setSummary(c.getSummary());
                    blogVo.setContent(c.getContent());
                    blogVo.setViewsCount(c.getViewsCount());
                    blogVo.setCommentsCount(c.getCommentsCount());
                    return blogVo;
                }
        ).filter(c -> CommUtils.isNotNull(c)).collect(Collectors.toList());
        pageVo.setItems(blogVos);
        //log.info("{} 出参：all={}", process, JSON.toJSONString(all));
        //log.info("{} 最终结果：body={}", process, JSON.toJSONString(pageVo));
        return pageVo;
    }

    private List<TBlog> getDataForDb(PageBo<TBlog> pageBo, String token, PageVo<BlogVo> pageVo) {
        Page<TBlog> all = blogReponsitory.findAll((r, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(pageBo.getParam().getTitle()))
                predicates.add(cb.like(r.get("title").as(String.class), "%" + pageBo.getParam().getTitle() + "%"));
            //cq.orderBy(cb.desc(r.get("id")));
            if (StringUtils.isEmpty(token)) {
                predicates.add(cb.equal(r.get("published").as(Boolean.class), true));
            }
            cq.orderBy(cb.desc(r.get("updateTime")));
            Predicate[] pred = new Predicate[predicates.size()];
            return cb.and(predicates.toArray(pred));
        }, PageRequest.of(pageBo.getPage() - 1, pageBo.getSize()));
        pageVo.setPage(all.getTotalPages());
        pageVo.setSize(all.getSize());
        pageVo.setTotal(all.getTotalElements());
        pageVo.setHasNextPage(all.hasNext());
        stringRedisTemplate.opsForValue().set(CacheKey.BLOG_PAGE_TOTAL.getKey(),String.valueOf(pageVo.getTotal()),60L,TimeUnit.MINUTES);
        return all.getContent();
    }

    private List<TBlog> getDataForCache(PageBo<TBlog> pageBo, String token, PageVo<BlogVo> pageVo) {
        try {
            String json = (String) stringRedisTemplate.opsForHash().get(CacheKey.BLOG_PAGE_LIST.getKey(), "ten");
            List<TBlog> blogList = JSONObject.parseObject(json, new TypeReference<List<TBlog>>() {
            });
            //Page<TBlog> all = json2Object(blogsJson,new TypeReference<Page<TBlog>>(){});
            if (CollectionUtils.isEmpty(blogList)) {
                //并发枪锁
                Boolean setNx = null;
                setNx = stringRedisTemplate.opsForValue().setIfAbsent(CacheKey.BLOG_PAGE_SETNX.getKey(), "lock", 10, TimeUnit.SECONDS);
                //枪锁成功
                if (setNx) {
                    Page<TBlog> all = blogReponsitory.findAll((r, cq, cb) -> {
                        List<Predicate> predicates = new ArrayList<>();
                        if (!StringUtils.isEmpty(pageBo.getParam().getTitle()))
                            predicates.add(cb.like(r.get("title").as(String.class), "%" + pageBo.getParam().getTitle() + "%"));
                        //cq.orderBy(cb.desc(r.get("id")));
                        if (StringUtils.isEmpty(token)) {
                            predicates.add(cb.equal(r.get("published").as(Boolean.class), true));
                        }
                        cq.orderBy(cb.desc(r.get("updateTime")));
                        Predicate[] pred = new Predicate[predicates.size()];
                        return cb.and(predicates.toArray(pred));
                    }, PageRequest.of(pageBo.getPage() - 1, pageBo.getSize()));
                    pageVo.setPage(all.getTotalPages());
                    pageVo.setSize(all.getSize());
                    pageVo.setTotal(all.getTotalElements());
                    pageVo.setHasNextPage(all.hasNext());
                    blogList = all.getContent();
                    //首页放入redis
                    if (pageBo.getPage() == 1 && all.getContent().size() > 0) {
                        stringRedisTemplate.opsForValue().set(CacheKey.BLOG_PAGE_TOTAL.getKey(),String.valueOf(pageVo.getTotal()),60L,TimeUnit.MINUTES);
                        stringRedisTemplate.opsForHash().put(CacheKey.BLOG_PAGE_LIST.getKey(), "ten", JSONObject.toJSONString(all.getContent()));
                    }
                    log.info("首页放入redis成功");
                } else {
                    //枪锁失败
                    Thread.sleep(500);
                    //重新获取redis数据
                    this.findAllAndPage(pageBo, token);
                }
            }
            pageVo.setTotal(Long.parseLong(stringRedisTemplate.opsForValue().get(CacheKey.BLOG_PAGE_TOTAL.getKey())));
            pageVo.setHasNextPage(true);
            return blogList;
        } catch (InterruptedException e) {
            log.error("获取缓存失败，异常信息位：{}",e);
            return null;
        }
    }


    /**
     * JSON转Object对象
     */
    public static <T> Page<T> json2Object2(String json, TypeReference<Page<T>> typeReference) {
        return JSON.parseObject(json, typeReference);
    }

    private Page<TBlog> json2Object(String blogsJson, TypeReference<Page<TBlog>> pageTypeReference) {
        return JSONObject.parseObject(blogsJson, pageTypeReference);
    }

    public BlogVo getBlogById(Long id) {
        Optional<TBlog> byId = blogReponsitory.findById(id);
        TBlog blog = byId.get();
        if (ObjectUtils.isEmpty(blog)) return null;
        blog.setViewsCount((blog.getViewsCount() == null ? 1L : blog.getViewsCount()) + 1L);
        new Thread(() -> {
            blogReponsitory.saveAndFlush(blog);
        }).start();
        log.info("{} 数据库查询结果：blog={}", JSON.toJSONString(blog));
        BlogVo blogVo = new BlogVo();
        blogVo.setId(blog.getId());
        blogVo.setTop(blog.getIsTop());
        blogVo.setBanner(blog.getBanner());
        blogVo.setHot(blog.getIsHot());
        blogVo.setPubTime(DateUtil.formatDate(blog.getUpdateTime(), DateUtil.FMT2));
        blogVo.setTitle(blog.getTitle());
        blogVo.setSummary(blog.getSummary());
        blogVo.setContent(blog.getContent());
        blogVo.setViewsCount(blog.getViewsCount());
        blogVo.setCommentsCount(blog.getCommentsCount());
        blogVo.setPublished(blog.getPublished());
        return blogVo;
    }

    public R save(TBlog blog) {
        log.info("保存 blog：={}", JSON.toJSONString(blog));
        if (ObjectUtils.isEmpty(blog.getTitle()) || ObjectUtils.isEmpty(blog.getContent())
                || ObjectUtils.isEmpty(blog.getIsTop()) || ObjectUtils.isEmpty(blog.getIsHot())
                || ObjectUtils.isEmpty(blog.getBanner()) || ObjectUtils.isEmpty(blog.getSummary()))
            return R.error().message("参数不能为空");
        Date date = new Date();
        if (ObjectUtils.isEmpty(blog.getId())) {
            blog.setCreateTime(date);
        }
        blog.setUpdateTime(date);
        TBlog tBlog = blogReponsitory.saveAndFlush(blog);
        if (null == tBlog) return R.error().message(ResultMsg.SAVE_FAILED);
        return R.ok();
    }


    public R delById(Long id) {
        if (ObjectUtils.isEmpty(id)) return R.error().message(ResultMsg.PARMS_NOT_NULL);
        try {
            blogReponsitory.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error().message(ResultMsg.FAILED_TO_DELETE);
        }
        return R.ok();
    }


    public List<BlogVo> getFocusList() {
        List<TBlog> all = blogReponsitory.findAll((r, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(r.get("published").as(Boolean.class), true));
            predicates.add(cb.equal(r.get("isHot").as(Boolean.class), true));
            //predicates.add(cb.equal(r.get("isTop").as(Boolean.class), true));
            cq.orderBy(cb.desc(r.get("updateTime")));
            Predicate[] pred = new Predicate[predicates.size()];
            return cb.and(predicates.toArray(pred));
        });
        if (all.size() < 5) {
            all = blogReponsitory.findAll((r, cq, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(r.get("published").as(Boolean.class), true));
                cq.orderBy(cb.desc(r.get("updateTime")));
                Predicate[] pred = new Predicate[predicates.size()];
                return cb.and(predicates.toArray(pred));
            });
        }
        List<BlogVo> blogVos = all.stream().filter(b -> CommUtils.isNotNull(b))
                .map(b -> {
                    BlogVo blogVo = new BlogVo();
                    blogVo.setId(b.getId());
                    blogVo.setTitle(b.getTitle());
                    blogVo.setBanner(b.getBanner());
                    return blogVo;
                }).collect(Collectors.toList());

        return blogVos.subList(0, 5);
    }

    public R comment(Comment comment) {
        if (null == comment || null == comment.getEmail() || null == comment.getContent())
            return R.error().message(ResultMsg.PARMS_NOT_NULL);
        comment.setUnread(true);
        comment.setCreateTime(new Date());
        Comment save = commentRepository.save(comment);
        return R.ok();
    }

    public R commentList(PageBo<CommentBo> bo, String token) {
        if (!JwtUtils.checkToken(token)) return R.error().message(ResultMsg.NOT_LOGIN).code(403);

        List<Sort.Order> orderList = new ArrayList<>();
        orderList.add(Sort.Order.desc("unread"));
        orderList.add(Sort.Order.desc("createTime"));
        Page<Comment> all = commentRepository.findAll((r, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (bo.getParam().isUnread()) {
                predicates.add(cb.equal(r.get("unread").as(Boolean.class), true));
            }
            Predicate[] pred = new Predicate[predicates.size()];
            return cb.and(predicates.toArray(pred));
        }, PageRequest.of(bo.getPage() - 1, bo.getSize(), Sort.by(orderList.toArray(new Sort.Order[orderList.size()]))));
        PageVo<CommentVo> pageVo = new PageVo<>();
        pageVo.setPage(all.getTotalPages());
        pageVo.setSize(all.getSize());
        pageVo.setTotal(all.getTotalElements());
        pageVo.setHasNextPage(all.hasNext());
        List<CommentVo> reslut = all.getContent().stream().map(c -> {
                    CommentVo commentVo = new CommentVo();
                    commentVo.setId(c.getId());
                    commentVo.setCreateTime(DateUtil.formatDate(c.getCreateTime(), DateUtil.FMT2));
                    commentVo.setContent(c.getContent());
                    commentVo.setEmail(c.getEmail());
                    commentVo.setUnread(c.getUnread());
                    return commentVo;
                }
        ).filter(c -> CommUtils.isNotNull(c)).collect(Collectors.toList());
        pageVo.setItems(reslut);
        log.info("最终结果：pageVo={}", JSON.toJSONString(pageVo));
        return R.ok(pageVo);
    }

    public R haveRead(Long id, String token) {
        if (!JwtUtils.checkToken(token)) return R.error().message(ResultMsg.NOT_LOGIN).code(403);
        if (ObjectUtils.isEmpty(id)) return R.error().message(ResultMsg.PARMS_NOT_NULL);
        Comment comment = commentRepository.findById(id).get();
        if (comment.getUnread()) {
            comment.setUnread(false);
        }
        comment = commentRepository.saveAndFlush(comment);
        return R.ok();
    }

    public R delCommentById(Long id, String token) {
        if (!JwtUtils.checkToken(token)) return R.error().message(ResultMsg.NOT_LOGIN).code(403);
        if (CommUtils.isNull(id)) return R.error().message(ResultMsg.PARMS_NOT_NULL);
        //先把该评论有关子级关联删除
        List<Comment> comments = commentRepository.findAllByParentCommentId(id);
        comments = comments.stream().map(c -> {
            c.setParentCommentId(null);
            return c;
        }).filter(c -> CommUtils.isNotNull(c)).collect(Collectors.toList());
        commentRepository.saveAll(comments);
        commentRepository.deleteById(id);
        return R.ok();
    }
}
