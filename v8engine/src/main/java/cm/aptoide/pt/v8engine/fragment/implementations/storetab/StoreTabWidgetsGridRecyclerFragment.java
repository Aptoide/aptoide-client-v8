/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/08/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations.storetab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablesFactory;
import java.util.List;
import rx.Observable;

/**
 * Created by neuro on 29-04-2016.
 */
public abstract class StoreTabWidgetsGridRecyclerFragment extends StoreTabGridRecyclerFragment {

  private AptoideClientUUID aptoideClientUUID;
  private AptoideAccountManager accountManager;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    aptoideClientUUID =
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(), getContext());
    accountManager =
        AptoideAccountManager.getInstance(getContext(), Application.getConfiguration());
  }

  protected Observable<List<Displayable>> loadGetStoreWidgets(GetStoreWidgets getStoreWidgets,
      boolean refresh, String url) {
    return Observable.from(getStoreWidgets.getDatalist().getList())
        .flatMap(wsWidget -> WSWidgetsUtils.loadWidgetNode(wsWidget,
            StoreUtils.getStoreCredentialsFromUrl(url), refresh, accountManager.getAccessToken(),
            aptoideClientUUID.getUniqueIdentifier(),
            DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(V8Engine.getContext()),
            DataProvider.getConfiguration().getPartnerId(), accountManager.isMatureSwitchOn()))
        .toList()
        .flatMapIterable(wsWidgets -> getStoreWidgets.getDatalist().getList())
        .concatMap(wsWidget -> DisplayablesFactory.parse(wsWidget, storeTheme, storeRepository,
            storeContext, getContext()))
        .toList()
        .first();
  }
}
