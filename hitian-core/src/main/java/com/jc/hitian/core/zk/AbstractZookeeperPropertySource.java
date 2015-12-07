package com.jc.hitian.core.zk;

import lombok.Getter;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.core.env.EnumerablePropertySource;

/**
 * @author Spencer Gibb
 */
public abstract class AbstractZookeeperPropertySource extends EnumerablePropertySource<CuratorFramework> {

	@Getter
	private String context;

	public AbstractZookeeperPropertySource(String context, CuratorFramework source) {
		super(context, source);

		this.context = context;

		if (!this.context.startsWith("/")) {
			this.context = "/" + this.context;
		}
	}

	protected String sanitizeKey(String path) {
		return path.replace(this.context + "/", "").replace('/', '.');
	}
}
