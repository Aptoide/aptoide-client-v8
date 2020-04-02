package cm.aptoide.pt.store.view.recommended;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.dataprovider.model.v7.store.ListStores;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.store.RoomStoreRepository;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.store.view.GetStoreEndlessFragment;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.ArrayList;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by trinkes on 21/03/2017.
 */

public class RecommendedStoresFragment extends GetStoreEndlessFragment<ListStores> {
  //// TODO(pedro): 19/07/17 More recommended store events here

  @Inject StoreCredentialsProvider storeCredentialsProvider;
  @Inject RoomStoreRepository storeRepository;
  @Inject StoreUtilsProxy storeUtilsProxy;
  private AptoideAccountManager accountManager;

  public static Fragment newInstance() {
    return new RecommendedStoresFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
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
