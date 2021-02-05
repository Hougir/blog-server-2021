package com.blog.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author huang hao
 * @version 1.0
 * @date 2021/2/3 17:28
 */
@Data
@AllArgsConstructor
public class Sms implements Serializable {
    private String phone;
    private String content;
}
