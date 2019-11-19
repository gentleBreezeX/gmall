package com.atguigu.gmall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2016101200671261";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCHjDgK/wnhGXYPMjJvBcJ829YpSSNKM4zKk1iMTMHXFZfngYw9aJlf75yGZsBF+D2NSJSq0ns5qXkOEI6sxbOu4xK5xvPr1pBSbS2NytjTyj+38URXx/eE/vfMnYLB1qptI80c++HP7ueNlC65QwCGnKXy2qCeWzGRaGMuy7zODYCDMzYrHY2PDG8S9DlDEIEGV13dPS6xBMVXBpxgeEiNkYznDyY8s7ivXj5PuOkgHU7VEQeHBko/N/tjUtZJautl19xQfzXN4qbIWxMcSTsWwKhyHNgpBpO6bWJFS81fA2agBPBEylPuexNxb8MKdxjSVkHecNYI0AyemeQQli0nAgMBAAECggEAXMtvqoeC+el2W1zgDgWBaf0OFmGNmUTFRAGvgw0hPt/3iHO10GyFY5okLBa31q7lZI2lyXQjDAyrZSeMpVcLddTagvhjELdpQiSgJQwTeJpjGMhBI18wYooylo/WbmFJd0IlL4x4wOMq/k/N1OK7v2wKZ8GoaADCZGt0SxsLfHvZ1d99dNkSpY03oAvM31TDBh/hujkmQ+YNNTtKlACqPUQ22m2fpQx8Q91c5EuIAoLiZ2treHQlkvOu2hxDcKun+xwNhPXOzrY/HaHycShR/xQ/pjwQlFNpGKZMy8ynWbn0UjgcR0K9+xVQW0c2GJxd7v24xVRU8WcyCkzObZ43UQKBgQDizjrDRoSyNXh15lM8lon3yRi09gfBa7upHUQ07+Wql2V3mf1c6Q9kI0/UBoWMGh/wSdI2wIwP8seSgvOX/HKIUAnDNnxwUy0ikcdIZvmusA/ShJOaF6cDeaGualou0B1jzS51cfmv5Or5O7+nLtEc2Z2p1oA7miV++HRRVd05WwKBgQCY/tTNYuNVNmhilsXdVK4NkKAVMz2A6mxgsFj4zoex0YbFkyhkiMNOQdkG0q/l7hahNa5Wa0y3rF7e9IHnR9n+auzZYkv3vsNEw7Y+Vllg+K4Uzc03uH+iQrGJ3u3+ypjsoHDmn2Bad3woZw7C8zMZVnKkIgVWPLRqj4vxo3kZJQKBgEm035/qYOFRQy59hXthKMEf1ymn8ulGy5uv6SSS+b4wqUbvAkmZa+kNGLo8zFW1f7+lHe2xMVNVgMn6SJOR9N6btDB+mn4eacKcQXkkDexZRysQ7q7bFOmqM4LtCRXBiGuQmJKOUah56mrIogYAzvBjQDube9ziwWC7+YEdOGGVAoGAfl5FgJtEMBbvYzcrmSOfW77wKnKXQ0rdV4NxCZj1BY+NpNVmkJtRzeqfin4tIVplQKRpKiIYTMFH3xnPzitPyE+i7+671pavDLYmahjCXFEq4C4YagSvD2PM5pbGtyO56gCfIC3V1QNJ/skYrTdZJn171UvwYoljb3y/V0fx9y0CgYBXNCAAqv6m04RTUcxFmY2zzqTXkYsSQJX5sWOlpH8yZb2CKyVt6UPxNfMoB1vV5bVaSzbruKoCHkXL3HV6+p9jka1yhl1nzdInxReCqBSerCm2PJVBTAJk8glor3q0iOyQ81icfQr+aofAPR1buEue4J0RwT/J68ASbxZ/22YS4Q==";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgSM9FnVIkSvVZnBhlZ/6ECAv32QVfr0UDeeYrFO8qLLmwzywt4y4DlGYDJIbbt6RQOx0aCATv1tbejY/eO51YGek5JTp3Xj2JtGK0CpiGs96dghN37FaoMYXkDakpG5vT0SDEjnxhOj5UFWRfIyMkKx2bmIi4sBMOyZ5xbbK67Yb654LUlnSkMlKAdlCs0upfPrtGuJ6n9hlxVkxP/jyGUWxWv0b+4P+l9cVSbfyY6Epu5E4yGu7LsV0OBISyFm60psIuGPoFia2LX+Q9w/6RzKBGT+DKeWu0e2vVxlb9rutM3Z8WXUn51R1LxCWYK5f0HhPHxTZ4Tj2mR8AWjr3pQIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://rc33apygeo.52http.net/api/order/pay/success";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url = null;

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
