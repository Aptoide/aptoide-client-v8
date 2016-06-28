/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/06/2016.
 */

package cm.aptoide.pt;

import android.content.Context;

import cm.aptoide.pt.preferences.AptoideConfiguration;
import cm.aptoide.pt.v8engine.V8Engine;

/**
 * Created by neuro on 10-05-2016.
 */
public class Aptoide extends V8Engine {

	@Override
	protected void attachBaseContext(Context base) {
		//MultiDex.install(this);
		super.attachBaseContext(base);
	}

	@Override
	protected AptoideConfiguration createConfiguration() {
		return new VanillaConfiguration();
	}
}
