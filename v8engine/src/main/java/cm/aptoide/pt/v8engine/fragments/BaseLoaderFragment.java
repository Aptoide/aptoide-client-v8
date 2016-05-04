/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 05/05/2016.
 */

package cm.aptoide.pt.v8engine.fragments;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import cm.aptoide.pt.v8engine.layouthandler.LoadInterface;
import cm.aptoide.pt.v8engine.layouthandler.LoaderLayoutHandler;

/**
 * Created by neuro on 16-04-2016.
 */
public abstract class BaseLoaderFragment extends BaseFragment implements LoadInterface {

	private LoaderLayoutHandler loaderLayoutHandler;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loaderLayoutHandler = createLoaderLayoutHandler();
	}

	@NonNull
	protected LoaderLayoutHandler createLoaderLayoutHandler() {
		return new LoaderLayoutHandler(getBaseViewId(), this);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void bindViews(View view) {
		loaderLayoutHandler.bindViews(view);
	}

	@IdRes
	protected abstract int getBaseViewId();

	protected void finishLoading() {
		loaderLayoutHandler.finishLoading();
	}

	protected void finishLoading(Throwable throwable) {
		loaderLayoutHandler.finishLoading(throwable);
	}

	public abstract void load();
}
