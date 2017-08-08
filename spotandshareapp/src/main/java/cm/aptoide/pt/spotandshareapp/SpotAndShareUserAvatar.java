package cm.aptoide.pt.spotandshareapp;

import cm.aptoide.pt.spotandshareapp.view.SpotAndShareAvatar;

/**
 * Created by filipe on 23-06-2017.
 */

public class SpotAndShareUserAvatar implements SpotAndShareAvatar {

  private String avatarString;
  private int avatarId;
  private boolean selected;

  public SpotAndShareUserAvatar(int avatarId, String avatarString, boolean selected) {
    this.avatarId = avatarId;
    this.avatarString = avatarString;
    this.selected = selected;
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

  @Override public boolean isSelected() {
    return selected;
  }
}
