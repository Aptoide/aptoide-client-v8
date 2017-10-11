package cm.aptoide.pt.view.store.my;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.ErrorRequestListener;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.Layout;
import cm.aptoide.pt.dataprovider.model.v7.store.ListStores;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetMyStoreListRequest;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.displayable.DisplayablesFactory;
import cm.aptoide.pt.view.store.GetStoreEndlessFragment;
import cm.aptoide.pt.view.store.GridStoreDisplayable;
import cm.aptoide.pt.view.store.recommended.RecommendedStoreDisplayable;
import com.facebook.appevents.AppEventsLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.functions.Action1;

/**
 * Created by neuro on 26-12-2016.
 */

public class MyStoresSubscribedFragment extends GetStoreEndlessFragment<ListStores> {

  private AptoideAccountManager accountManager;
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private StoreCredentialsProvider storeCredentialsProvider;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;
  private StoreAnalytics storeAnalytics;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    tokenInvalidator =
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator();
    storeCredentialsProvider = new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(
        ((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), cm.aptoide.pt.database.realm.Store.class));
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    bodyInterceptor =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7();
    httpClient = ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    storeAnalytics =
        new StoreAnalytics(AppEventsLogger.newLogger(getContext()), Analytics.getInstance());
  }

  @Override protected V7<ListStores, ? extends Endless> buildRequest(boolean refresh, String url) {
    GetMyStoreListRequest request =
        GetMyStoreListRequest.of(url, true, bodyInterceptor, httpClient, converterFactory,
            tokenInvalidator,
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
            getContext().getResources(),
            (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE));

    return request;
  }

  @Override protected Action1<ListStores> buildAction() {
    return listStores -> addDisplayables(getStoresDisplayable(listStores.getDataList()
        .getList()));
  }

  @Override protected ErrorRequestListener getErrorRequestListener() {
    return (throwable) -> {
      getRecyclerView().clearOnScrollListeners();
      LinkedList<String> errorsList = new LinkedList<>();
      errorsList.add(WSWidgetsUtils.USER_NOT_LOGGED_ERROR);
      if (WSWidgetsUtils.shouldAddObjectView(errorsList, throwable)) {
        DisplayablesFactory.loadLocalSubscribedStores(storeRepository)
            .compose(bindUntilEvent(LifecycleEvent.DESTROY))
            .subscribe(stores -> addDisplayables(getStoresDisplayable(stores)), err -> {
              CrashReport.getInstance()
                  .log(err);
            });
      } else {
        finishLoading(throwable);
      }
    };
  }

  @NonNull private ArrayList<Displayable> getStoresDisplayable(List<Store> list) {
    ArrayList<Displayable> storesDisplayables = new ArrayList<>(list.size());
    Collections.sort(list, (store, t1) -> store.getName()
        .compareTo(t1.getName()));
    for (int i = 0; i < list.size(); i++) {
      if (i == 0
          || list.get(i - 1)
          .getId() != list.get(i)
          .getId()) {
        if (layout == Layout.LIST) {
          storesDisplayables.add(
              new RecommendedStoreDisplayable(list.get(i), storeRepository, accountManager,
                  new StoreUtilsProxy(accountManager, bodyInterceptor, storeCredentialsProvider,
                      AccessorFactory.getAccessorFor(
                          ((AptoideApplication) getContext().getApplicationContext()
                              .getApplicationContext()).getDatabase(),
                          cm.aptoide.pt.database.realm.Store.class), httpClient,
                      WebService.getDefaultConverter(), tokenInvalidator,
                      ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences()),
                  storeCredentialsProvider));
        } else {
          storesDisplayables.add(
              new GridStoreDisplayable(list.get(i), "More Followed Stores", storeAnalytics));
        }
      }
    }
    return storesDisplayables;
  }

  public static Fragment newInstance() {
    return new MyStoresSubscribedFragment();
  }
}
