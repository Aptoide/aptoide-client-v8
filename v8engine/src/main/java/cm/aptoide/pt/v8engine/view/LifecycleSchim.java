/*
 * Copyright (c) 2016.
 * Modified on 23/08/2016.
 */

package cm.aptoide.pt.v8engine.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created
 * <p>
 * Used to mock common components lifecycle
 * </p>
 */
public interface LifecycleSchim {

  void onResume();

  void onPause();

  void onViewCreated();

  void onDestroyView();

  void onSaveInstanceState(Bundle outState);

  void onViewStateRestored(@Nullable Bundle savedInstanceState);
}
