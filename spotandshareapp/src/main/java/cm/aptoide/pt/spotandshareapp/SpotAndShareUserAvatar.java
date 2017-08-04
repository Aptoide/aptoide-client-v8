package cm.aptoide.pt.spotandshareapp;

import cm.aptoide.pt.spotandshareapp.view.SpotAndShareAvatar;

/**
 * Created by filipe on 23-06-2017.
 */

public class SpotAndShareUserAvatar implements SpotAndShareAvatar {

  private String avatarString;
  private int avatarId;

  public SpotAndShareUserAvatar(String avatarString, int avatarId) {
    this.avatarString = avatarString;
    this.avatarId = avatarId;
  }

  @Override public byte[] serialize() {
    return new byte[0];
  }

  @Override public String getString() {
    return avatarString;
  }

  @Override public int getAvatarId() {
    return avatarId;
  }
}
