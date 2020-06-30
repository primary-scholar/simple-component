package com.mimu.simple.zkreference;

import com.mimu.simple.zkreference.annotation.EnableZKCenter;
import com.mimu.simple.zkreference.zkconfig.ZKConfigResource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * author: mimu
 * date: 2018/12/4
 */
@Configuration
@ComponentScan(basePackageClasses = ZKPropertyModel.class)
@EnableZKCenter(proxyTargetClass = true)
@Import(value = ZKConfigResourceConfig.class)
public class ApplicationConfig {

}
