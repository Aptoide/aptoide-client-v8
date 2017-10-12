package cm.aptoide.pt.store.view;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.dataprovider.model.v7.store.GetHomeMeta;
import cm.aptoide.pt.dataprovider.model.v7.store.HomeUser;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;
import java.util.Collections;
import java.util.List;
import rx.Observable;

/**
 * Created by neuro on 04-08-2016.
 */
public class GridStoreMetaDisplayable extends DisplayablePojo<GetHomeMeta> {

  private StoreCredentialsProvider storeCredentialsProvider;
  private StoreAnalytics storeAnalytics;

  public GridStoreMetaDisplayable() {
  }

  public GridStoreMetaDisplayable(GetHomeMeta pojo,
      StoreCredentialsProvider storeCredentialsProvider, StoreAnalytics storeAnalytics) {
    super(pojo);
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.storeAnalytics = storeAnalytics;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_store_meta;
  }

  public List<Store.SocialChannel> getSocialLinks() {
    return getStore().getSocialChannels() == null ? Collections.EMPTY_LIST
        : getStore().getSocialChannels();
  }

  public StoreCredentialsProvider getStoreCredentialsProvider() {
    return storeCredentialsProvider;
  }

  public String getStoreUserName() {
    return storeCredentialsProvider.get(getStoreName())
        .getUsername();
  }

  public String getStoreName() {
    return getStore().getName();
  }

  public String getStorePassword() {
    return storeCredentialsProvider.get(getStoreName())
        .getPasswordSha1();
  }

  public StoreAnalytics getStoreAnalytics() {
    return storeAnalytics;
  }

  public String getMainIcon() {
    if (getStore() != null) {
      return getStore().getAvatar();
    }
    return getSecondaryIcon();
  }

  public String getSecondaryIcon() {
    if (getUser() != null) {
      return getUser().getAvatar();
    }
    return null;
  }

  private HomeUser getUser() {
    return getPojo().getData()
        .getUser();
  }

  private Store getStore() {
    return getPojo().getData()
        .getStore();
  }

  public String getMainName() {
    Store store = getStore();
    if (store != null) {
      return store.getName();
    }
    return getSecondaryName();
  }

  public String getSecondaryName() {
    HomeUser user = getUser();
    if (user != null) {
      return user.getName();
    }
    return null;
  }

  public long getAppsCount() {
    Store store = getStore();
    if (store != null) {
      return store.getStats()
          .getApps();
    }
    return 0;
  }

  public long getFollowersCount() {
    return getPojo().getData()
        .getStats()
        .getFollowers();
  }

  public long getFollowingsCount() {
    return getPojo().getData()
        .getStats()
        .getFollowing();
  }

  public Observable<Boolean> isStoreOwner(AptoideAccountManager accountManager) {
    return accountManager.accountStatus()
        .first()
        .map(account -> {
          if (getStore() == null || account.getStore() == null) {
            return false;
          }
          return account.getStore()
              .getName()
              .equals(getStore().getName());
        });
  }

  public String getDescription() {
    Store store = getStore();
    if (store != null) {
      return store.getAppearance()
          .getDescription();
    }
    return null;
  }

  public StoreTheme getStoreTheme() {
    Store store = getStore();
    return StoreTheme.get(store == null || store.getAppearance() == null ? "default"
        : store.getAppearance()
            .getTheme());
  }

  public long getStoreId() {
    return getStore().getId();
  }

  public boolean isUserOnly() {
    return getStore() == null;
  }

  public Observable<Boolean> isFollowingStore(StoreAccessor storeAccessor) {
    if (getStore() != null) {
      return storeAccessor.getAll()
          .map(stores -> {
            for (cm.aptoide.pt.database.realm.Store store : stores) {
              if (store.getStoreName()
                  .equals(getStoreName())) {
                return true;
              }
            }
            return false;
          })
          .distinctUntilChanged();
    }
    return Observable.just(false);
  }
}