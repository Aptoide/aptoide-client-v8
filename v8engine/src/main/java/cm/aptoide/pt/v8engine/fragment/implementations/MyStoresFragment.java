package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.StoreRepository;

/**
 * Created by trinkes on 13/12/2016.
 */

public class MyStoresFragment extends StoreTabGridRecyclerFragment {

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    StoreRepository storeRepository = RepositoryFactory.getRepositoryFor(Store.class);
    storeRepository.getAll().subscribe(stores -> {
      reload();
    });
    super.load(create, refresh, savedInstanceState);
  }
}
