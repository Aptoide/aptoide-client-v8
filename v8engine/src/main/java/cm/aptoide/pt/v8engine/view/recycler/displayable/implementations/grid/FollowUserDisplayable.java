package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.text.TextUtils;
import cm.aptoide.pt.model.v7.GetFollowers;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by trinkes on 16/12/2016.
 */

public class FollowUserDisplayable extends DisplayablePojo<GetFollowers.TimelineUser> {

  public FollowUserDisplayable() {
  }

  public FollowUserDisplayable(GetFollowers.TimelineUser pojo) {
    super(pojo);
  }

  @Override public int getViewLayout() {
    return R.layout.timeline_follow_user;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  public String getUserName() {
    if (TextUtils.isEmpty(getPojo().getName())) {
      if (TextUtils.isEmpty(getPojo().getStore().getName())) {
        return String.valueOf(getPojo().getId());
      } else {
        return getPojo().getStore().getName();
      }
    } else {
      return getPojo().getName();
    }
  }
}
