/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.os.Bundle;

/**
 * Created by marcelobenites on 8/22/16.
 */
public interface Presenter {

	public void present();

	void saveState(Bundle state);

	void restoreState(Bundle state);
}
