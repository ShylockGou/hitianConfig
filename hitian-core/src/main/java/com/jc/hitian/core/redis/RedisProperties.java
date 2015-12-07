package com.jc.hitian.core.redis;

import lombok.Data;

/**
 * @author Spencer Gibb
 */
@Data
public class RedisProperties {

	private String host = "192.168.99.100";

	private int port = 6379;

	private int database = 1;

	private boolean usePool = true;
}
