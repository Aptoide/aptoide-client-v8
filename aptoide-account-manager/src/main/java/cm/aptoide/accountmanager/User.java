package cm.aptoide.accountmanager;

/**
 * Created by franciscocalado on 12/14/17.
 */

public class User {

  private final long id;
  private final String username;
  private final String avatar;

  public User(long id, String username, String avatar) {

    this.id = id;
    this.username = username;
    this.avatar = avatar;
  }

  public static User emptyUser() {
    return new User(0, "", "");
  }

  public long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getAvatar() {
    return avatar;
  }
}
