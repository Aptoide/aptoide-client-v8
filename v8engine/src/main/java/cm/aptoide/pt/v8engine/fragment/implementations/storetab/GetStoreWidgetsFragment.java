package cm.aptoide.pt.v8engine.fragment.implementations.storetab;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 26-12-2016.
 */

public class GetStoreWidgetsFragment extends StoreTabGridRecyclerFragment {

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);

    RepositoryFactory.getRequestRepositoty()
        .getStoreWidgets(url)
        .observe(refresh)
        .observeOn(Schedulers.io())
        .subscribe(getStoreWidgets -> {
          loadGetStoreWidgets(getStoreWidgets, refresh);
        }, this::finishLoading);
  }
}
