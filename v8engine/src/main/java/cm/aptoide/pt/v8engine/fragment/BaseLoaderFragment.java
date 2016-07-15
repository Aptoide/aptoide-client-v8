/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 08/06/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.v8engine.interfaces.LoadInterface;
import cm.aptoide.pt.v8engine.layouthandler.LoaderLayoutHandler;
import lombok.Getter;

/**
 * Created by neuro on 16-04-2016.
 */
public abstract class BaseLoaderFragment extends BaseFragment implements LoadInterface {

	private LoaderLayoutHandler loaderLayoutHandler;
	// Just a convenient reuse option.
	protected ErrorRequestListener errorRequestListener = e -> finishLoading(e);
	@Getter private boolean created = false;

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		load(!created, savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (loaderLayoutHandler != null) {
			loaderLayoutHandler = null;
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
	Bundle savedInstanceState) {
		loaderLayoutHandler = createLoaderLayoutHandler();
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void bindViews(View view) {
		if (loaderLayoutHandler != null) {
			loaderLayoutHandler.bindViews(view);
		}
		if (created) {
			finishLoading();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		created = true;
	}

	@NonNull
	protected LoaderLayoutHandler createLoaderLayoutHandler() {
		return new LoaderLayoutHandler(getViewToShowAfterLoadingId(), this);
	}

	@IdRes
	protected abstract int getViewToShowAfterLoadingId();

	protected void finishLoading() {
		if (loaderLayoutHandler != null) {
			loaderLayoutHandler.finishLoading();
		}
	}

	protected void finishLoading(Throwable throwable) {
		if (loaderLayoutHandler != null) {
			loaderLayoutHandler.finishLoading(throwable);
		}
	}

	public abstract void load(boolean refresh, Bundle savedInstanceState);
}
