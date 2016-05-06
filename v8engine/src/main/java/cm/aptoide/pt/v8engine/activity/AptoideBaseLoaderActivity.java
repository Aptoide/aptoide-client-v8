/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/05/2016.
 */

package cm.aptoide.pt.v8engine.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import cm.aptoide.pt.v8engine.layouthandler.LoadInterface;
import cm.aptoide.pt.v8engine.layouthandler.LoaderLayoutHandler;

/**
 * Created by neuro on 04-05-2016.
 */
public abstract class AptoideBaseLoaderActivity extends AptoideBaseActivity implements
		LoadInterface {

	private LoaderLayoutHandler loaderLayoutHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		loaderLayoutHandler = createLoaderLayoutHandler();
		super.onCreate(savedInstanceState);
		load();
	}

	@Override
	protected void bindViews() {
		loaderLayoutHandler.bindViews(getView());
	}

	@NonNull
	protected LoaderLayoutHandler createLoaderLayoutHandler() {
		return new LoaderLayoutHandler(getViewToShowAfterLoadingId(), this);
	}

	public void finishLoading() {
		loaderLayoutHandler.finishLoading();
	}

	protected void finishLoading(Throwable throwable) {
		loaderLayoutHandler.finishLoading(throwable);
	}

	@IdRes
	protected abstract int getViewToShowAfterLoadingId();

	public abstract void load();
}
