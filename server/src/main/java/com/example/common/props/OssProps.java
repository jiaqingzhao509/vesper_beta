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
@ConfigurationProperties("oss")
public class OssProps {

    /**
     * 文件上传地址
     */
    private String uploadPath = System.getProperty("user.dir") + "/target/classes/static/upload";

    /**
     * 文件访问地址
     */
    private String remotePath = "http://127.0.0.1:8090/upload";
}
