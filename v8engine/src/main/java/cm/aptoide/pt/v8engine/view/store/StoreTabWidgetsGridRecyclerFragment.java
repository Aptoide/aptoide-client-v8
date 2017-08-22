/*
 * Copyright (c) 2016.
 * Modified on 02/08/2016.
 */

package cm.aptoide.pt.v8engine.view.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.database.AccessorFactory;
import cm.aptoide.pt.v8engine.install.InstalledRepository;
import cm.aptoide.pt.v8engine.networking.IdsRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.store.StoreUtils;
import cm.aptoide.pt.v8engine.store.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablesFactory;
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
  private AptoideAccountManager accountManager;
  private StoreUtilsProxy storeUtilsProxy;
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private StoreCredentialsProvider storeCredentialsProvider;
  private InstalledRepository installedRepository;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private QManager qManager;
  private TokenInvalidator tokenInvalidator;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    sharedPreferences =
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences();
    qManager = ((V8Engine) getContext().getApplicationContext()).getQManager();
    storeCredentialsProvider = new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(
        ((V8Engine) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class));
    idsRepository = ((V8Engine) getContext().getApplicationContext()).getIdsRepository();
    httpClient = ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    bodyInterceptor = ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    tokenInvalidator = ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator();
    storeUtilsProxy = new StoreUtilsProxy(accountManager, bodyInterceptor, storeCredentialsProvider,
        AccessorFactory.getAccessorFor(((V8Engine) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class), httpClient,
        WebService.getDefaultConverter(), tokenInvalidator,
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());
    installedRepository =
        RepositoryFactory.getInstalledRepository(getContext().getApplicationContext());
  }

  protected Observable<List<Displayable>> loadGetStoreWidgets(GetStoreWidgets getStoreWidgets,
      boolean refresh, String url) {
    return Observable.from(getStoreWidgets.getDataList()
        .getList())
        .flatMap(wsWidget -> {
          return WSWidgetsUtils.loadWidgetNode(wsWidget,
              StoreUtils.getStoreCredentialsFromUrl(url, storeCredentialsProvider), refresh,
              idsRepository.getUniqueIdentifier(),
              AdNetworkUtils.isGooglePlayServicesAvailable(getContext().getApplicationContext()),
              V8Engine.getConfiguration()
                  .getPartnerId(), accountManager.isAccountMature(), bodyInterceptor, httpClient,
              converterFactory, qManager.getFilters(ManagerPreferences.getHWSpecsFilter(
                  ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences())),
              tokenInvalidator, sharedPreferences, getContext().getResources(),
              ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)),
              (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE),
              ((V8Engine) getContext().getApplicationContext()).getVersionCodeProvider());
        })
        .toList()
        .flatMapIterable(wsWidgets -> getStoreWidgets.getDataList()
            .getList())
        .concatMap(wsWidget -> {
          return DisplayablesFactory.parse(wsWidget, storeTheme, storeRepository, storeContext,
              getContext(), accountManager, storeUtilsProxy,
              (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE),
              getContext().getResources(), installedRepository);
        })
        .toList()
        .first();
  }
}
