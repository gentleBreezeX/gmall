package com.atguigu.gmall.order.config;

import com.atguigu.core.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * @author breeze
 * @date 2019/11/12 16:43
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {

    private String pubKeyPath;// 公钥地址

    private PublicKey publicKey; // 公钥

    private String cookieName; // cookie名称
    /**
     * @PostConstruct 在构造执行之后执行该方法
     */
    @PostConstruct
    public void init(){

        try {
            //3. 获取公钥私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            log.error("初始化公私钥失败");
            e.printStackTrace();
        }
    }

}
