/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 16/04/2016.
 */

package cm.aptoide.pt.v8engine.fragments;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

import cm.aptoide.pt.v8engine.R;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 16-04-2016.
 */
public abstract class BaseLoaderFragment<T extends View> extends BaseFragment<T> {

	protected T baseView;
	protected ProgressBar progressBar;

	@SuppressWarnings("unchecked")
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		baseView = (T) view.findViewById(getBaseViewId());
		progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
	}

	@IdRes
	protected abstract int getBaseViewId();

	public void finishLoading() {
		Observable.fromCallable(() -> {
			progressBar.setVisibility(View.GONE);
			baseView.setVisibility(View.VISIBLE);
			return null;
		}).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
	}
}
