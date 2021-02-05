package com.blog.util;

import com.blog.enums.ResultMsg;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
//统一返回结果的类
public class R {

    @ApiModelProperty(value = "是否成功")
    private Boolean success;

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

//    @ApiModelProperty(value = "返回数据")
//    private Map<String, Object> data = new HashMap<String, Object>();

    @ApiModelProperty(value = "应用返回结果", name = "data")
    private Object data;

    //把构造方法私有
    private R() {
    }

    //成功静态方法
    public static R ok() {
        R r = new R();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS);
        r.setMessage("成功");
        return r;
    }

    public static R ok(Object data) {
        R r = new R();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS);
        r.setMessage("成功");
        r.setData(data);
        return r;
    }

    //失败静态方法
    public static R error() {
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR);
        r.setMessage("失败");
        return r;
    }

    public R success(Boolean success){
        this.setSuccess(success);
        return this;
    }

    public R message(String message){
        this.setMessage(message);
        return this;
    }

    public R message(ResultMsg resultMsg){
        this.setMessage(resultMsg.getMsg());
        return this;
    }

    public R code(Integer code){
        this.setCode(code);
        return this;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
