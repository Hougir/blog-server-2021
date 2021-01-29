package com.blog.util;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 公共工具类
 */
public class CommUtils {

	/**
	 * equals
	 *
	 * @param v1
	 * @param v2
	 * @return
	 *
	 * date: 2020/6/28 14:32
	 */
	public static boolean equals(Object v1, Object v2){
		if(v1 == null && v2 == null){
			return true;
		}
		if(v1 == null){
			return false;
		} else {
			return v1.equals(v2);
		}
	}
	/**
	 * not equals
	 *
	 * @param v1
	 * @param v2
	 * @return
	 *
	 * date: 2020/6/28 14:32
	 */
	public static boolean equalsNot(Object v1, Object v2){
		return !equals(v1, v2);
	}

	public static <T> T getByDefault(T obj, T defaultValue){
		if(obj == null){
			return defaultValue;
		}
		return obj;
	}
	public static <P,T> T getByDefault(P obj, Function<P, T> fun, T defaultValue) {
		if (obj == null) {
			return defaultValue;
		}
		return fun == null?defaultValue:fun.apply(obj);
	}
	public static <T> T getNotNullByDefault(T obj, T defaultValue) {
		if (isNull(obj)) {
			return defaultValue;
		}
		return obj;
	}
	public static <P,T> T getNotNullByDefault(P obj, Function<P, T> fun, T defaultValue) {
		if (isNull(obj)) {
			return defaultValue;
		}
		return fun == null?defaultValue:fun.apply(obj);
	}

	/**
	 * 比较版本号
	 * @param v1
	 * @param v2
	 * @return 0代表相等，1代表左边大，-1代表右边大
	 */
	public static int compareAppVersion(String v1, String v2) {
		if (v1 == null || "".equals(v1)
				|| v2 == null || "".equals(v2) ) {
			throw new RuntimeException("version must not be null");
		}
		if (v1.equals(v2)) {
			return 0;
		}
		// 支持
		String[] version1Array = v1.split("[._]");
		String[] version2Array = v2.split("[._]");
		int index = 0;
		int minLen = Math.min(version1Array.length, version2Array.length);
		long diff = 0;

		while (index < minLen
				&& (diff = Long.parseLong(version1Array[index])
				- Long.parseLong(version2Array[index])) == 0) {
			index++;
		}
		if (diff == 0) {
			for (int i = index; i < version1Array.length; i++) {
				if (Long.parseLong(version1Array[i]) > 0) {
					return 1;
				}
			}

			for (int i = index; i < version2Array.length; i++) {
				if (Long.parseLong(version2Array[i]) > 0) {
					return -1;
				}
			}
			return 0;
		} else {
			return diff > 0 ? 1 : -1;
		}
	}

	/**
	 * 对字符串处理:将指定位置到指定位置的字符以星号代替
	 * @param content 传入的字符串
	 * @param begin 开始位置
	 * @param end 结束位置
	 * @return
	 */
	public static String getStarString(String content, int begin, int end) {
		if (begin >= content.length() || begin < 0) {
			return content;
		}
		if (end >= content.length() || end < 0) {
			return content;
		}
		if (begin >= end) {
			return content;
		}
		String starStr = "";
		for (int i = begin; i < end; i++) {
			starStr = starStr + "*";
		}
		return content.substring(0, begin) + starStr + content.substring(end, content.length());
	}

	/**
	 * 判断对象是否为NULL
	 *
	 * @param obj 任意对象
	 * @return boolean true 对象为NULL false 对象不为空
	 */
	public static boolean isNull(Object obj) {
		boolean result = false;
		if (obj != null) {
			if (obj instanceof String) {
				if (((String) obj).trim().isEmpty()) {
					result = true;
				}
			} else if (obj instanceof Collection) {
				if (((Collection) obj).isEmpty()) {
					result = true;
				}
			} else if (obj instanceof Map) {
				if (((Map) obj).isEmpty()) {
					result = true;
				}
			} else if (obj.getClass().isArray()) {
				if (Array.getLength(obj) <= 0) {
					return true;
				}
			}

		} else {
			result = true;
		}
		return result;
	}

	public static boolean isNotNull(Object obj){
		return !isNull(obj);
	}

	/**
	 * 获得一个UUID
	 *
	 * @return String UUID
	 */
	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		// 去掉“-”符号
		// return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
		return s.replace("-", "");
	}

	/**
	 * MD5加密
	 *
	 * @param source 待产生MD5的byte数组
	 * @return String MD5值
	 */
	public static String getMD5(byte[] source) {
		String s = null;
		// 用来将字节转换成16进制表示的字符
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest();
			char ch[] = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				byte byte0 = tmp[i];
				ch[k++] = hexDigits[byte0 >>> 4 & 0xf];
				ch[k++] = hexDigits[byte0 & 0xf];
			}
			s = new String(ch);
		} catch (Exception ex) {
			Logger.getLogger(CommUtils.class.getName()).log(Level.SEVERE, null, ex);
		}
		return s;
	}

	/**
	 * 根据给定的日期格式将日期字符串解析为日期对象
	 *
	 * @param dateString 日期字符串
	 * @param pattern 给定的日期格式,如果为NULL则默认使用yyyy-MM-dd
	 * @return Date 解析后的日期
	 */
	public static Date convertStringToDate(String dateString, String pattern) {
		Date date = null;
		if (pattern == null || pattern.trim().equals("")) {
			pattern = "yyyy-MM-dd";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			date = sdf.parse(dateString);
		} catch (ParseException ex) {
			Logger.getLogger(CommUtils.class.getName()).log(Level.SEVERE, null, ex);
		}
		return date;
	}

	/**
	 * 根据给定的日期格式将日期解析为日期字符串
	 *
	 * @param date 日期
	 * @param pattern 给定的日期格式,如果为NULL则默认使用yyyy-MM-dd
	 * @return String 解析后的日期字符串
	 */
	public static String convertDateToString(Date date, String pattern) {
		String dateString;
		if (pattern == null || pattern.trim().equals("")) {
			pattern = "yyyy-MM-dd";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		dateString = sdf.format(date);
		return dateString;
	}

	/**
	 * 比较两个日期是否相等
	 *
	 * @param d1 日期
	 * @param d2 日期
	 * @param pattern 给定的日期格式,如果为NULL则默认使用yyyy-MM-dd hh:mm:ss yyyy-MM-dd hh:mm:ss
	 * @return true 相等，false 不等
	 */
	public static boolean compareDate(Date d1, Date d2, String pattern) {
		boolean result = false;
		if (d1 != null && d2 != null) {
			String date1 = convertDateToString(d1, pattern);
			String date2 = convertDateToString(d2, pattern);

			if (date1.equals(date2)) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
	 *
	 * @param v1 被除数
	 * @param v2 除数
	 * @param scale 表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 正则表达式验证
	 *
	 * @param dataType 1:邮箱 2:日期 3:电话号码 4:数字
	 * @param data
	 * @return
	 */
	public static boolean validate(int dataType, String data) {
		String regexStr = "";
		switch (dataType) {
			case 1:
				//regexStr = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
				//regexStr = "^([a-z0-9A-Z]+[-_\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+([a-z0-9A-Z\\-]+)?\\.)+[a-zA-Z]{2,}$";
				regexStr = "^[A-Za-z0-9]+([-_\\.][A-Za-z0-9]+)*@([a-z0-9A-Z]+([a-z0-9A-Z\\-]+)?\\.)+[a-zA-Z]{2,}$";
				break;
			case 2:
				regexStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
				break;
			case 3:
				regexStr = "(\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$";
				break;
			case 4:
				regexStr = "-?(0|([1-9][0-9]*))(.[0-9]+)?";
				break;
		}
		Pattern pattern = Pattern.compile(regexStr);
		Matcher matcher = pattern.matcher(data);
		return matcher.matches();
	}


	/**
	 * 验证手机格式
	 * @param mobile
	 * @return
	 */
	public static boolean checkMobile(String mobile){
		String regexStr = "0?1[0-9]{10}";
		if(StringUtils.isBlank(mobile) || !Pattern.matches(regexStr,mobile)){
			return false;
		}
		return true;
	}

	/**
	 * 验证密码格式
	 * @param password
	 * @return
	 */
	public static boolean checkPwd(String password){
		String regexStr = "[a-zA-Z0-9]{6,20}";
		if(StringUtils.isBlank(password) || !Pattern.matches(regexStr,password)){
			return false;
		}
		return true;
	}

	/**
	 * 生成随机多位数字
	 *
	 * @return
	 */
	public static String getRandomNumber(int num) {
		Random random = new Random();
		StringBuilder randomCode = new StringBuilder();
		for (int i = 0; i < num; i++) {
			// 得到随机产生的验证码数字。
			String strRand = String.valueOf(random.nextInt(10));
			// 将产生的多个随机数组合在一起。
			randomCode.append(strRand);
		}
		return randomCode.toString();
	}

	public static String formatterPhone(String phone) {
		if (!CommUtils.isNull(phone)) {

			return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
		}
		return "";
	}

	public static String formatterIdCard(String idCard) {
		if (!CommUtils.isNull(idCard)) {

			return idCard.substring(0, 3) + "****" + idCard.substring(idCard.length() - 4);
		}
		return "";
	}

	public static String toString(Object obj) {
		if (obj != null) {
			return obj.toString();
		} else {
			return "";
		}
	}

	/**
	 * 判断是否有null对象
	 * @param objs
	 * @author yanxuewen
	 * date 2019-01-28 17:30
	 * @return
	 */
	public static boolean hasNull(Object ... objs) {
		for (Object obj : objs) {
			if (obj == null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 强转判空
	 * @param obj
	 * @return
	 *
	 * date: 2019/9/2 10:49
	 */
	public static <R> R cast(Object obj){
		if(obj == null){
			return null;
		}
		return (R)obj;
	}

	public static int getSafeInt(Integer integer){
		return integer == null ? 0 : integer;
	}


	/**
	 * 将字符串首字母大写
	 * @param str
	 * @return
	 */
	public static String upperCase(String str) {
		char[] ch = str.toCharArray();
		if (ch[0] >= 'a' && ch[0] <= 'z') {
			ch[0] = (char) (ch[0] - 32);
		}
		return new String(ch);
	}

	public static String maxLength(String val, int maxLength){
		if(val == null || val.length() < maxLength){
			return val;
		}
		return val.substring(0, maxLength);
	}

	/**
	 * 判断对象名称开头和结尾不能为空字符
	 * @param obj
	 * @return 存在" "返回true，不存在" "返回false
	 */
	public static boolean checkFirstLastNull(String obj){
		if (CommUtils.isNull(obj)){
			return true;
		}
		String regEx ="^(?!\\s)(?!.*\\s$)";
		if (Pattern.compile(regEx).matcher(obj).find()){
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断是否是姓名
	 * @param name
	 * @return
	 */
	public static boolean checkName(String name) {
		String regex = "^[ a-z0-9A-Z]+$";
		if(!CommUtils.isNull(name)) {
			return name.matches(regex);
		}
		return false;
	}

	/**
	 * 判断是否由字母或数字
	 * @param str
	 * @return
	 */
	public static boolean isLetterDigit(String str) {
		String regex = "^[a-z0-9A-Z]+$";
		if(!CommUtils.isNull(str)) {
			return str.matches(regex);
		}
		return false;
	}
}
