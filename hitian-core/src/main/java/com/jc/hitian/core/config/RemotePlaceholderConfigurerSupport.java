package com.jc.hitian.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringValueResolver;

/**
 * <p/>
 * Created by Shylock on 15/12/7.
 */
@Slf4j
public class RemotePlaceholderConfigurerSupport extends PropertiesLoaderSupport implements BeanNameAware, BeanFactoryAware, BeanFactoryPostProcessor, PriorityOrdered {

    /**
     * Default placeholder prefix: {@value}
     */
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

    /**
     * Default placeholder suffix: {@value}
     */
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

    /**
     * Default value separator: {@value}
     */
    public static final String DEFAULT_VALUE_SEPARATOR = ":";

    /**
     * Defaults to {@value #DEFAULT_PLACEHOLDER_PREFIX}
     */
    protected String placeholderPrefix = DEFAULT_PLACEHOLDER_PREFIX;

    /**
     * Defaults to {@value #DEFAULT_PLACEHOLDER_SUFFIX}
     */
    protected String placeholderSuffix = DEFAULT_PLACEHOLDER_SUFFIX;

    /**
     * Defaults to {@value #DEFAULT_VALUE_SEPARATOR}
     */
    protected String valueSeparator = DEFAULT_VALUE_SEPARATOR;

    protected boolean ignoreUnresolvablePlaceholders = false;

    protected String nullValue;

    private int order = Ordered.LOWEST_PRECEDENCE;  // default: same as non-Ordered

    private BeanFactory beanFactory;

    private String beanName;

    Environment env;

    public RemotePlaceholderConfigurerSupport () {
        log.info ( "init RemotePlaceholderConfigurerSupport" );
    }

    public RemotePlaceholderConfigurerSupport ( Environment environment ) {
        this();
        this.env = environment;
    }

    public int getOrder () {

        return this.order;
    }

    /**
     * Set the order value of this object for sorting purposes.
     *
     * @see PriorityOrdered
     */
    public void setOrder ( int order ) {

        this.order = order;
    }

    /**
     * @throws BeanInitializationException if any properties cannot be loaded
     */
    public void postProcessBeanFactory ( ConfigurableListableBeanFactory beanFactory ) throws BeansException {

        // Convert the merged properties, if necessary.
        // Let the subclass process the properties.
        processProperties ( beanFactory );
    }


    /**
     * Callback that supplies the owning factory to a bean instance.
     * <p>Invoked after the population of normal bean properties
     * but before an initialization callback such as
     * {@link InitializingBean#afterPropertiesSet()} or a custom init-method.
     *
     * @param beanFactory owning BeanFactory (never {@code null}).
     *                    The bean can immediately call methods on the factory.
     * @throws BeansException in case of initialization errors
     * @see BeanInitializationException
     */
    @Override
    public void setBeanFactory ( BeanFactory beanFactory ) throws BeansException {

        this.beanFactory = beanFactory;
    }

    /**
     * Set the name of the bean in the bean factory that created this bean.
     * <p>Invoked after population of normal bean properties but before an
     * init callback such as {@link InitializingBean#afterPropertiesSet()}
     * or a custom init-method.
     *
     * @param name the name of the bean in the factory.
     *             Note that this name is the actual bean name used in the factory, which may
     *             differ from the originally specified name: in particular for inner bean
     *             names, the actual bean name might have been made unique through appending
     *             "#..." suffixes. Use the {@link BeanFactoryUtils#originalBeanName(String)}
     *             method to extract the original bean name (without suffix), if desired.
     */
    @Override
    public void setBeanName ( String name ) {

        this.beanName = name;
    }

    /**
     * Apply the given Properties to the given BeanFactory.
     *
     * @param beanFactory the BeanFactory used by the application context
     * @param props       the Properties to apply
     * @throws BeansException in case of errors
     */
    /**
     * Visit each bean definition in the given bean factory and attempt to replace ${...} property
     * placeholders with values from the given properties.
     */
    protected void processProperties ( ConfigurableListableBeanFactory beanFactoryToProcess)
            throws BeansException {

        StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver ( );
        doProcessProperties ( beanFactoryToProcess, valueResolver );
    }

    protected void doProcessProperties ( ConfigurableListableBeanFactory beanFactoryToProcess,
                                         StringValueResolver valueResolver ) {

        BeanDefinitionVisitor visitor = new BeanDefinitionVisitor ( valueResolver );

        String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames ();
        for ( String curName : beanNames ) {
            // Check that we're not parsing our own bean definition,
            // to avoid failing on unresolvable placeholders in properties file locations.
            if ( !( curName.equals ( this.beanName ) && beanFactoryToProcess.equals ( this.beanFactory ) ) ) {
                BeanDefinition bd = beanFactoryToProcess.getBeanDefinition ( curName );
                try {
                    visitor.visitBeanDefinition ( bd );
                } catch ( Exception ex ) {
                    throw new BeanDefinitionStoreException ( bd.getResourceDescription (), curName, ex.getMessage (), ex );
                }
            }
        }

        // New in Spring 2.5: resolve placeholders in alias target names and aliases as well.
        beanFactoryToProcess.resolveAliases ( valueResolver );

        // New in Spring 3.0: resolve placeholders in embedded values such as annotation attributes.
        beanFactoryToProcess.addEmbeddedValueResolver ( valueResolver );
    }
    private String resolvePlaceholder ( String placeholderName ) {

       return env.getProperty ( placeholderName );
    }



    private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

        private final PropertyPlaceholderHelper helper;

        private final PropertyPlaceholderHelper.PlaceholderResolver resolver;

        public PlaceholderResolvingStringValueResolver ( ) {

            this.helper = new PropertyPlaceholderHelper (
                    placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders );
            this.resolver = new PropertyPlaceholderConfigurerResolver ();
        }

        @Override
        public String resolveStringValue ( String strVal ) throws BeansException {

            String value = this.helper.replacePlaceholders ( strVal, this.resolver );
            return ( value.equals ( nullValue ) ? null : value );
        }
    }


    private class PropertyPlaceholderConfigurerResolver implements PropertyPlaceholderHelper.PlaceholderResolver {



        @Override
        public String resolvePlaceholder ( String placeholderName ) {

            return RemotePlaceholderConfigurerSupport.this.resolvePlaceholder(placeholderName);
        }
    }

}
