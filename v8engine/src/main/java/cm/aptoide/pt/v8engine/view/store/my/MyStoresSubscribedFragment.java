package cm.aptoide.pt.v8engine.view.store.my;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetMyStoreListRequest;
import cm.aptoide.pt.model.v7.Layout;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.store.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablesFactory;
import cm.aptoide.pt.v8engine.view.store.GetStoreEndlessFragment;
import cm.aptoide.pt.v8engine.view.store.GridStoreDisplayable;
import cm.aptoide.pt.v8engine.view.store.recommended.RecommendedStoreDisplayable;
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

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    storeCredentialsProvider = new StoreCredentialsProviderImpl();
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    bodyInterceptor = ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    httpClient = ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
  }

  @Override protected V7<ListStores, ? extends Endless> buildRequest(boolean refresh, String url) {
    GetMyStoreListRequest request =
        GetMyStoreListRequest.of(url, true, bodyInterceptor, httpClient, converterFactory);

    return request;
  }

  @Override protected Action1<ListStores> buildAction() {
    return listStores -> addDisplayables(getStoresDisplayable(listStores.getDatalist()
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
                      AccessorFactory.getAccessorFor(cm.aptoide.pt.database.realm.Store.class),
                      httpClient, WebService.getDefaultConverter()), storeCredentialsProvider));
        } else {
          storesDisplayables.add(new GridStoreDisplayable(list.get(i)));
        }
      }
    }
    return storesDisplayables;
  }
}
