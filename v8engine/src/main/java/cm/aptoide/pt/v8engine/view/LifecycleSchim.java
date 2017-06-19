package cm.aptoide.pt.v8engine.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Used to mock common components lifecycle
 */
public interface LifecycleSchim {

  void onResume();

  void onPause();

  void onViewCreated();

  void onDestroyView();

  void onSaveInstanceState(Bundle outState);

  void onViewStateRestored(@Nullable Bundle savedInstanceState);
}
