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
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablesFactory;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import rx.Observable;

/**
 * Created by neuro on 29-04-2016.
 */
public abstract class StoreTabWidgetsGridRecyclerFragment extends StoreTabGridRecyclerFragment {

  protected List<Displayable> loadGetStoreWidgets(GetStoreWidgets getStoreWidgets, boolean refresh,
      String url) {
    // Load sub nodes
    List<GetStoreWidgets.WSWidget> list = getStoreWidgets.getDatalist().getList();
    CountDownLatch countDownLatch = new CountDownLatch(list.size());

    Observable.from(list)
        .forEach(wsWidget -> WSWidgetsUtils.loadInnerNodes(wsWidget,
            StoreUtils.getStoreCredentialsFromUrl(url), countDownLatch, refresh,
            throwable -> countDownLatch.countDown(), AptoideAccountManager.getAccessToken(),
            new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                DataProvider.getContext()).getAptoideClientUUID(),
            DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(V8Engine.getContext()),
            DataProvider.getConfiguration().getPartnerId(),
            AptoideAccountManager.isMatureSwitchOn()));

    try {
      countDownLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return DisplayablesFactory.parse(getStoreWidgets, storeTheme, storeRepository);
  }
}
