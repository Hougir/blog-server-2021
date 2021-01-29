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
 * @Date 2021-01-28 16:49:23 
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
@Table ( name ="t_user" )
public class TUser  implements Serializable {

	private static final long serialVersionUID =  6293516683641343109L;

   	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

   	@Column(name = "avatar" )
	private String avatar;

   	@Column(name = "create_time" )
	private Date createTime;

   	@Column(name = "email" )
	private String email;

   	@Column(name = "nickname" )
	private String nickname;

   	@Column(name = "password" )
	private String password;

   	@Column(name = "type" )
	private Long type;

   	@Column(name = "update_time" )
	private Date updateTime;

   	@Column(name = "username" )
	private String username;

   	@Column(name = "openid" )
	private String openid;
}
