package com.jc.hitian.core;

import org.apache.curator.framework.CuratorFramework;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class Application {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CuratorFramework curatorFramework;

    @Test
    public void testValueGet () {

        ValueOperations< String, String > valueOps = stringRedisTemplate.opsForValue ();
        valueOps.set ( "hello", "word" );
        String value = valueOps.get ( "hello" );
        Assert.assertTrue ( value.endsWith ( "word" ) );
    }

    @Test
    public void testHashGet () {

        HashOperations< String, String, String > hashOps = stringRedisTemplate.opsForHash ();
        String                                   value   = hashOps.get ( "application", "application" );
        Assert.assertTrue ( value.endsWith ( "config" ) );
    }

    @Test
    public void testZkGet () {
        try {
            Object children = curatorFramework.getChildren ().forPath ( "/config/null/msg" );
            assert children != null;

            byte[] data = curatorFramework.getData ().forPath ( "/config/null/msg" );
            assert data.length != 0;
            String value = new String ( data );
            assert value.equals ( "hi" );
        } catch ( Exception e ) {
            e.printStackTrace ();
        }
    }
}
