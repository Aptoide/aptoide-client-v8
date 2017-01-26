package cm.aptoide.pt.v8engine.fragment.implementations.storetab;

import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetMyStoreListRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.model.v7.Layout;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablesFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RecommendedStoreDisplayable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import rx.functions.Action1;

/**
 * Created by neuro on 26-12-2016.
 */

public class MyStoresSubscribedFragment extends GetStoreEndlessFragment<ListStores> {

  private final AptoideClientUUID aptoideClientUUID;

  public MyStoresSubscribedFragment() {
    aptoideClientUUID = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext());
  }

  @Override protected V7<ListStores, ? extends Endless> buildRequest(boolean refresh, String url) {

    GetMyStoreListRequest request =
        GetMyStoreListRequest.of(url, AptoideAccountManager.getAccessToken(),
            aptoideClientUUID.getAptoideClientUUID(), true);

    return request;
  }

  @Override protected Action1<ListStores> buildAction() {
    return listStores -> addDisplayables(getStoresDisplayable(listStores.getDatalist().getList()));
  }

  @Override protected ErrorRequestListener getErrorRequestListener() {
    return (throwable) -> {
      recyclerView.clearOnScrollListeners();
      LinkedList<String> errorsList = new LinkedList<>();
      errorsList.add(WSWidgetsUtils.USER_NOT_LOGGED_ERROR);
      if (WSWidgetsUtils.shouldAddObjectView(errorsList, throwable)) {
        DisplayablesFactory.loadLocalSubscribedStores(storeRepository)
            .compose(bindUntilEvent(LifecycleEvent.DESTROY_VIEW))
            .subscribe(stores -> addDisplayables(getStoresDisplayable(stores)));
      } else {
        finishLoading(throwable);
      }
    };
  }

  @NonNull private ArrayList<Displayable> getStoresDisplayable(List<Store> list) {
    ArrayList<Displayable> storesDisplayables = new ArrayList<>(list.size());
    Collections.sort(list, (store, t1) -> store.getName().compareTo(t1.getName()));
    for (int i = 0; i < list.size(); i++) {
      if (i == 0 || list.get(i - 1).getId() != list.get(i).getId()) {
        if (layout == Layout.LIST) {
          storesDisplayables.add(new RecommendedStoreDisplayable(list.get(i), storeRepository));
        } else {
          storesDisplayables.add(new GridStoreDisplayable(list.get(i)));
        }
      }
    }
    return storesDisplayables;
  }
}
