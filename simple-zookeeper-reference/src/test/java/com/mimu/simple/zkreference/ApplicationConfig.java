package com.mimu.simple.zkreference;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * author: mimu
 * date: 2018/12/4
 */
@Configuration
@ComponentScan(basePackageClasses = ZKPropertyModel.class)
@EnableZKCenter(proxyTargetClass = true)
public class ApplicationConfig {
}
