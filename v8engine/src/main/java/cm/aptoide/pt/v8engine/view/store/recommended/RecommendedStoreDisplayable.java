package cm.aptoide.pt.v8engine.view.store.recommended;

import android.content.Context;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.store.StoreUtils;
import cm.aptoide.pt.v8engine.store.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import rx.Observable;

/**
 * Created by trinkes on 06/12/2016.
 */

public class RecommendedStoreDisplayable extends DisplayablePojo<Store> {

  private AptoideAccountManager accountManager;
  private StoreRepository storeRepository;
  private StoreUtilsProxy storeUtilsProxy;
  private StoreCredentialsProvider storeCredentialsProvider;

  public RecommendedStoreDisplayable() {
  }

  public RecommendedStoreDisplayable(Store pojo, StoreRepository storeRepository,
      AptoideAccountManager accountManager, StoreUtilsProxy storeUtilsProxy,
      StoreCredentialsProvider storeCredentialsProvider) {
    super(pojo);
    this.storeRepository = storeRepository;
    this.accountManager = accountManager;
    this.storeUtilsProxy = storeUtilsProxy;
    this.storeCredentialsProvider = storeCredentialsProvider;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_recommended_store;
  }

  Observable<Boolean> isFollowing() {
    return storeRepository.isSubscribed(getPojo().getId());
  }

  public void subscribeStore(Context context) {
    storeUtilsProxy.subscribeStore(getPojo().getName());
  }

  void unsubscribeStore() {
    if (accountManager.isLoggedIn()) {
      accountManager.unsubscribeStore(getPojo().getName(),
          storeCredentialsProvider.get(getPojo().getName())
              .getName(), storeCredentialsProvider.get(getPojo().getName())
              .getPasswordSha1());
    }
    StoreUtils.unSubscribeStore(getPojo().getName(), accountManager, storeCredentialsProvider);
  }

  void openStoreFragment(FragmentNavigator navigator) {
    navigator.navigateTo(V8Engine.getFragmentProvider()
        .newStoreFragment(getPojo().getName(), getPojo().getAppearance()
            .getTheme()));
  }
}
