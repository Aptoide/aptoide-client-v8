package cm.aptoide.pt.view.store.recommended;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.v7.store.ListStores;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.view.store.GetStoreEndlessFragment;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.ArrayList;
import okhttp3.OkHttpClient;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by trinkes on 21/03/2017.
 */

public class RecommendedStoresFragment extends GetStoreEndlessFragment<ListStores> {
  //// TODO(pedro): 19/07/17 More recommended store events here

  private AptoideAccountManager accountManager;
  private StoreUtilsProxy storeUtilsProxy;
  private StoreCredentialsProvider storeCredentialsProvider;

  public static Fragment newInstance() {
    return new RecommendedStoresFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    storeCredentialsProvider = new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(
        ((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class));
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    BodyInterceptor<BaseBody> bodyInterceptor =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7();
    final OkHttpClient httpClient =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    storeUtilsProxy = new StoreUtilsProxy(accountManager, bodyInterceptor, storeCredentialsProvider,
        AccessorFactory.getAccessorFor(((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class), httpClient,
        WebService.getDefaultConverter(),
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator(),
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences());
  }

  @Override protected V7<ListStores, ? extends Endless> buildRequest(boolean refresh, String url) {
    return requestFactoryCdnPool.newGetRecommendedStores(url);
  }

  @Override protected Action1<ListStores> buildAction() {
    return listStores -> Observable.just(listStores)
        .flatMapIterable(getStoreWidgets -> getStoreWidgets.getDataList()
            .getList())
        .map(store -> new RecommendedStoreDisplayable(store, storeRepository, accountManager,
            storeUtilsProxy, storeCredentialsProvider, "Recommended Stores More"))
        .toList()
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(disp -> addDisplayables(new ArrayList<>(disp), true));
  }
}
