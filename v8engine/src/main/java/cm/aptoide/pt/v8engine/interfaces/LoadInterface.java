/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.v8engine.interfaces;

import android.os.Bundle;

/**
 * Created by neuro on 04-05-2016.
 */
public interface LoadInterface {
  /**
   * @param created flags that the fragment is already created, ie. only it's view is being
   * restored.
   * @param refresh flags that the fragment should refresh it's state, reload data from network and
   * refresh fragment state.
   * @param savedInstanceState savedInstanceState bundle.
   */
  void load(boolean created, boolean refresh, Bundle savedInstanceState);
}
