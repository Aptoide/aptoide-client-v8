/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 21/04/2016.
 */

package cm.aptoide.pt.dataprovider;

import android.app.Application;
import android.content.Context;

import lombok.Getter;

/**
 * Created by neuro on 20-04-2016.
 */
public abstract class DataProvider extends Application {

	@Getter protected static Context context;
}
