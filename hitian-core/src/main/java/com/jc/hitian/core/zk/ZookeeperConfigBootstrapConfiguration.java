package com.jc.hitian.core.zk;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * @author Spencer Gibb
 */
@Configuration
@Import(ZookeeperAutoConfiguration.class)
public class ZookeeperConfigBootstrapConfiguration {

    @Resource
    Environment environment;

    @Bean
    public ZookeeperPropertySourceLocator zookeeperPropertySourceLocator ( CuratorFramework curator,
                                                                           ZookeeperConfigProperties properties ) {
        return new ZookeeperPropertySourceLocator ( curator, properties );
    }

    @Bean
    public ZookeeperConfigProperties zookeeperConfigProperties () {

        ZookeeperConfigProperties properties =  new ZookeeperConfigProperties ();

        properties.setEnabled ( environment.getProperty ( "hitian.application.config.zookeeper.enabled",Boolean.class ) );
        properties.setRoot ( environment.getProperty ( "hitian.application.config.zookeeper.root",String.class ) );
        properties.setDefaultContext ( environment.getProperty ( "hitian.application.config.zookeeper.defaultContext",String.class ) );
        properties.setProfileSeparator ( environment.getProperty ( "hitian.application.config.zookeeper.profileSeparator",String.class ) );
        properties.setCacheEnabled ( environment.getProperty ( "hitian.application.config.zookeeper.cacheEnabled",Boolean.class ) );
        return properties;
    }
}
