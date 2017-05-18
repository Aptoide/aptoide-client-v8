package cm.aptoide.pt.v8engine.view.store;

import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.LinkedList;
import java.util.List;
import rx.functions.Action1;

/**
 * Created by neuro on 26-12-2016.
 */

public class ListStoresFragment extends GetStoreEndlessFragment<ListStores> {

  @Override protected V7<ListStores, ? extends Endless> buildRequest(boolean refresh, String url) {
    return requestFactory.newListStoresRequest(url);
  }

  @Override protected Action1<ListStores> buildAction() {
    return listStores -> {

      // Load sub nodes
      List<Store> list = listStores.getDatalist()
          .getList();

      List<Displayable> displayables = new LinkedList<>();
      for (Store store : list) {
        displayables.add(new GridStoreDisplayable(store));
      }

      addDisplayables(displayables);
    };
  }
}
