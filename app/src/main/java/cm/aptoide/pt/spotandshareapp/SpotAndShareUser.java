package cm.aptoide.pt.spotandshareapp;

/**
 * Created by filipe on 23-06-2017.
 */

public class SpotAndShareUser {

  private String username;
  private SpotAndShareUserAvatar avatar;

  public SpotAndShareUser(String username, SpotAndShareUserAvatar avatar) {
    this.username = username;
    this.avatar = avatar;
  }

  public String getUsername() {
    return username;
  }

  public SpotAndShareUserAvatar getAvatar() {
    return avatar;
  }
}
