package com.jc.hitian.core.redis;

import com.jc.hitian.core.config.PropertySourceLocator;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p/>
 * Created by Shylock on 15/12/3.
 */
public class RedisPropertySourceLocator implements PropertySourceLocator {

    private StringRedisTemplate redisTemplate;

    private RedisConfigProperties properties;

    public RedisPropertySourceLocator ( RedisConfigProperties properties, StringRedisTemplate redisTemplate ) {

        this.properties = properties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public PropertySource< ? > locate ( Environment environment ) {

        if (properties.isEnabled () &&  environment instanceof ConfigurableEnvironment ) {
            ConfigurableEnvironment env      = (ConfigurableEnvironment) environment;
            String                  appName  = env.getProperty ( "hitian.application.name" );
            List< String >          profiles = Arrays.asList ( env.getActiveProfiles () );

            List< String > contexts = new ArrayList<> ();

            String defaultContext = properties.getDefaultContext ();
            if ( defaultContext != null ) {
                contexts.add ( defaultContext );
                addProfiles ( contexts, defaultContext, profiles );
            }
            String baseContext = appName;
            if ( baseContext != null ) {
                contexts.add ( baseContext );
                addProfiles ( contexts, baseContext, profiles );
            }

            CompositePropertySource composite = new CompositePropertySource ( "redis" );

            Collections.reverse ( contexts );

            for ( String propertySourceContext : contexts ) {
                if ( propertySourceContext == null ) {
                    continue;
                }
                PropertySource propertySource = create ( propertySourceContext );
                composite.addPropertySource ( propertySource );
            }
            return composite;
        }
        return null;
    }

    private RedisPropertySource create ( String context ) {

        return new RedisPropertySource ( context, redisTemplate );
    }

    private void addProfiles ( List< String > contexts, String baseContext,
                               List< String > profiles ) {

        for ( String profile : profiles ) {
            contexts.add ( baseContext + properties.getProfileSeparator () + profile );
        }
    }
}
