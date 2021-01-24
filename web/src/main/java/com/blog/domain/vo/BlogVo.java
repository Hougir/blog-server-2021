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
    private Integer id = 1;
    private boolean isTop= true;
    private String banner="https://s1.ax1x.com/2020/05/14/YDhagx.jpg";
    private boolean isHot=true;
    //@JsonFormat(pattern = "yyyy-mm-yy")
    //private Date pubTime= new Date();
    private String pubTime= "2020-01-23";
    private String title= "看一遍闭着眼都会安装Lua了";
    private String summary= "Lua 是一种轻量小巧的脚本语言，能为应用程序提供灵活的扩展和定制功能。";
    private String content= "<p>I am testing data, I am testing data.</p><p><img src='https://wpimg.wallstcn.com/4c69009c-0fd4-4153-b112-6cb53d1cf943'></p>";
    private Integer viewsCount=4045;
    private Integer commentsCount = 99;
}
