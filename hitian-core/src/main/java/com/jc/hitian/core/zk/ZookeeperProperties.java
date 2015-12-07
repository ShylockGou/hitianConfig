package com.jc.hitian.core.zk;

import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author Spencer Gibb
 */
@Data
public class ZookeeperProperties {

	private String connectString = "127.0.0.1:2181";

	private boolean enabled = false;

	/**
	 * @param baseSleepTimeMs initial amount of time to wait between retries
	 */
	private Integer baseSleepTimeMs = 50;

	/**
	 * @param maxRetries max number of times to retry
	 */
	private Integer maxRetries = 50;

	/**
	 * @param maxSleepMs max time in ms to sleep on each retry
	 */
	private Integer maxSleepMs = 500;

	private Integer blockUntilConnectedWait = 10;

	private TimeUnit blockUntilConnectedUnit = TimeUnit.SECONDS;
}
