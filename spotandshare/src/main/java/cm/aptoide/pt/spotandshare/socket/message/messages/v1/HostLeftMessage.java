package cm.aptoide.pt.spotandshare.socket.message.messages.v1;

import cm.aptoide.pt.spotandshare.socket.entities.Host;
import cm.aptoide.pt.spotandshare.socket.message.Message;

/**
 * Created by neuro on 14-02-2017.
 */

public class HostLeftMessage extends Message {

  private static final long serialVersionUID = 6476552013615482398L;

  private final Host hostThatLeft;

  public HostLeftMessage(Host localhost, Host hostThatLeft) {
    super(localhost);
    this.hostThatLeft = hostThatLeft;
  }

  public Host getHostThatLeft() {
    return this.hostThatLeft;
  }
}
