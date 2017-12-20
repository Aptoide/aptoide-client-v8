package cm.aptoide.pt.spotandshare.socket.message.messages.v1;

import cm.aptoide.pt.spotandshare.socket.entities.Friend;
import cm.aptoide.pt.spotandshare.socket.entities.Host;
import cm.aptoide.pt.spotandshare.socket.message.Message;

/**
 * Created by neuro on 13-07-2017.
 */
public class NewFriendMessage extends Message {

  private final Friend friend;

  public NewFriendMessage(Host localHost, Friend friend) {
    super(localHost);
    this.friend = friend;
  }

  public Friend getFriend() {
    return friend;
  }
}
