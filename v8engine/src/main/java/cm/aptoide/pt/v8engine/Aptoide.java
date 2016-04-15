/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 15/04/2016.
 */

package cm.aptoide.pt.v8engine;

import android.app.Application;
import android.content.Context;

import lombok.Getter;

/**
 * Created by neuro on 14-04-2016.
 */
public class Aptoide extends Application {

	@Getter private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();

		context = this;
	}
}
