package com.example.common.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 本地文件上传配置
 *
 * @author tycoding
 * @since 2021/6/3
 */
@Data
@ConfigurationProperties("auth")
public class AuthProps {

    private String apiKey;
    private String modelName;
    private String googleKey;
    private String googleSecret;
    private String webUrl;
    private String redirectUrl;
    private String chartUrl;
    private Integer limit = 30;
}
