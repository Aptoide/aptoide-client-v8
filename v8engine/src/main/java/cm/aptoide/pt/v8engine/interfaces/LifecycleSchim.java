package cm.aptoide.pt.v8engine.interfaces;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * created by SithEngineer
 * <p>
 * mock to activity / fragment components lifecycle
 */
public interface LifecycleSchim {

	void onResume();

	void onPause();

	void onSaveInstanceState(Bundle outState);

	void onViewStateRestored(@Nullable Bundle savedInstanceState);
}
