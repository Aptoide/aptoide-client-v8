/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 10/05/2016.
 */

package cm.aptoide.pt.preferences;

import android.content.Context;

import lombok.Getter;

/**
 * Created by neuro on 22-04-2016.
 */
public abstract class Application extends android.app.Application {

	@Getter protected static Context context;
	@Getter protected static AptoideConfiguration configuration;

	@Override
	public void onCreate() {
		super.onCreate();

		context = this;
		configuration = createConfiguration();
	}

	protected abstract AptoideConfiguration createConfiguration();
}
