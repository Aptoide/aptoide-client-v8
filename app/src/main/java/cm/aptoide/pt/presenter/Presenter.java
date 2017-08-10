/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.presenter;

import android.os.Bundle;

/**
 * Created by marcelobenites on 8/22/16.
 */
public interface Presenter {

  void present();

  void saveState(Bundle state);

  void restoreState(Bundle state);
}
