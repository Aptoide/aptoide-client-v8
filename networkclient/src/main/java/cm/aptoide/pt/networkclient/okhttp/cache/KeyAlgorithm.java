/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/04/2016.
 */

package cm.aptoide.pt.networkclient.okhttp.cache;

import okhttp3.Request;

/**
 * Created by sithengineer on 27/04/16.
 */
public interface KeyAlgorithm {
	String getKeyFrom(Request request);
}
