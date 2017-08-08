package cm.aptoide.pt.spotandshareapp.view;

/**
 * Created by filipe on 04-08-2017.
 */

public interface SpotAndShareAvatar {

  byte[] serialize();

  String getString();

  int getAvatarId();

  boolean isSelected();
}
