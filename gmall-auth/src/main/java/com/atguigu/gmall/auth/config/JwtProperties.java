package com.atguigu.gmall.auth.config;

import com.atguigu.core.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author breeze
 * @date 2019/11/12 16:43
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {

    private String secret; // 密钥

    private String pubKeyPath;// 公钥

    private String priKeyPath;// 私钥

    private int expire;// token过期时间

    private PublicKey publicKey; // 公钥

    private PrivateKey privateKey; // 私钥

    private String cookieName; // cookie名称

    /**
     * @PostConstruct 在构造执行之后执行该方法
     */
    @PostConstruct
    public void init(){

        try {
            File pubKeyFile = new File(pubKeyPath);
            File priKeyFile = new File(priKeyPath);
            //1. 判断路径中是否存在公私钥文件
            if (!priKeyFile.exists() || !pubKeyFile.exists()) {
                //2. 生成公私钥
                RsaUtils.generateKey(pubKeyPath, priKeyPath, secret);
            }
            //3. 获取公钥私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
            this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
        } catch (Exception e) {
            log.error("初始化公私钥失败");
            e.printStackTrace();
        }
    }

}
