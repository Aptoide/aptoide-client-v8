/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/05/2016.
 */

package cm.aptoide.pt.v8engine;

import android.support.v4.app.Fragment;

import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.v8engine.activities.AptoideSimpleFragmentActivity;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreFragment;

/**
 * Created by neuro on 06-05-2016.
 */
public class MainActivityFragment extends AptoideSimpleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return StoreFragment.newInstance("apps", StoreContext.home);
	}
}
