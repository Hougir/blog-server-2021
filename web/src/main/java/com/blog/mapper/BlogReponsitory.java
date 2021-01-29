package com.blog.mapper;

import com.blog.domain.entity.TBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author huang hao
 * @version 1.0
 * @date 2021/1/25 9:51
 */
@Repository
public interface BlogReponsitory extends JpaRepository<TBlog,Long>, JpaSpecificationExecutor<TBlog> {
}
