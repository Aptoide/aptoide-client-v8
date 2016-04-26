/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.preferences;

import android.content.Context;

import lombok.Getter;

/**
 * Created by neuro on 22-04-2016.
 */
public class Application extends android.app.Application {

	@Getter protected static Context context;

	@Override
	public void onCreate() {
		super.onCreate();

		context = this;
	}
}
