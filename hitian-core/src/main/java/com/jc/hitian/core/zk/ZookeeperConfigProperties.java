package com.jc.hitian.core.zk;

import lombok.Data;

/**
 * @author Spencer Gibb
 */
@Data
public class ZookeeperConfigProperties {
	private boolean enabled = true;

	private String root = "config";

	private String defaultContext = "global";

	private String profileSeparator = ",";

	private boolean cacheEnabled = true;
}
