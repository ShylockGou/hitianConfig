package com.jc.hitian.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
public class PropertySourceBootstrapContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

	private static final String BOOTSTRAP_PROPERTY_SOURCE_NAME = "bootstrap";

	private int order = Ordered.HIGHEST_PRECEDENCE + 10;

	private List<PropertySourceLocator> propertySourceLocators = null;//= new ArrayList<>();

	@Override
	public int getOrder() {
		return this.order;
	}

	public void setPropertySourceLocators( Collection<PropertySourceLocator> propertySourceLocators) {
		this.propertySourceLocators = new ArrayList<>(propertySourceLocators);
	}

	@Override
    @Resource
	public void initialize(ConfigurableApplicationContext applicationContext) {
		CompositePropertySource composite = new CompositePropertySource( BOOTSTRAP_PROPERTY_SOURCE_NAME);
        if ( propertySourceLocators == null ) {
            setPropertySourceLocators ( propertySourceLocators ( applicationContext) );
        }
		AnnotationAwareOrderComparator.sort(this.propertySourceLocators);
		boolean empty = true;
		ConfigurableEnvironment environment = applicationContext.getEnvironment();
		for (PropertySourceLocator locator : this.propertySourceLocators) {
			PropertySource<?> source = locator.locate(environment);
			if (source == null) {
				continue;
			}
			log.info("Located property source: " + source);
			composite.addPropertySource(source);
			empty = false;
		}
		if (!empty) {
			MutablePropertySources propertySources = environment.getPropertySources();
			if (propertySources.contains(BOOTSTRAP_PROPERTY_SOURCE_NAME)) {
				propertySources.remove(BOOTSTRAP_PROPERTY_SOURCE_NAME);
			}
			insertPropertySources(propertySources, composite);
            addPlaceholderConfigurerPostProcessor ( applicationContext );
            applicationContext.refresh ();
		}
	}

    private Collection< PropertySourceLocator > propertySourceLocators (ConfigurableApplicationContext applicationContext) {

        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext (PropertySourceBootstrapConfiguration.class);
        Map< String, PropertySourceLocator > locators = ac.getBeansOfType ( PropertySourceLocator.class );
		applicationContext.setParent ( ac );

        return locators.values ();
    }

    private  void addPlaceholderConfigurerPostProcessor ( ConfigurableApplicationContext applicationContext ){
        RemotePlaceholderConfigurerSupport support = new RemotePlaceholderConfigurerSupport ( applicationContext.getEnvironment () );
        applicationContext.addBeanFactoryPostProcessor ( support );
    }
	private void insertPropertySources(MutablePropertySources propertySources,
			CompositePropertySource composite) {
		MutablePropertySources incoming = new MutablePropertySources();
		incoming.addFirst(composite);
		PropertySourceBootstrapProperties remoteProperties = new PropertySourceBootstrapProperties();
		if (!remoteProperties.isAllowOverride() ||
            (!remoteProperties.isOverrideNone() &&
             remoteProperties.isOverrideSystemProperties())) {
			propertySources.addFirst(composite);
			return;
		}
		if (remoteProperties.isOverrideNone()) {
			propertySources.addLast(composite);
			return;
		}
		if (propertySources.contains(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)) {
			if (!remoteProperties.isOverrideSystemProperties()) {
				propertySources.addAfter(
						StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
						composite);
			}
			else {
				propertySources.addBefore(
						StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
						composite);
			}
		}
		else {
			propertySources.addLast(composite);
		}
	}


}
