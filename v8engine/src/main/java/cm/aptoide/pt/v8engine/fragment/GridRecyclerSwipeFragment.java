/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.ReloadInterface;
import cm.aptoide.pt.v8engine.layouthandler.LoaderLayoutHandler;
import cm.aptoide.pt.v8engine.layouthandler.SwipeLoaderLayoutHandler;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;

/**
 * Created by neuro on 05-05-2016.
 */
public abstract class GridRecyclerSwipeFragment<T extends BaseAdapter>
    extends GridRecyclerFragmentWithDecorator<T> implements ReloadInterface {

  protected String storeTheme;
  private BroadcastReceiver receiver;

  @Partners @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    if (storeTheme == null) {
      //storeTheme = V8Engine.getConfiguration().getDefaultTheme();
    }
    /*ThemeUtils.setStoreTheme(getActivity(), storeTheme);
    ThemeUtils.setStatusBarThemeColor(getActivity(), StoreThemeEnum.get(storeTheme));*/
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @NonNull @Override protected LoaderLayoutHandler createLoaderLayoutHandler() {
    return new SwipeLoaderLayoutHandler(getViewToShowAfterLoadingId(), this);
  }

  @Override public void reload() {
    load(false, true, null);
  }

  @Override public int getContentViewId() {
    return R.layout.recycler_swipe_fragment;
  }

  @Override public void onDestroyView() {
    unregisterReceiverForAccountManager();
    super.onDestroyView();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    registerReceiverForAccountManager();
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
      try {
        getContext().unregisterReceiver(receiver);
      } catch (IllegalArgumentException ex) {
        CrashReport.getInstance().log(ex);
      }
    }

  protected static class BundleCons {

    public static final String STORE_THEME = "storeTheme";
  }
}
