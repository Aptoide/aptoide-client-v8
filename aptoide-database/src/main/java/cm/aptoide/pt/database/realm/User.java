package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by franciscocalado on 12/15/17.
 */

public class User extends RealmObject {

  public static final String USER_ID = "userId";
  public static final String USERNAME = "username";

  @PrimaryKey private long userId;
  private String username;
  private String avatar;

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }
}
