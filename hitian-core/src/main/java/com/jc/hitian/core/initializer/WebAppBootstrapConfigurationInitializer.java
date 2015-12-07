package com.jc.hitian.core.initializer;

import com.jc.hitian.core.config.PropertySourceBootstrapContextInitializer;
import org.springframework.util.StringUtils;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import static org.springframework.web.context.ContextLoader.GLOBAL_INITIALIZER_CLASSES_PARAM;

public class WebAppBootstrapConfigurationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup (ServletContext container ) throws ServletException {
        String initializerClasses = container.getInitParameter(GLOBAL_INITIALIZER_CLASSES_PARAM);

        String propertySourceInitClassName = PropertySourceBootstrapContextInitializer.class.getName ();

        if ( StringUtils.hasText ( initializerClasses )) {
            initializerClasses += " " + propertySourceInitClassName;
        }
        else {
            initializerClasses = propertySourceInitClassName;
        }

        container.setInitParameter (GLOBAL_INITIALIZER_CLASSES_PARAM, initializerClasses);

    }

}
