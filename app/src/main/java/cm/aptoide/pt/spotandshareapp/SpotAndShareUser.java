package cm.aptoide.pt.spotandshareapp;

import android.graphics.drawable.Drawable;

/**
 * Created by filipe on 06-09-2017.
 */

public class SpotAndShareUser {

  private String username;
  private Drawable avatar;

  public SpotAndShareUser(String username, Drawable avatar) {
    this.username = username;
    this.avatar = avatar;
  }

  public String getUsername() {
    return username;
  }

  public Drawable getAvatar() {
    return avatar;
  }
}
