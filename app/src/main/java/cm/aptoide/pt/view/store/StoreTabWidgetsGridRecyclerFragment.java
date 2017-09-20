/*
 * Copyright (c) 2016.
 * Modified on 02/08/2016.
 */

package cm.aptoide.pt.view.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.networking.IdsRepository;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreUtils;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.displayable.DisplayablesFactory;
import com.facebook.appevents.AppEventsLogger;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 29-04-2016.
 */
public abstract class StoreTabWidgetsGridRecyclerFragment extends StoreTabGridRecyclerFragment {

  private SharedPreferences sharedPreferences;
  private IdsRepository idsRepository;
  protected AptoideAccountManager accountManager;
  protected StoreUtilsProxy storeUtilsProxy;
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private StoreCredentialsProvider storeCredentialsProvider;
  protected InstalledRepository installedRepository;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private QManager qManager;
  private TokenInvalidator tokenInvalidator;
  protected StoreAnalytics storeAnalytics;
  private String partnerId;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    partnerId = ((AptoideApplication) getContext().getApplicationContext()).getPartnerId();
    sharedPreferences =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences();
    qManager = ((AptoideApplication) getContext().getApplicationContext()).getQManager();
    storeCredentialsProvider = new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(
        ((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class));
    idsRepository = ((AptoideApplication) getContext().getApplicationContext()).getIdsRepository();
    httpClient = ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    bodyInterceptor =
        ((AptoideApplication) getContext().getApplicationContext()).getBaseBodyInterceptorV7Pool();
    tokenInvalidator =
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator();
    storeUtilsProxy = new StoreUtilsProxy(accountManager, bodyInterceptor, storeCredentialsProvider,
        AccessorFactory.getAccessorFor(((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class), httpClient,
        WebService.getDefaultConverter(), tokenInvalidator,
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences());
    installedRepository =
        RepositoryFactory.getInstalledRepository(getContext().getApplicationContext());
    storeAnalytics =
        new StoreAnalytics(AppEventsLogger.newLogger(getContext().getApplicationContext()),
            Analytics.getInstance());
  }

  protected Observable<List<Displayable>> parseDisplayables(GetStoreWidgets getStoreWidgets) {
    return Observable.from(getStoreWidgets.getDataList()
        .getList())
        .flatMap(wsWidget -> DisplayablesFactory.parse(wsWidget, storeTheme, storeRepository,
            storeContext, getContext(), accountManager, storeUtilsProxy,
            (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE),
            getContext().getResources(), installedRepository, storeAnalytics))
        .toList()
        .first();
  }

  protected Observable<List<GetStoreWidgets.WSWidget>> loadGetStoreWidgets(
      GetStoreWidgets getStoreWidgets, boolean refresh, String url) {
    return Observable.from(getStoreWidgets.getDataList()
        .getList())
        .flatMap(wsWidget -> {
          return WSWidgetsUtils.loadWidgetNode(wsWidget,
              StoreUtils.getStoreCredentialsFromUrl(url, storeCredentialsProvider), refresh,
              idsRepository.getUniqueIdentifier(),
              AdNetworkUtils.isGooglePlayServicesAvailable(getContext().getApplicationContext()),
              partnerId, accountManager.isAccountMature(), bodyInterceptor, httpClient,
              converterFactory, qManager.getFilters(ManagerPreferences.getHWSpecsFilter(
                  ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences())),
              tokenInvalidator, sharedPreferences, getContext().getResources(),
              ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)),
              (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE),
              ((AptoideApplication) getContext().getApplicationContext()).getVersionCodeProvider());
        })
        .toList()
        .flatMapIterable(wsWidgets -> getStoreWidgets.getDataList()
            .getList())
        .toList()
        .first();
  }


}
