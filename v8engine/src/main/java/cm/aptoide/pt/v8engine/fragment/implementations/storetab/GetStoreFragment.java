package cm.aptoide.pt.v8engine.fragment.implementations.storetab;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.fragment.implementations.HomeFragment;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AdultRowDisplayable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 26-12-2016.
 */

public class GetStoreFragment extends StoreTabGridRecyclerFragment {

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);

    RepositoryFactory.getRequestRepositoty()
        .getStore(url)
        .observe(refresh)
        .observeOn(Schedulers.io())
        .subscribe(getStore -> {
          loadGetStoreWidgets(getStore.getNodes().getWidgets(), refresh);

          // We only want Adult Switch in Home Fragment.
          if (getParentFragment() != null && getParentFragment() instanceof HomeFragment) {
            displayables.add(new AdultRowDisplayable());
          }
        }, this::finishLoading);
  }
}
