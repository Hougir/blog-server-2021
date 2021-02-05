package com.blog.entity;

import com.blog.util.R;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author huang hao
 * @version 1.0
 * @date 2021/1/20 10:22
 */
@SuppressWarnings("all")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SmsResult implements Serializable {
    private String code;	//string	10000	更多返回参数示例值请参看“错误参照码”
    private boolean charge;	//boolean	false 或 true	false：不扣费 true：扣费
    private long remain;	//1305	数据剩余次数
    private String msg;	//查询成功	更多返回参数示例值请参看“错误参照码”
    private R result;
}
