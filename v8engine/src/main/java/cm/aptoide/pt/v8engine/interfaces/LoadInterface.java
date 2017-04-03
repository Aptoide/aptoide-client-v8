/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.v8engine.interfaces;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.view.fragment.BaseLoaderFragment;

/**
 * Created by neuro on 04-05-2016.
 */
public interface LoadInterface {
  /**
   * @param create flags that the fragment is being created for the first time. Will be set to
   * false
   * on {@link BaseLoaderFragment#onStop()}.
   * @param refresh flags that the fragment should refresh it's state, reload data from network and
   * refresh its state.
   * @param savedInstanceState savedInstanceState bundle.
   */
  void load(boolean create, boolean refresh, Bundle savedInstanceState);
}
