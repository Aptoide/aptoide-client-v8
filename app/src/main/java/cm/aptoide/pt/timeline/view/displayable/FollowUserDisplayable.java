package cm.aptoide.pt.timeline.view.displayable;

import android.content.Context;
import android.text.TextUtils;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.GetFollowers;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.store.view.StoreFragment;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;

/**
 * Created by trinkes on 16/12/2016.
 */

public class FollowUserDisplayable extends DisplayablePojo<GetFollowers.TimelineUser> {

  private boolean isLike;
  private int storeColor;
  private int buttonThemeBackground;

  public FollowUserDisplayable() {
  }

  public FollowUserDisplayable(GetFollowers.TimelineUser pojo, boolean isLike, int storeColor,
      int buttonThemeColor) {
    super(pojo);
    this.isLike = isLike;
    this.storeColor = storeColor;
    this.buttonThemeBackground = buttonThemeColor;
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
    return storeColor;
  }

  public int getButtonBackgroundStoreThemeColor(Context context) {
    return buttonThemeBackground;
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
          .getTheme() == null ? BuildConfig.APTOIDE_THEME : store.getAppearance()
          .getTheme();
    } else {
      theme = BuildConfig.APTOIDE_THEME;
    }
    return theme;
  }

  public boolean isLike() {
    return isLike;
  }
}
