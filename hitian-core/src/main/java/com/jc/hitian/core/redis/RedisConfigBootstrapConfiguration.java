/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jc.hitian.core.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

/**
 * @author Spencer Gibb
 */
@Configuration
@Import(RedisAutoConfiguration.class)
public class RedisConfigBootstrapConfiguration {

    @Resource
    Environment environment;

    @Bean
    public RedisPropertySourceLocator redisPropertySourceLocator ( RedisConfigProperties properties, StringRedisTemplate template ) {

        return new RedisPropertySourceLocator ( properties, template );
    }

    @Bean
    public RedisConfigProperties redisConfigProperties () {

        RedisConfigProperties properties = new RedisConfigProperties ();

        properties.setEnabled ( environment.getProperty ( "hitian.application.config.redis.enabled",Boolean.class ) );
        properties.setDefaultContext ( environment.getProperty ( "hitian.application.config.redis.defaultContext",String.class ) );
        properties.setProfileSeparator ( environment.getProperty ( "hitian.application.config.redis.profileSeparator",String.class ) );

        return properties;
    }
}
