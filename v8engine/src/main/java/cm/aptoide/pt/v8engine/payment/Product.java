/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.os.Parcelable;

/**
 * Created by marcelobenites on 8/16/16.
 */
public interface Product {

	int getId();

	String getIcon();

	String getTitle();

	String getDescription();

	String getPriceDescription();
}