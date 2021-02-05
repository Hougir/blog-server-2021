package com.blog.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author huanghao 
 * @Date 2021-02-04 11:12:14 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentVo implements Serializable {

	private static final long serialVersionUID =  5762167393936118148L;

	private Long id;

	private String content;

	private String createTime;

	private String email;

	private Boolean unread;
}
