/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import java.util.regex.Matcher;

import cm.aptoide.pt.utils.RegexUtils;

/**
 * Created by neuro on 23-05-2016.
 */
public class V7Url {

	private String url;

	public V7Url(String url) {
		this.url = url;
	}

	public String get() {
		return url;
	}

	public String getStoreName() {
		Matcher matcher = RegexUtils.getStoreNameFromGetUrlPattern().matcher(url);
		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	public Long getStoreId() {
		Matcher matcher = RegexUtils.getStoreIdFromGetUrlPattern().matcher(url);
		if (matcher.find()) {
			return Long.parseLong(matcher.group(1));
		}

		return null;
	}

	public V7Url remove(String toRemove) {
		this.url = url.replace(toRemove, "");
		return this;
	}
}
