package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.accounts.AccountManager;
import android.content.Context;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
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

  private AptoideAccountManager accountManager;
  private StoreRepository storeRepository;

  public RecommendedStoreDisplayable() {
  }

  public RecommendedStoreDisplayable(Store pojo, StoreRepository storeRepository, Context context) {
    super(pojo);
    this.storeRepository = storeRepository;
    this.accountManager = AptoideAccountManager.getInstance(context, Application.getConfiguration(),
        new SecureCoderDecoder.Builder(context.getApplicationContext()).create(),
        AccountManager.get(context.getApplicationContext()), new IdsRepositoryImpl(
            SecurePreferencesImplementation.getInstance(),
            context.getApplicationContext()));
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_recommended_store;
  }

  public Observable<Boolean> isFollowing() {
    return storeRepository.isSubscribed(getPojo().getId());
  }

  public void subscribeStore() {
    StoreUtilsProxy.subscribeStore(getPojo().getName());
  }

  public void unsubscribeStore() {
    if (accountManager.isLoggedIn()) {
      accountManager.unsubscribeStore(getPojo().getName());
    }
    StoreUtils.unsubscribeStore(getPojo().getName());
  }

  public void openStoreFragment(FragmentShower fragmentShower) {
    fragmentShower.pushFragment(V8Engine.getFragmentProvider()
        .newStoreFragment(getPojo().getName(), getPojo().getAppearance().getTheme()));
  }
}
