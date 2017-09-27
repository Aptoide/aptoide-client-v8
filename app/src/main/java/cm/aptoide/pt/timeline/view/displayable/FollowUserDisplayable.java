package cm.aptoide.pt.timeline.view.displayable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.GetFollowers;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;
import cm.aptoide.pt.store.view.StoreFragment;

/**
 * Created by trinkes on 16/12/2016.
 */

public class FollowUserDisplayable extends DisplayablePojo<GetFollowers.TimelineUser> {

  private boolean isLike;
  private String defaultTheme;

  public FollowUserDisplayable() {
  }

  public FollowUserDisplayable(GetFollowers.TimelineUser pojo, boolean isLike,
      String defaultTheme) {
    super(pojo);
    this.isLike = isLike;
    this.defaultTheme = defaultTheme;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.timeline_follow_user;
  }

  public String getUserName() {
    return getPojo().getName();
  }

  public String storeName() {
    return getPojo().getStore()
        .getName();
  }

  public String getFollowing() {
    long number;
    if (getPojo().getStats() != null) {
      number = getPojo().getStats()
          .getFollowing();
    } else {
      number = 0;
    }
    return String.valueOf(number);
  }

  public String getFollowers() {
    long number;
    if (getPojo().getStats() != null) {
      number = getPojo().getStats()
          .getFollowers();
    } else {
      number = 0;
    }
    return String.valueOf(number);
  }

  public String getStoreName() {
    return getPojo().getStore() == null ? null : getPojo().getStore()
        .getName();
  }

  public String getStoreAvatar() {
    return getPojo().getStore()
        .getAvatar();
  }

  public String getUserAvatar() {
    return getPojo().getAvatar();
  }

  public boolean hasStoreAndUser() {
    return hasStore() && hasUser();
  }

  public boolean hasStore() {
    return getPojo().getStore() != null && (!TextUtils.isEmpty(getPojo().getStore()
        .getName()) || (!TextUtils.isEmpty(getPojo().getStore()
        .getAvatar())));
  }

  public boolean hasUser() {
    return !TextUtils.isEmpty(getPojo().getName()) || !TextUtils.isEmpty(getPojo().getAvatar());
  }

  public int getStoreColor(Context context) {
    Store store = getPojo().getStore();
    if (store != null && store.getAppearance() != null) {
      return StoreTheme.get(store.getAppearance()
          .getTheme())
          .getStoreHeaderColorResource(context.getResources(), context.getTheme());
    } else {
      return StoreTheme.get(defaultTheme)
          .getStoreHeaderColorResource(context.getResources(), context.getTheme());
    }
  }

  public Drawable getButtonBackgroundStoreThemeColor(Context context) {
    Store store = getPojo().getStore();
    StoreTheme storeTheme;
    if (store.getAppearance() != null) {
      storeTheme = StoreTheme.get(store);
    } else {
      storeTheme = StoreTheme.ORANGE;
    }
    return storeTheme.getButtonLayoutDrawable(context.getResources(), context.getTheme());
  }

  public void viewClicked(FragmentNavigator navigator) {
    Store store = getPojo().getStore();
    String theme = getStoreTheme(store);

    if (store != null) {
      navigator.navigateTo(AptoideApplication.getFragmentProvider()
          .newStoreFragment(store.getName(), theme, StoreFragment.OpenType.GetHome), true);
    } else {
      navigator.navigateTo(AptoideApplication.getFragmentProvider()
          .newStoreFragment(getPojo().getId(), theme, StoreFragment.OpenType.GetHome), true);
    }
  }

  private String getStoreTheme(Store store) {
    String theme;
    if (store != null && store.getAppearance() != null) {
      theme = store.getAppearance()
          .getTheme() == null ? defaultTheme : store.getAppearance()
          .getTheme();
    } else {
      theme = defaultTheme;
    }
    return theme;
  }

  public boolean isLike() {
    return isLike;
  }
}
