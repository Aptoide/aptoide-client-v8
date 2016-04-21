/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 21/04/2016.
 */

package cm.aptoide.pt.v8engine;

import cm.aptoide.pt.dataprovider.DataProvider;

/**
 * Created by neuro on 14-04-2016.
 */
public class Aptoide extends DataProvider {

	@Override
	public void onCreate() {
		super.onCreate();

		context = this;
	}
}
