package cm.aptoide.pt.spotandshare.socket.message.messages.v1;

import cm.aptoide.pt.spotandshare.socket.entities.Friend;
import cm.aptoide.pt.spotandshare.socket.entities.Host;
import cm.aptoide.pt.spotandshare.socket.message.Message;

/**
 * Created by neuro on 12-07-2017.
 */
public class WelcomeMessage extends Message {

  private static final long serialVersionUID = 3171074727404858798L;

  private final Friend friend;

  public WelcomeMessage(Host localHost, Friend friend) {
    super(localHost);
    this.friend = friend;
  }
}
