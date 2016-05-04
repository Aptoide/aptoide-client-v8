/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.fragments;

import android.support.annotation.IdRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ProgressBar;

import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.ThreadUtils;
import cm.aptoide.pt.v8engine.R;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 16-04-2016.
 */
public abstract class BaseLoaderFragment<T extends View> extends BaseFragment<T> {

	protected T baseView;
	protected ProgressBar progressBar;
	protected View genericErrorView;
	protected View noNetworkConnectionView;
	protected View retryView;
	protected SwipeRefreshLayout swipeContainer;

	@Override
	@SuppressWarnings("unchecked")
	protected void bindViews(View view) {
		baseView = (T) view.findViewById(getBaseViewId());
		swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		genericErrorView = view.findViewById(R.id.generic_error);
		noNetworkConnectionView = view.findViewById(R.id.no_network_connection);
		retryView = view.findViewById(R.id.retry);
	}

	@IdRes
	protected abstract int getBaseViewId();

	protected void finishLoading() {
		Observable.fromCallable(() -> {
			progressBar.setVisibility(View.GONE);
			baseView.setVisibility(View.VISIBLE);
			return null;
		}).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
	}

	protected void finishLoading(Throwable throwable) {
		Logger.printException(throwable);

		ThreadUtils.runOnUiThread(() -> {
			progressBar.setVisibility(View.GONE);
			baseView.setVisibility(View.GONE);
			swipeContainer.setRefreshing(false);
			swipeContainer.setEnabled(false);

			if (throwable instanceof NoNetworkConnectionException) {
				genericErrorView.setVisibility(View.GONE);
				noNetworkConnectionView.setVisibility(View.VISIBLE);
				retryView.setOnClickListener(view -> {
					restoreVisibility();
					load();
				});
			} else {
				noNetworkConnectionView.setVisibility(View.GONE);
				genericErrorView.setVisibility(View.VISIBLE);
				retryView.setOnClickListener(view -> {
					restoreVisibility();
					load();
				});
			}
		});
	}

	protected void restoreVisibility() {
		genericErrorView.setVisibility(View.GONE);
		noNetworkConnectionView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
	}

	protected abstract void load();
}
