package cm.aptoide.pt.store.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.ColorInt;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.store.GetHomeMeta;
import cm.aptoide.pt.dataprovider.model.v7.store.HomeUser;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetHomeMetaRequest;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 04-08-2016.
 */
public class GridStoreMetaDisplayable extends DisplayablePojo<GetHomeMeta> {

  public static final int REQUEST_CODE = 53298475;
  private StoreCredentialsProvider storeCredentialsProvider;
  private StoreAnalytics storeAnalytics;
  private BadgeDialogFactory badgeDialogFactory;
  private FragmentNavigator fragmentNavigator;
  private StoreAccessor storeAccessor;
  private BodyInterceptor<BaseBody> bodyInterceptorV7;
  private OkHttpClient client;
  private Converter.Factory converter;
  private TokenInvalidator tokenInvalidator;
  private SharedPreferences sharedPreferences;

  public GridStoreMetaDisplayable() {
  }

  public GridStoreMetaDisplayable(GetHomeMeta pojo,
      StoreCredentialsProvider storeCredentialsProvider, StoreAnalytics storeAnalytics,
      BadgeDialogFactory badgeDialogFactory, FragmentNavigator fragmentNavigator,
      StoreAccessor storeAccessor, BodyInterceptor<BaseBody> bodyInterceptorV7, OkHttpClient client,
      Converter.Factory converter, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(pojo);
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.storeAnalytics = storeAnalytics;
    this.badgeDialogFactory = badgeDialogFactory;
    this.fragmentNavigator = fragmentNavigator;
    this.storeAccessor = storeAccessor;
    this.bodyInterceptorV7 = bodyInterceptorV7;
    this.client = client;
    this.converter = converter;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_store_meta;
  }

  public List<Store.SocialChannel> getSocialLinks() {
    return getStore() == null || getStore().getSocialChannels() == null ? Collections.EMPTY_LIST
        : getStore().getSocialChannels();
  }

  public StoreCredentialsProvider getStoreCredentialsProvider() {
    return storeCredentialsProvider;
  }

  public String getStoreName() {
    return getStore().getName();
  }

  public StoreAnalytics getStoreAnalytics() {
    return storeAnalytics;
  }

  public String getMainIcon() {
    if (getStore() != null) {
      return getStore().getAvatar();
    }
    return getUserIcon();
  }

  public String getSecondaryIcon() {
    return getStore() == null ? null : getUserIcon();
  }

  public String getUserIcon() {
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
    return getUserName();
  }

  private String getUserName() {
    return getUser() == null ? null : getUser().getName();
  }

  public String getSecondaryName() {
    if (getStore() != null) {
      return getUserName();
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
        .map(account -> getStore() != null && account.getStore() != null && account.getStore()
            .getName()
            .equals(getStore().getName()));
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
    return getStore() == null ? 0 : getStore().getId();
  }

  public boolean hasStore() {
    return getStore() != null;
  }

  public GridStoreMetaWidget.HomeMeta.Badge getBadge() {
    if (hasStore()) {
      switch (getPojo().getData()
          .getStore()
          .getBadge()
          .getName()) {
        case BRONZE:
          return GridStoreMetaWidget.HomeMeta.Badge.BRONZE;
        case SILVER:
          return GridStoreMetaWidget.HomeMeta.Badge.SILVER;
        case GOLD:
          return GridStoreMetaWidget.HomeMeta.Badge.GOLD;
        case PLATINUM:
          return GridStoreMetaWidget.HomeMeta.Badge.PLATINUM;
        case NONE:
          return GridStoreMetaWidget.HomeMeta.Badge.TIN;
        default:
          return GridStoreMetaWidget.HomeMeta.Badge.NONE;
      }
    } else {
      return GridStoreMetaWidget.HomeMeta.Badge.NONE;
    }
  }

  public Observable<GridStoreMetaWidget.HomeMeta> getHomeMeta(AptoideAccountManager accountManager,
      Context context) {
    return Observable.merge(isFollowingStore(storeAccessor),
        updateStoreMeta().flatMap(__ -> isFollowingStore(storeAccessor))
            .first())
        .flatMap(isFollowing -> isStoreOwner(accountManager).map(
            isOwner -> new GridStoreMetaWidget.HomeMeta(getMainIcon(), getSecondaryIcon(),
                getMainName(), getSecondaryName(), isOwner, hasStore(), isFollowing,
                getSocialLinks(), getAppsCount(), getFollowersCount(), getFollowingsCount(),
                getDescription(), getColorOrDefault(getStoreTheme(), context), getStoreId(),
                hasStore(), getBadge())));
  }

  private Observable<GetHomeMeta> updateStoreMeta() {
    return fragmentNavigator.results(REQUEST_CODE)
        .filter(result -> result.getResultCode() == Activity.RESULT_OK)
        .flatMap(__ -> GetHomeMetaRequest.of(storeCredentialsProvider.get(getStoreId()),
            bodyInterceptorV7, client, converter, tokenInvalidator, sharedPreferences)
            .observe(true, true))
        .doOnNext(pojo -> setPojo(pojo));
  }

  private @ColorInt int getColorOrDefault(StoreTheme theme, Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return context.getResources()
          .getColor(theme.getPrimaryColor(), context.getTheme());
    } else {
      return context.getResources()
          .getColor(theme.getPrimaryColor());
    }
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

  public long getUserId() {
    return getUser().getId();
  }

  public BadgeDialogFactory getBadgeDialogFactory() {
    return badgeDialogFactory;
  }

  public int getRequestCode() {
    return REQUEST_CODE;
  }
}