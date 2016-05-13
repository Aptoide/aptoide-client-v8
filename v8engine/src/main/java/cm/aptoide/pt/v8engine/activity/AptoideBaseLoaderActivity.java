/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.v8engine.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;

import cm.aptoide.pt.v8engine.interfaces.LoadInterface;
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
		load(true);
	}

	@Override
	public void bindViews(View view) {
		loaderLayoutHandler.bindViews(view);
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

	public abstract void load(boolean refresh);
}
