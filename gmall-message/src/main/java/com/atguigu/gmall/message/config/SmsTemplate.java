package com.atguigu.gmall.message.config;

import com.atguigu.core.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class SmsTemplate {
	@Value("${sms.host}")
	private String host;
	@Value("${sms.path}")
	private String path;
	@Value("${sms.method}")
	private String method;
	@Value("${sms.appcode}")
	private String appcode;

	public boolean sendCode(Map<String, String> querys) {

		//log.debug("开始发送短信-参数：{}", querys);
//		String host = "http://dingxin.market.alicloudapi.com";
//		String path = "/dx/sendSms";
//		String method = "POST";
//		String appcode = "你自己的AppCode";
		Map<String, String> headers = new HashMap<>();
		// 最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
		headers.put("Authorization", "APPCODE " + appcode);
//		Map<String, String> querys = new HashMap<String, String>();
//		querys.put("mobile", "159xxxx9999");
//		querys.put("param", "code:1234");
//		querys.put("tpl_id", "TP1711063");
		Map<String, String> bodys = new HashMap<>();

		try {
			/**
			 * 重要提示如下: HttpUtils请从
			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
			 * 下载
			 *
			 * 相应的依赖请参照
			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
			 */
			HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
			System.out.println(response.toString());
			// 获取response的body
			//System.out.println(EntityUtils.toString(response.getEntity()));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
