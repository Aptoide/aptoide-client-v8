package cm.aptoide.pt.v8engine.social.data.publisher;

import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;

/**
 * Created by jdandrade on 27/06/2017.
 */

public class Poster {
  private final String primaryName;
  private final String primaryAvatar;
  private final String secondaryAvatar;
  private final String secondaryName;
  private final Comment.User user;
  private final Store store;

  public Poster(Comment.User user, Store store) {
    this.user = user;
    this.store = store;
    if (doesUserHasStore()) {
      this.primaryName = store.getName();
      this.primaryAvatar = store.getAvatar();
      if (isUserPublic()) {
        this.secondaryName = getUserName(user);
        this.secondaryAvatar = user.getAvatar();
      } else {
        this.secondaryName = "";
        this.secondaryAvatar = "";
      }
    } else {
      if (isUserPublic()) {
        this.primaryName = getUserName(user);
        this.primaryAvatar = user.getAvatar();
        this.secondaryName = "";
        this.secondaryAvatar = "";
      } else {
        this.primaryName = "";
        this.primaryAvatar = "";
        this.secondaryName = "";
        this.secondaryAvatar = "";
      }
    }
  }

  private boolean doesUserHasStore() {
    if (store != null) {
      return true;
    }
    return false;
  }

  private String getUserName(Comment.User user) {
    return TextUtils.isEmpty(user.getName()) ? "no-user" : user.getName();
  }

  public String getPrimaryName() {
    return primaryName;
  }

  public String getPrimaryAvatar() {
    return primaryAvatar;
  }

  public String getSecondaryAvatar() {
    return secondaryAvatar;
  }

  public String getSecondaryName() {
    return secondaryName;
  }

  public boolean isUserPublic() {
    if (user != null) {
      return true;
    }
    return false;
  }
}
