package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.v8engine.fragment.implementations.storetab.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by trinkes on 13/12/2016.
 */

public class MyStoresFragment extends StoreTabGridRecyclerFragment {
  private static final String TAG = MyStoresFragment.class.getSimpleName();
  private Subscription subscription;

  public static MyStoresFragment newInstance(Event event, String title, String storeTheme,
      String tag) {
    Bundle args = buildBundle(event, title, storeTheme, tag);
    MyStoresFragment fragment = new MyStoresFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    StoreRepository storeRepository = RepositoryFactory.getStoreRepository();
    if (subscription == null || subscription.isUnsubscribed()) {
      subscription = storeRepository.getAll().distinct()
          .observeOn(AndroidSchedulers.mainThread())
          .skip(1)
          .compose(bindUntilEvent(LifecycleEvent.DESTROY_VIEW))
          .subscribe(stores -> {
            Logger.d(TAG, "Store database changed, reloading...");
            super.load(false, true, null);
          });
    }
    super.load(create, refresh, savedInstanceState);
  }
}
