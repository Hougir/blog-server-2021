package com.blog.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @PACKAGE_NAME: com.xpwi.springboot
 * @NAME: BlogVo
 * @AUTHOR: 如意郎君
 * @DATE: 2021-01-23
 * @TIME: 15:19
 * @DAY_NAME_SHORT: 星期六
 * @VERSION: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BlogVo {
    private Long id;
    private boolean isTop;
    private String banner;
    private boolean isHot;
    //@JsonFormat(pattern = "yyyy-mm-yy")
    //private Date pubTime= new Date();
    private String pubTime;
    private String title;
    private String summary;
    private String content;
    private Long viewsCount;
    private Long commentsCount;
}
