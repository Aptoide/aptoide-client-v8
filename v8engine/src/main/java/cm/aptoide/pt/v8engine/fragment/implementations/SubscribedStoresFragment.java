/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.MyStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablesFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CreateStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.MyStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreGridHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SubscribedStoreDisplayable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 11-05-2016.
 */
public class SubscribedStoresFragment extends GridRecyclerSwipeFragment {

  private static final String TAG = SubscribedStoresFragment.class.getName();
  private StoreAccessor storesAccessor;

  public static SubscribedStoresFragment newInstance() {
    return new SubscribedStoresFragment();
  }

  private Observable<List<Displayable>> loadStores() {
    return Observable.zip(loadFromNetwokStores(true), loadStoresFromDb(),
        (networkDisplayables, dbDisplayables) -> {
          dbDisplayables.addAll(networkDisplayables);
          int i;
          for (i = 0; i < dbDisplayables.size(); i++) {
            Displayable displayable = dbDisplayables.get(i);
            if (displayable instanceof CreateStoreDisplayable
                || displayable instanceof MyStoreDisplayable) {
              dbDisplayables.remove(displayable);
              dbDisplayables.add(0, displayable);
            }
          }
          return dbDisplayables;
        }).subscribeOn(Schedulers.computation());
  }

  @Override public void reload() {
    super.reload();
    loadStores().first().subscribe(displayables -> setDisplayables(displayables), err -> {
      Logger.e(TAG, err);
      CrashReports.logException(err);
    });
  }

  private Observable<List<Displayable>> loadStoresFromDb() {
    return storesAccessor.getAll().observeOn(AndroidSchedulers.mainThread()).map(stores -> {
      LinkedList<Displayable> displayables = new LinkedList<>();
      if (stores.size() > 0) {
        displayables.add(new StoreGridHeaderDisplayable(
            // TODO: 02/12/2016 trinkes extract
            new GetStoreWidgets.WSWidget().setTitle("Followed Stores")));
      }
      for (Store store : stores) {
        displayables.add(new SubscribedStoreDisplayable(store));
      }
      return displayables;
    });
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    storesAccessor = AccessorFactory.getAccessorFor(Store.class);
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    if (!refresh) {
      loadStores().first().subscribe(displayables -> setDisplayables(displayables), err -> {
        Logger.e(TAG, err);
        CrashReports.logException(err);
      });
    }
  }

  private Observable<List<Displayable>> loadFromNetwokStores(boolean refresh) {
    return MyStoreRequest.of().observe(refresh).observeOn(Schedulers.io()).map(myStore -> {

      List<Displayable> displayables = new LinkedList();
      List<GetStoreWidgets.WSWidget> list = myStore.getWidgets().getDatalist().getList();
      // Load sub nodes
      CountDownLatch countDownLatch = new CountDownLatch(list.size());

      Observable.from(list)
          .observeOn(Schedulers.io())
          .forEach(wsWidget -> WSWidgetsUtils.loadInnerNodes(wsWidget,
              wsWidget.getView() != null ? StoreUtils.getStoreCredentialsFromUrl(wsWidget.getView())
                  : new BaseRequestWithStore.StoreCredentials(), countDownLatch, refresh,
              throwable -> countDownLatch.countDown(), AptoideAccountManager.getAccessToken(),
              AptoideAccountManager.getUserEmail(),
              new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                  DataProvider.getContext()).getAptoideClientUUID(),
              DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(
                  V8Engine.getContext()), DataProvider.getConfiguration().getPartnerId(),
              !TextUtils.isEmpty(AptoideAccountManager.getUserData().getUserRepo())));

      try {
        countDownLatch.await(5, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      displayables = DisplayablesFactory.parse(myStore.getWidgets(),
          V8Engine.getConfiguration().getDefaultTheme());

      return displayables;
    }).onErrorReturn(throwable -> {
      CrashReports.logException(throwable);
      Logger.e(TAG, "loadFromNetwokStores: " + throwable);
      return Collections.emptyList();
    });
  }

  @Override public int getContentViewId() {
    return R.layout.my_stores_layout_fragment;
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
  }
}
