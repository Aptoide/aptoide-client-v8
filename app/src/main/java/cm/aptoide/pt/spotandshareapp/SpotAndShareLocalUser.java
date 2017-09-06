package cm.aptoide.pt.spotandshareapp;

import cm.aptoide.pt.spotandshareapp.view.SpotAndShareAvatar;

/**
 * Created by filipe on 23-06-2017.
 */

public class SpotAndShareLocalUser {

  private String username;
  private SpotAndShareAvatar avatar;

  public SpotAndShareLocalUser(String username, SpotAndShareAvatar avatar) {
    this.username = username;
    this.avatar = avatar;
  }

  public String getUsername() {
    return username;
  }

  public SpotAndShareAvatar getAvatar() {
    return avatar;
  }
}
