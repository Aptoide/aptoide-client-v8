/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 23/08/2016.
 */

package cm.aptoide.pt.v8engine.interfaces;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * created by SithEngineer
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
