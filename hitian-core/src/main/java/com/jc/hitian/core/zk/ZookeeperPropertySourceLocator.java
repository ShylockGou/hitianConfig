package com.jc.hitian.core.zk;

import com.jc.hitian.core.config.PropertySourceLocator;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Spencer Gibb
 */
public class ZookeeperPropertySourceLocator implements PropertySourceLocator {

	private ZookeeperConfigProperties properties;

	private CuratorFramework curator;

	private ConcurrentHashMap<String, ZookeeperTreeCachePropertySource> lifecycleSources = new ConcurrentHashMap<>();

	public ZookeeperPropertySourceLocator(CuratorFramework curator, ZookeeperConfigProperties properties) {
		this.curator = curator;
		this.properties = properties;
	}

	@Override
	public PropertySource<?> locate(Environment environment) {
		if (properties.isEnabled () && environment instanceof ConfigurableEnvironment) {
			ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
			String appName = env.getProperty("hitian.application.name");
			List<String> profiles = Arrays.asList(env.getActiveProfiles());

			String root = properties.getRoot();
			List<String> contexts = new ArrayList<>();


			String defaultContext = root + "/" + properties.getDefaultContext();
			contexts.add(defaultContext);
			addProfiles(contexts, defaultContext, profiles);

			if ( appName != null ) {
				String baseContext = root + "/" + appName;
				contexts.add(baseContext);
				addProfiles(contexts, baseContext, profiles);

			}

			CompositePropertySource composite = new CompositePropertySource("zookeeper");

			Collections.reverse(contexts);

			for (String propertySourceContext : contexts) {
				PropertySource propertySource = create(propertySourceContext);
				composite.addPropertySource(propertySource);
				// TODO: howto call close when /refresh
			}

			return composite;
		}
		return null;
	}

	@PreDestroy
	public void destroy() {
		for (ZookeeperTreeCachePropertySource source : this.lifecycleSources.values()) {
			source.stop();
		}
	}

	private PropertySource<CuratorFramework> create(String context) {
		if (this.properties.isCacheEnabled()) {
			ZookeeperTreeCachePropertySource propertySource = new ZookeeperTreeCachePropertySource(context, curator);
			propertySource.start();
			lifecycleSources.put(propertySource.getName(), propertySource);
			return propertySource;
		}
		return new ZookeeperPropertySource(context, curator);
	}

	private void addProfiles(List<String> contexts, String baseContext,
			List<String> profiles) {
		for (String profile : profiles) {
			contexts.add(baseContext + properties.getProfileSeparator() + profile);
		}
	}
}
