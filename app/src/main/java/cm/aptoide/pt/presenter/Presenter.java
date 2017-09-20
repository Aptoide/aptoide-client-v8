/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.presenter;

import android.os.Bundle;

public interface Presenter {

  void present();

  /**
   * @deprecated View state should be managed in the View implementation. Presenter should not now
   * about Android specific details.
   */
  @Deprecated void saveState(Bundle state);

  /**
   * @deprecated View state should be managed in the View implementation. Presenter should not now
   * about Android specific details.
   */
  @Deprecated void restoreState(Bundle state);
}
