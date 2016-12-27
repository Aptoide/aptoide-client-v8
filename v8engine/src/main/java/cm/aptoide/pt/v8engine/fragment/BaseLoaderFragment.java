/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.v8engine.interfaces.LoadInterface;
import cm.aptoide.pt.v8engine.layouthandler.LoaderLayoutHandler;
import lombok.Getter;

/**
 * Created by neuro on 16-04-2016.
 */
public abstract class BaseLoaderFragment extends SupportV4BaseFragment implements LoadInterface {

  private LoaderLayoutHandler loaderLayoutHandler;
  // Just a convenient reuse option.
  protected ErrorRequestListener errorRequestListener = e -> finishLoading(e);
  @Getter private boolean create = true;
  private BroadcastReceiver receiver;

  @CallSuper @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    load(create, false, savedInstanceState);

    registerReceiverForAccountManager();
  }

  @CallSuper @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    loaderLayoutHandler = createLoaderLayoutHandler();
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  protected void registerReceiverForAccountManager() {
    receiver = new BroadcastReceiver() {
      @Override public void onReceive(Context context, Intent intent) {
        load(false, true, null);
      }
    };
    IntentFilter intentFilter = new IntentFilter(AptoideAccountManager.LOGIN);
    intentFilter.addAction(AptoideAccountManager.LOGOUT);
    getContext().registerReceiver(receiver, intentFilter);
  }

  private void unregisterReceiverForAccountManager() {
    getContext().unregisterReceiver(receiver);
  }

  @CallSuper @Override public void bindViews(View view) {
    if (loaderLayoutHandler != null) {
      loaderLayoutHandler.bindViews(view);
    }
    if (!create) {
      finishLoading();
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
    unregisterReceiverForAccountManager();
  }

  @NonNull protected LoaderLayoutHandler createLoaderLayoutHandler() {
    return new LoaderLayoutHandler(getViewToShowAfterLoadingId(), this);
  }

  @IdRes protected abstract int getViewToShowAfterLoadingId();

  @CallSuper protected void finishLoading() {
    if (loaderLayoutHandler != null) {
      loaderLayoutHandler.finishLoading();
    }
  }

  @CallSuper protected void finishLoading(Throwable throwable) {
    if (loaderLayoutHandler != null) {
      loaderLayoutHandler.finishLoading(throwable);
    }
  }

  public abstract void load(boolean create, boolean refresh, Bundle savedInstanceState);
}
