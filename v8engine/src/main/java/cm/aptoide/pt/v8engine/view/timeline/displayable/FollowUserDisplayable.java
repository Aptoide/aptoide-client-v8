package cm.aptoide.pt.v8engine.view.timeline.displayable;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import cm.aptoide.pt.model.v7.GetFollowers;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.store.StoreFragment;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by trinkes on 16/12/2016.
 */

public class FollowUserDisplayable extends DisplayablePojo<GetFollowers.TimelineUser> {

  private boolean isLike;

  public FollowUserDisplayable() {
  }

  public FollowUserDisplayable(GetFollowers.TimelineUser pojo, boolean isLike) {
    super(pojo);
    this.isLike = isLike;
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
    return getPojo().getStore().getName();
  }

  public String getFollowing() {
    long number;
    if (getPojo().getStats() != null) {
      number = getPojo().getStats().getFollowing();
    } else {
      number = 0;
    }
    return String.valueOf(number);
  }

  public String getFollowers() {
    long number;
    if (getPojo().getStats() != null) {
      number = getPojo().getStats().getFollowers();
    } else {
      number = 0;
    }
    return String.valueOf(number);
  }

  public String getStoreName() {
    return getPojo().getStore() == null ? null : getPojo().getStore().getName();
  }

  public String getStoreAvatar() {
    return getPojo().getStore().getAvatar();
  }

  public String getUserAvatar() {
    return getPojo().getAvatar();
  }

  public boolean hasStoreAndUser() {
    return hasStore() && hasUser();
  }

  public boolean hasStore() {
    return getPojo().getStore() != null && (!TextUtils.isEmpty(getPojo().getStore().getName())
        || (!TextUtils.isEmpty(getPojo().getStore().getAvatar())));
  }

  public boolean hasUser() {
    return !TextUtils.isEmpty(getPojo().getName()) || !TextUtils.isEmpty(getPojo().getAvatar());
  }

  public int getStoreColor() {
    Store store = getPojo().getStore();
    if (store != null && store.getAppearance() != null) {
      return StoreThemeEnum.get(store.getAppearance().getTheme()).getStoreHeaderInt();
    } else {
      return StoreThemeEnum.get(V8Engine.getConfiguration().getDefaultTheme()).getStoreHeaderInt();
    }
  }

  public Drawable getButtonBackgroundStoreThemeColor() {
    Store store = getPojo().getStore();
    StoreThemeEnum storeThemeEnum;
    if (store.getAppearance() != null) {
      storeThemeEnum = StoreThemeEnum.get(store);
    } else {
      storeThemeEnum = StoreThemeEnum.APTOIDE_STORE_THEME_ORANGE;
    }
    return storeThemeEnum.getButtonLayoutDrawable();
  }

  public void viewClicked(FragmentNavigator navigator) {
    Store store = getPojo().getStore();
    String theme = getStoreTheme(store);

    if (store != null) {
      navigator.navigateTo(V8Engine.getFragmentProvider()
          .newStoreFragment(store.getName(), theme, StoreFragment.OpenType.GetHome));
    } else {
      navigator.navigateTo(V8Engine.getFragmentProvider()
          .newStoreFragment(getPojo().getId(), theme, StoreFragment.OpenType.GetHome));
    }
  }

  private String getStoreTheme(Store store) {
    String theme;
    if (store != null && store.getAppearance() != null) {
      theme =
          store.getAppearance().getTheme() == null ? V8Engine.getConfiguration().getDefaultTheme()
              : store.getAppearance().getTheme();
    } else {
      theme = V8Engine.getConfiguration().getDefaultTheme();
    }
    return theme;
  }

  public boolean isLike() {
    return isLike;
  }
}
