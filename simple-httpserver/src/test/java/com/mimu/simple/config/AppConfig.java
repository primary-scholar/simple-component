package com.mimu.simple.config;

import com.mimu.simple.SimpleHttpServerTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses= SimpleHttpServerTest.class)
public class AppConfig {
}
