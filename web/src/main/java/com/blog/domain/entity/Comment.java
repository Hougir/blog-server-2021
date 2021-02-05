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
 * @Date 2021-02-04 11:12:14 
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
@Table ( name ="t_comment" )
public class Comment implements Serializable {

	private static final long serialVersionUID =  5762167393936118148L;

   	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

   	@Column(name = "avatar" )
	private String avatar;

   	@Column(name = "content" )
	private String content;

   	@Column(name = "create_time" )
	private Date createTime;

   	@Column(name = "email" )
	private String email;

   	@Column(name = "nickname" )
	private String nickname;

   	@Column(name = "blog_id" )
	private Long blogId;

   	@Column(name = "parent_comment_id" )
	private Long parentCommentId;

   	@Column(name = "unread" )
	private Boolean unread;
}
