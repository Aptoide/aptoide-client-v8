/*
 * Copyright (c) 2016.
 * Modified on 02/08/2016.
 */

package cm.aptoide.pt.store.view;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.install.AptoideInstalledAppsRepository;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.store.RoomStoreRepository;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.displayable.DisplayablesFactory;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;

/**
 * Created by neuro on 29-04-2016.
 */
public abstract class StoreTabWidgetsGridRecyclerFragment extends StoreTabGridRecyclerFragment {

  protected AptoideAccountManager accountManager;
  protected StoreAnalytics storeAnalytics;
  protected NavigationTracker navigationTracker;
  @Inject StoreUtilsProxy storeUtilsProxy;
  @Inject AptoideInstalledAppsRepository aptoideInstalledAppsRepository;
  @Inject AnalyticsManager analyticsManager;
  @Inject @Named("marketName") String marketName;
  @Inject ThemeManager themeManager;
  @Inject StoreCredentialsProvider storeCredentialsProvider;
  @Inject RoomStoreRepository storeRepository;
  private StoreTabNavigator storeTabNavigator;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    navigationTracker =
        ((AptoideApplication) getContext().getApplicationContext()).getNavigationTracker();
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();

    storeAnalytics = new StoreAnalytics(analyticsManager, navigationTracker);
    storeTabNavigator = new StoreTabNavigator(getFragmentNavigator());
  }

  public Observable<List<Displayable>> parseDisplayables(GetStoreWidgets getStoreWidgets) {
    int currentNightMode = getContext().getResources()
        .getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

    boolean isDarkTheme = currentNightMode == Configuration.UI_MODE_NIGHT_YES;

    return Observable.from(getStoreWidgets.getDataList()
        .getList())
        .concatMapEager(wsWidget -> {
          AptoideApplication application =
              (AptoideApplication) getContext().getApplicationContext();
          return DisplayablesFactory.parse(marketName, wsWidget, storeTheme, storeRepository,
              storeCredentialsProvider, storeContext, getContext(), accountManager, storeUtilsProxy,
              (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE),
              getContext().getResources(), aptoideInstalledAppsRepository, storeAnalytics,
              storeTabNavigator, navigationTracker,
              new BadgeDialogFactory(getActivity(), themeManager),
              ((ActivityResultNavigator) getContext()).getFragmentNavigator(),
              application.getBodyInterceptorPoolV7(), application.getDefaultClient(),
              WebService.getDefaultConverter(), application.getTokenInvalidator(),
              application.getDefaultSharedPreferences(), themeManager);
        })
        .toList()
        .first();
  }
}
