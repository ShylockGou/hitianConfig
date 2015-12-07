package com.jc.hitian.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {"com.jc.hitian.core"})
@PropertySource(value = "classpath:hitian.properties")
public class PropertySourceBootstrapConfiguration {


}
