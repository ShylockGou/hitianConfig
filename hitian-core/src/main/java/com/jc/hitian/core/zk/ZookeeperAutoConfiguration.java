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

package com.jc.hitian.core.zk;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * @author Spencer Gibb
 */
@Configuration
@Slf4j
public class ZookeeperAutoConfiguration {

    @Resource
    Environment environment;

    @Autowired(required = false)
    private EnsembleProvider ensembleProvider;

    @Bean
    public ZookeeperProperties zookeeperProperties () {
        ZookeeperProperties properties = new ZookeeperProperties ();
        properties.setEnabled ( environment.getProperty ( "hitian.application.config.zookeeper.enabled",Boolean.class ) );
        properties.setConnectString ( environment.getProperty ( "hitian.application.config.zookeeper.connectString" ) );
        properties.setBaseSleepTimeMs ( environment.getProperty ( "hitian.application.config.zookeeper.baseSleepTimeMs" ,Integer.class) );
        properties.setMaxRetries ( environment.getProperty ( "hitian.application.config.zookeeper.maxRetries",Integer.class ) );
        properties.setMaxSleepMs ( environment.getProperty ( "hitian.application.config.zookeeper.maxSleepMs", Integer.class ) );
        properties.setBlockUntilConnectedWait ( environment.getProperty ( "hitian.application.config.zookeeper.blockUntilConnectedWait", Integer.class ));
        return properties;
    }

    @Bean
    public RetryPolicy exponentialBackoffRetry () {

        return new ExponentialBackoffRetry ( zookeeperProperties ().getBaseSleepTimeMs (),
                                             zookeeperProperties ().getMaxRetries (),
                                             zookeeperProperties ().getMaxSleepMs () );
    }

    @Bean(destroyMethod = "close")
    @SneakyThrows
    public CuratorFramework curatorFramework ( RetryPolicy retryPolicy, ZookeeperProperties properties ) {

        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder ();
        if ( ensembleProvider != null ) {
            builder.ensembleProvider ( ensembleProvider );
        }
        CuratorFramework curator = builder
                .retryPolicy ( retryPolicy )
                .connectString ( zookeeperProperties ().getConnectString () )
                .build ();
        curator.start ();

        log.trace ( "blocking until connected to zookeeper for " +
                    properties.getBlockUntilConnectedWait () +
                    properties.getBlockUntilConnectedUnit () );
        curator.blockUntilConnected ( properties.getBlockUntilConnectedWait (),
                                      properties.getBlockUntilConnectedUnit () );
        log.trace ( "connected to zookeeper" );
        return curator;
    }
}
