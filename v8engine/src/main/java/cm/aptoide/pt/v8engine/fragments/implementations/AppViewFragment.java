/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.fragments.implementations;

import android.os.Bundle;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.fragments.GridRecyclerFragment;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewFragment extends GridRecyclerFragment {

	private static final String TAG = AppViewFragment.class.getName();
	private String appId;

	public static AppViewFragment newInstance(String appId) {
		Bundle bundle = new Bundle();
		bundle.putString(BundleKeys.APP_ID.name(), appId);

		AppViewFragment fragment = new AppViewFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected void loadBundle(Bundle args) {
		super.loadBundle(args);
		appId = args.getString(BundleKeys.APP_ID.name());
	}

	@Override
	public void load() {
		loadAppInfo(appId).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::showAppInfo);
	}

	private Observable<Object> loadAppInfo(String appId) {
		return Observable.just(appId);
	}

	private void showAppInfo(Object obj) {
		Logger.d(TAG, "loaded app info");

		// TODO
	}

	private enum BundleKeys {
		APP_ID
	}
}
