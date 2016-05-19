/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 19/05/2016.
 */

package cm.aptoide.pt.utils;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by sithengineer on 19/05/16.
 */
public final class IdUtils {

	private static final SecureRandom random = new SecureRandom();
	private static final AtomicLong sequentialLongId = new AtomicLong();
	private final AtomicLong longId;

	public IdUtils(long initialValue) {
		longId = new AtomicLong(initialValue);
	}

	/**
	 * @return unique identifier as a String from method {@link UUID#randomUUID()}
	 */
	public static String randomString() {
		return UUID.randomUUID().toString();
	}

	public static long randomLong() {
		return random.nextLong();
	}

	public static long nextSequentialLong() {
		return sequentialLongId.incrementAndGet();
	}

	public long nextLong() {
		return longId.incrementAndGet();
	}
}
