package com.blog.util;

/**
 * //生成随机码
 * @PACKAGE_NAME: top.yellowhao.p2p.util
 * @NAME: GetGenerateCode
 * @AUTHOR: 如意郎君
 * @DATE: 2020/10/19
 * @TIME: 19:52
 * @DAY_NAME_SHORT: 星期一
 * @VERSION: 1.0
 */
public class GetGenerateCode {
    //生成随机码
    public static String generateCode(int size) {
        //Math.round( Math.random()*100000);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            stringBuilder.append(Math.round(Math.random() * 9));
        }
        return stringBuilder.toString();
    }
}
