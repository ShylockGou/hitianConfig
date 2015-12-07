package com.jc.hitian.core.redis;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Spencer Gibb
 */
@Slf4j
public class RedisPropertySource extends EnumerablePropertySource< RedisTemplate > {

    @Getter
    private String        context;

    private HashOperations<String,String,String> hashOps;

    private LoadingCache<String, String> cache ;
    public RedisPropertySource ( String context, RedisTemplate source ) {
        super ( context, source );
        this.context = context;
        this.hashOps = source.opsForHash ();
        buildCache();
    }

    private void buildCache () {
        cache = CacheBuilder.newBuilder ().expireAfterWrite ( 60, TimeUnit.SECONDS )
                            .build ( new CacheLoader< String, String > () {
            @Override
            public String load ( String key ) throws Exception {
                String data = hashOps.get ( getContext (), key );
                return data;
            }
        } );
        Map keyValue = hashOps.entries ( getContext () );

        cache.putAll ( keyValue );
    }


    @Override
    public Object getProperty ( final String name ) {
        try {
            return cache.get ( name );
        } catch ( Exception e ) {
            return null;
        }
    }

    @Override
    public String[] getPropertyNames () {
        Set<String>    keys = cache.asMap ().keySet ();
        return keys.toArray ( new String[0] );
    }

}
