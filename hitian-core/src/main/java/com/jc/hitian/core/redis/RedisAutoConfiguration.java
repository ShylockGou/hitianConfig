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

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;

/**
 * @author Spencer Gibb
 */
@Configuration
@Slf4j
public class RedisAutoConfiguration {

    @Resource
    Environment environment;

    @Bean
    public RedisProperties redisProperties () {

        RedisProperties properties = new RedisProperties ();
        properties.setHost ( environment.getProperty ( "hitian.application.config.redis.host" ) );
        properties.setPort ( environment.getProperty ( "hitian.application.config.redis.port" ,Integer.class) );
        properties.setDatabase ( environment.getProperty ( "hitian.application.config.redis.database",Integer.class ) );
        properties.setUsePool ( environment.getProperty ( "hitian.application.config.redis.usePool", Boolean.class ) );
        return properties;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory (RedisProperties properties) {

        JedisConnectionFactory factory = new JedisConnectionFactory ();
        factory.setHostName ( properties.getHost () );
        factory.setPort ( properties.getPort () );
        factory.setDatabase ( properties.getDatabase () );
        factory.setUsePool ( properties.isUsePool () );
        JedisPoolConfig config = new JedisPoolConfig ();
        factory.setPoolConfig ( config );
        return factory;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate ( JedisConnectionFactory jedisConnectionFactory ) {

        StringRedisTemplate redisTemplate = new StringRedisTemplate ();
        redisTemplate.setConnectionFactory ( jedisConnectionFactory );
        return redisTemplate;
    }

}
