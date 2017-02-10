/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/08/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations.storetab;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
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

  private final AptoideClientUUID aptoideClientUUID;

  public StoreTabWidgetsGridRecyclerFragment() {
    aptoideClientUUID = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext());
  }

  protected Observable<List<Displayable>> loadGetStoreWidgets(GetStoreWidgets getStoreWidgets,
      boolean refresh, String url) {
    return Observable.from(getStoreWidgets.getDatalist().getList())
        .flatMap(wsWidget -> WSWidgetsUtils.loadWidgetNode(wsWidget,
            StoreUtils.getStoreCredentialsFromUrl(url), refresh,
            AptoideAccountManager.getAccessToken(), aptoideClientUUID.getAptoideClientUUID(),
            DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(V8Engine.getContext()),
            DataProvider.getConfiguration().getPartnerId(),
            AptoideAccountManager.isMatureSwitchOn()))
        .toList()
        .map(wsWidgets -> DisplayablesFactory.parse(getStoreWidgets, storeTheme, storeRepository,
            storeContext))
        .first();
  }
}
