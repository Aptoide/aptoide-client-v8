package cm.aptoide.pt.v8engine.view.store.recommended;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.util.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.store.GetStoreEndlessFragment;
import cm.aptoide.pt.v8engine.view.store.recommended.RecommendedStoreDisplayable;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.ArrayList;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by trinkes on 21/03/2017.
 */

public class RecommendedStoresFragment extends GetStoreEndlessFragment<ListStores> {

  private AptoideAccountManager accountManager;
  private StoreUtilsProxy storeUtilsProxy;
  private StoreCredentialsProvider storeCredentialsProvider;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    storeCredentialsProvider = new StoreCredentialsProviderImpl();
    IdsRepositoryImpl aptoideClientUUID =
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(), getContext());
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    BodyInterceptor<BaseBody> bodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    storeUtilsProxy = new StoreUtilsProxy(accountManager, bodyInterceptor, storeCredentialsProvider,
        AccessorFactory.getAccessorFor(Store.class));
  }

  @Override protected V7<ListStores, ? extends Endless> buildRequest(boolean refresh, String url) {
    return requestFactory.newGetRecommendedStores(url);
  }

  @Override protected Action1<ListStores> buildAction() {
    return listStores -> Observable.just(listStores)
        .flatMapIterable(getStoreWidgets -> getStoreWidgets.getDatalist().getList())
        .map(store -> new RecommendedStoreDisplayable(store, storeRepository, accountManager,
            storeUtilsProxy, storeCredentialsProvider))
        .toList()
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(disp -> addDisplayables(new ArrayList<>(disp), true));
  }
}
