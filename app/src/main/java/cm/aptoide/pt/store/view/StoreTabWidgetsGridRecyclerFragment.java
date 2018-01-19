/*
 * Copyright (c) 2016.
 * Modified on 02/08/2016.
 */

package cm.aptoide.pt.store.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.displayable.DisplayablesFactory;
import java.util.List;
import javax.inject.Inject;
import okhttp3.OkHttpClient;
import rx.Observable;

/**
 * Created by neuro on 29-04-2016.
 */
public abstract class StoreTabWidgetsGridRecyclerFragment extends StoreTabGridRecyclerFragment {

  protected AptoideAccountManager accountManager;
  protected StoreUtilsProxy storeUtilsProxy;
  protected InstalledRepository installedRepository;
  protected StoreAnalytics storeAnalytics;
  private StoreTabNavigator storeTabNavigator;
  @Inject AnalyticsManager analyticsManager;
  protected NavigationTracker navigationTracker;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    navigationTracker = ((AptoideApplication) getContext().getApplicationContext()).getNavigationTracker();
    final StoreCredentialsProvider storeCredentialsProvider = new StoreCredentialsProviderImpl(
        AccessorFactory.getAccessorFor(((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class));
    final OkHttpClient httpClient =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    final BodyInterceptor<BaseBody> bodyInterceptor =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7();
    final TokenInvalidator tokenInvalidator =
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator();
    storeUtilsProxy = new StoreUtilsProxy(accountManager, bodyInterceptor, storeCredentialsProvider,
        AccessorFactory.getAccessorFor(((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class), httpClient,
        WebService.getDefaultConverter(), tokenInvalidator,
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences());
    installedRepository =
        RepositoryFactory.getInstalledRepository(getContext().getApplicationContext());
    storeAnalytics =
        new StoreAnalytics(analyticsManager, navigationTracker);
    storeTabNavigator = new StoreTabNavigator(getFragmentNavigator());
  }

  protected Observable<List<Displayable>> parseDisplayables(GetStoreWidgets getStoreWidgets) {
    return Observable.from(getStoreWidgets.getDataList()
        .getList())
        .concatMapEager(wsWidget -> {
          AptoideApplication application =
              (AptoideApplication) getContext().getApplicationContext();
          return DisplayablesFactory.parse(wsWidget, storeTheme, storeRepository, storeContext,
              getContext(), accountManager, storeUtilsProxy,
              (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE),
              getContext().getResources(), installedRepository, storeAnalytics, storeTabNavigator,
              navigationTracker, new BadgeDialogFactory(getContext()),
              ((ActivityResultNavigator) getContext()).getFragmentNavigator(),
              AccessorFactory.getAccessorFor(application.getDatabase(), Store.class),
              application.getBodyInterceptorPoolV7(), application.getDefaultClient(),
              WebService.getDefaultConverter(), application.getTokenInvalidator(),
              application.getDefaultSharedPreferences());
        })
        .toList()
        .first();
  }
}
