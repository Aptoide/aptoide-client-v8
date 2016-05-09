/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 09/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cm.aptoide.pt.v8engine.layouthandler.LoadInterface;
import cm.aptoide.pt.v8engine.layouthandler.LoaderLayoutHandler;
import lombok.Getter;

/**
 * Created by neuro on 16-04-2016.
 */
public abstract class BaseLoaderFragment extends BaseFragment implements LoadInterface {

	private LoaderLayoutHandler loaderLayoutHandler;
	@Getter private boolean created = false;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
	Bundle savedInstanceState) {
		if (!created) {
			loaderLayoutHandler = createLoaderLayoutHandler();
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		load(!created);
	}

	@Override
	protected void bindViews(View view) {
		if (loaderLayoutHandler != null) loaderLayoutHandler.bindViews(view);
		if (created) {
			finishLoading();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		created = true;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (loaderLayoutHandler != null) {
			loaderLayoutHandler = null;
		}
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

	public abstract void load(boolean refresh);
}
