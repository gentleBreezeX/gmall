package com.atguigu.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommonUtils {

    //生成订单号
	public static String getOrderNum() {
		SimpleDateFormat allTime = new SimpleDateFormat("YYYYMMddHHmmSSS");
		String subjectno = allTime.format(new Date()) + UUID.randomUUID().toString().replace("-", "").substring(0,10);
		return subjectno;
	}


	// 验证手机格式的方法
	public static boolean isMobilePhone(String phone) {
		boolean flag = true;
		String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
		if (phone.length() != 11) {
			flag = false;
		} else {
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(phone);
			flag = m.matches();
		}

		return flag;
	}
}
