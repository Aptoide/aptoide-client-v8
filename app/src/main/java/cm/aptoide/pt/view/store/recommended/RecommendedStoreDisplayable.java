package cm.aptoide.pt.view.store.recommended;

import android.content.Context;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.repository.StoreRepository;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreUtils;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;
import rx.Observable;

/**
 * Created by trinkes on 06/12/2016.
 */

public class RecommendedStoreDisplayable extends DisplayablePojo<Store> {

  private AptoideAccountManager accountManager;
  private StoreRepository storeRepository;
  private StoreUtilsProxy storeUtilsProxy;
  private StoreCredentialsProvider storeCredentialsProvider;
  private String origin = "";

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

  public RecommendedStoreDisplayable(Store store, StoreRepository storeRepository,
      AptoideAccountManager accountManager, StoreUtilsProxy storeUtilsProxy,
      StoreCredentialsProvider storeCredentialsProvider, String origin) {
    super(store);
    this.storeRepository = storeRepository;
    this.accountManager = accountManager;
    this.storeUtilsProxy = storeUtilsProxy;
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.origin = origin;
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

  void unsubscribeStore(Context context) {
    if (accountManager.isLoggedIn()) {
      accountManager.unsubscribeStore(getPojo().getName(),
          storeCredentialsProvider.get(getPojo().getName())
              .getName(), storeCredentialsProvider.get(getPojo().getName())
              .getPasswordSha1());
    }
    StoreUtils.unSubscribeStore(getPojo().getName(), accountManager, storeCredentialsProvider,
        AccessorFactory.getAccessorFor(
            ((AptoideApplication) context.getApplicationContext()).getDatabase(),
            cm.aptoide.pt.database.realm.Store.class));
  }

  void openStoreFragment(FragmentNavigator navigator) {
    navigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newStoreFragment(getPojo().getName(), getPojo().getAppearance()
            .getTheme()));
  }

  public String getOrigin() {
    return origin;
  }
}
