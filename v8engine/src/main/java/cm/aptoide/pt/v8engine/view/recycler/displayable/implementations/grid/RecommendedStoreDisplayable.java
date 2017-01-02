package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.util.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import rx.Observable;

/**
 * Created by trinkes on 06/12/2016.
 */

public class RecommendedStoreDisplayable extends DisplayablePojo<Store> {
  StoreRepository storeRepository;

  public RecommendedStoreDisplayable() {
  }

  public RecommendedStoreDisplayable(Store pojo, StoreRepository storeRepository) {
    super(pojo);
    this.storeRepository = storeRepository;
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_recommended_store;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  public Observable<Boolean> isFollowing() {
    return storeRepository.isSubscribed(getPojo().getId());
  }

  public void subscribeStore() {
    StoreUtilsProxy.subscribeStore(getPojo().getName());
  }

  public void unsubscribeStore() {
    if (AptoideAccountManager.isLoggedIn()) {
      AptoideAccountManager.unsubscribeStore(getPojo().getName());
    }
    StoreUtils.unsubscribeStore(getPojo().getName());
  }

  public void openStoreFragment(FragmentShower fragmentShower) {
    fragmentShower.pushFragmentV4(V8Engine.getFragmentProvider()
        .newStoreFragment(getPojo().getName(), getPojo().getAppearance().getTheme()));
  }
}
