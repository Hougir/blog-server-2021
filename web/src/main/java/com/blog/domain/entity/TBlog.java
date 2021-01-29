package com.blog.domain.entity;
import javax.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import lombok.*;
import java.io.Serializable;
import java.util.Date;
/** 
 * @Author huanghao 
 * @Date 2021-01-25 09:41:04 
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
@Table ( name ="t_blog" )
public class TBlog  implements Serializable {

	private static final long serialVersionUID =  790095960579406685L;

   	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

   	/*标题*/
   	@Column(name = "title" )
	private String title;

   	/*概要*/
   	@Column(name = "summary" )
	private String summary;

   	/*首图*/
   	@Column(name = "banner" )
	private String banner;

   	/*是否置顶 1 true置顶*/
   	@Column(name = "is_top" )
	private Boolean isTop;

   	/*是否热门*/
   	@Column(name = "is_hot" )
	private Boolean isHot;

   	/*是否已发表*/
   	@Column(name = "published" )
	private Boolean published;

   	/*评论数*/
   	@Column(name = "comments_count" )
	private Long commentsCount;

   	/*观看次数*/
   	@Column(name = "views_count" )
	private Long viewsCount;

   	/**/
   	@Column(name = "flag" )
	private String flag;

   	/*股份声明*/
   	@Column(name = "share_statement" )
	private Boolean shareStatement;

   	/*分类id*/
   	@Column(name = "type_id" )
	private Long typeId;

   	/*创建id*/
   	@Column(name = "user_id" )
	private Long userId;

   	/*具体内容*/
   	@Column(name = "content" )
	private String content;

   	@Column(name = "create_time" )
	private Date createTime;

   	@Column(name = "update_time" )
	private Date updateTime;
}
