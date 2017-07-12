package cm.aptoide.pt.spotandshare.socket.entities;

import java.io.Serializable;

/**
 * Created by neuro on 13-07-2017.
 */

public class Friend implements Serializable {

  private static final long serialVersionUID = 4683334195253012852L;

  private final String username;
  private final byte[] avatar;

  public Friend(String username, byte[] avatar) {
    this.username = username;
    this.avatar = avatar;
  }

  public Friend(String username) {
    this(username, null);
  }
}
