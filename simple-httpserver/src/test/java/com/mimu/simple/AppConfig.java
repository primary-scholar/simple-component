package com.mimu.simple;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses= SimpleHttpServerTest.class)
public class AppConfig {
}
