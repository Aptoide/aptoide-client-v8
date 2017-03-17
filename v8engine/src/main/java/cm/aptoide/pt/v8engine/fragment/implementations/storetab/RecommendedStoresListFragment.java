package cm.aptoide.pt.v8engine.fragment.implementations.storetab;

import cm.aptoide.pt.model.v7.store.ListStores;
import rx.functions.Action1;

/**
 * Created by trinkes on 17/03/2017.
 */

public class RecommendedStoresListFragment extends MyStoresSubscribedFragment {
  @Override protected Action1<ListStores> buildAction() {
    return listStores -> addDisplayables(getStoresDisplayable(listStores.getDatalist().getList()));
  }
}
