package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import cm.aptoide.pt.model.v7.GetFollowers;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.implementations.TimeLineFollowFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by trinkes on 16/12/2016.
 */

public class FollowUserDisplayable extends DisplayablePojo<GetFollowers.TimelineUser> {

  private TimeLineFollowFragment.FollowFragmentOpenMode openMode;

  public FollowUserDisplayable() {
  }

  public FollowUserDisplayable(GetFollowers.TimelineUser pojo,
      TimeLineFollowFragment.FollowFragmentOpenMode openMode) {
    super(pojo);
    this.openMode = openMode;
  }

  @Override public int getViewLayout() {
    return R.layout.timeline_follow_user;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
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

  public String getStoreAvatar() {
    return getPojo().getStore().getAvatar();
  }

  public String getUserAvatar() {
    return getPojo().getAvatar();
  }

  public boolean hasStoreAndUser() {
    return getPojo().getStore() != null
        && !TextUtils.isEmpty(getPojo().getStore().getName())
        && !TextUtils.isEmpty(getPojo().getName());
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
    StoreThemeEnum storeThemeEnum = StoreThemeEnum.get(store);
    return storeThemeEnum.getButtonLayoutDrawable();
  }

  public boolean hasUser() {
    return !TextUtils.isEmpty(getPojo().getName());
  }

  public boolean hasStore() {
    return getPojo().getStore() != null && !TextUtils.isEmpty(getPojo().getStore().getName());
  }

  public void viewClicked(FragmentShower shower) {
    Store store = getPojo().getStore();
    String theme =
        store.getAppearance().getTheme() == null ? V8Engine.getConfiguration().getDefaultTheme()
            : store.getAppearance().getTheme();
    shower.pushFragmentV4(V8Engine.getFragmentProvider().newStoreFragment(store.getName(), theme));
  }

  public TimeLineFollowFragment.FollowFragmentOpenMode getOpenMode() {
    return openMode;
  }
}
