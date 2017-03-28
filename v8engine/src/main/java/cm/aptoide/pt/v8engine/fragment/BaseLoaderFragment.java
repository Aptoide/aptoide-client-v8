/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.interfaces.LoadInterface;
import cm.aptoide.pt.v8engine.layouthandler.LoaderLayoutHandler;
import lombok.Getter;

/**
 * Created by neuro on 16-04-2016.
 */
public abstract class BaseLoaderFragment extends UIComponentFragment implements LoadInterface {

  private LoaderLayoutHandler loaderLayoutHandler;
  @Getter private boolean create = true;

  @CallSuper @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    load(create, false, savedInstanceState);
  }

  @Partners @CallSuper @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    loaderLayoutHandler = createLoaderLayoutHandler();
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @NonNull protected LoaderLayoutHandler createLoaderLayoutHandler() {
    if (getViewsToShowAfterLoadingId().length > 0) {
      return new LoaderLayoutHandler(this, getViewsToShowAfterLoadingId());
    }
    return new LoaderLayoutHandler(this, getViewToShowAfterLoadingId());
  }

  /**
   * Use this method to allow {@link LoaderLayoutHandler} to know what views it needs to hide (swap)
   * to show the Progress spinner during an operation (call network).
   * EITHER USE THIS METHOD OR {@link #getViewToShowAfterLoadingId}, if you dont want to use this
   * method, override it with new int[]{}
   */
  @IdRes protected abstract int[] getViewsToShowAfterLoadingId();

  /**
   * Use this method to allow {@link LoaderLayoutHandler} to know what view it needs to hide (swap)
   * to show the Progress spinner during an operation (call network).
   * EITHER USE THIS METHOD OR {@link #getViewsToShowAfterLoadingId}, if you dont want to use this
   * method, override it with -1.
   */
  @IdRes protected abstract int getViewToShowAfterLoadingId();

  /**
   * Called in {@link BaseLoaderFragment}.{@link BaseLoaderFragment#onViewCreated(View, Bundle)}
   *
   * @param create flags that the fragment is being created for the first time. Will be set to
   * false
   * on {@link BaseLoaderFragment#onStop()}.
   * @param refresh flags that the fragment should refresh it's state, reload data from network and
   * refresh its state.
   * @param savedInstanceState savedInstanceState bundle.
   */
  public abstract void load(boolean create, boolean refresh, Bundle savedInstanceState);

  @CallSuper @Override public void bindViews(View view) {
    if (loaderLayoutHandler != null) {
      loaderLayoutHandler.bindViews(view);
    }
    if (!create) {
      finishLoading();
    }
  }

  @Partners @CallSuper protected void finishLoading() {
    if (loaderLayoutHandler != null) {
      loaderLayoutHandler.finishLoading();
    }
  }

  @CallSuper @Override public void onStop() {
    super.onStop();
    create = false;
  }

  @CallSuper @Override public void onDestroyView() {
    super.onDestroyView();
    if (loaderLayoutHandler != null) {
      loaderLayoutHandler.unbindViews();
      loaderLayoutHandler = null;
    }
  }

  @Partners @CallSuper protected void finishLoading(Throwable throwable) {
    if (loaderLayoutHandler != null) {
      loaderLayoutHandler.finishLoading(throwable);
    }
    CrashReport.getInstance().log(throwable);
  }
}
