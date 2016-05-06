/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 05/05/2016.
 */

package cm.aptoide.pt.v8engine.layouthandler;

import android.support.annotation.IdRes;
import android.view.View;
import android.widget.ProgressBar;

import java.net.SocketTimeoutException;

import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.ThreadUtils;
import cm.aptoide.pt.v8engine.R;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Handler for Loader Layouts. Needs four identified views in the corresponding layout:<br>
 * <br>&#9{@link R.id#progress_bar} <br>&#9{@link R.id#generic_error} <br>&#9{@link
 * R.id#no_network_connection} <br>&#9{@link R.id#retry}
 */
public class LoaderLayoutHandler {

	protected final LoadInterface loadInterface;
	@IdRes private final int viewToShowAfterLoadingId;

	protected View viewToShowAfterLoading;
	protected ProgressBar progressBar;
	protected View genericErrorView;
	protected View noNetworkConnectionView;
	protected View retryErrorView;
	protected View retryNoNetworkView;

	public LoaderLayoutHandler(int viewToShowAfterLoadingId, LoadInterface loadInterface) {
		this.viewToShowAfterLoadingId = viewToShowAfterLoadingId;
		this.loadInterface = loadInterface;
	}

	@SuppressWarnings("unchecked")
	public void bindViews(View view) {
		viewToShowAfterLoading = view.findViewById(viewToShowAfterLoadingId);
		progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		genericErrorView = view.findViewById(R.id.generic_error);
		noNetworkConnectionView = view.findViewById(R.id.no_network_connection);
//		retryView = view.findViewById(R.id.retry);
		retryErrorView = genericErrorView.findViewById(R.id.retry);
		retryNoNetworkView = noNetworkConnectionView.findViewById(R.id.retry);
	}

	public void finishLoading(Throwable throwable) {
		Logger.printException(throwable);

		ThreadUtils.runOnUiThread(() -> onFinishLoading(throwable));
	}

	protected void onFinishLoading(Throwable throwable) {
		progressBar.setVisibility(View.GONE);
		viewToShowAfterLoading.setVisibility(View.GONE);

		if (throwable instanceof NoNetworkConnectionException || (throwable.getCause() != null &&
				throwable
				.getCause() instanceof SocketTimeoutException)) {
			genericErrorView.setVisibility(View.GONE);
			noNetworkConnectionView.setVisibility(View.VISIBLE);
			retryNoNetworkView.setOnClickListener(view -> {
				restoreState();
				loadInterface.load();
			});
		} else {
			noNetworkConnectionView.setVisibility(View.GONE);
			genericErrorView.setVisibility(View.VISIBLE);
			retryErrorView.setOnClickListener(view -> {
				restoreState();
				loadInterface.load();
			});
		}
	}

	public void finishLoading() {
		Observable.fromCallable(() -> {
			onFinishLoading();
			return null;
		}).subscribeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
		}, Logger::printException);
	}

	protected void onFinishLoading() {
		progressBar.setVisibility(View.GONE);
		viewToShowAfterLoading.setVisibility(View.VISIBLE);
	}

	protected void restoreState() {
		genericErrorView.setVisibility(View.GONE);
		noNetworkConnectionView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
	}
}
