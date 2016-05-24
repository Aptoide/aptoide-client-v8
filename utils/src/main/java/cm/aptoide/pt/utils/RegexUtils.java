/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.utils;

import java.util.regex.Pattern;

/**
 * Created by neuro on 23-05-2016.
 */
public class RegexUtils {

	private static final String STORE_ID_FROM_GET_URL = "store_id\\/(\\d+)\\/";
	private static final String STORE_NAME_FROM_GET_URL = "store_name\\/(.*?)\\/";

	private static Pattern STORE_ID_FROM_GET_URL_PATTERN;
	private static Pattern STORE_NAME_FROM_GET_URL_PATTERN;

	public static Pattern getStoreIdFromGetUrlPattern() {
		if (STORE_ID_FROM_GET_URL_PATTERN == null) {
			STORE_ID_FROM_GET_URL_PATTERN = Pattern.compile(STORE_ID_FROM_GET_URL);
		}

		return STORE_ID_FROM_GET_URL_PATTERN;
	}

	public static Pattern getStoreNameFromGetUrlPattern() {
		if (STORE_NAME_FROM_GET_URL_PATTERN == null) {
			STORE_NAME_FROM_GET_URL_PATTERN = Pattern.compile(STORE_NAME_FROM_GET_URL);
		}

		return STORE_NAME_FROM_GET_URL_PATTERN;
	}
}
