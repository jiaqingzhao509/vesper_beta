package com.example.common;

import com.example.common.props.AuthProps;
import com.example.common.props.OssProps;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Oss配置注入
 *
 * @author tycoding
 *
 */
@Order
@Configuration
@EnableConfigurationProperties({OssProps.class, AuthProps.class})
public class CoreAutoConfiguration {

}
