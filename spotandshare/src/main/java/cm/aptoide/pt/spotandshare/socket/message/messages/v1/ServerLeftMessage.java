package cm.aptoide.pt.spotandshare.socket.message.messages.v1;

import cm.aptoide.pt.spotandshare.socket.entities.Host;
import cm.aptoide.pt.spotandshare.socket.message.Message;

/**
 * Created by neuro on 13-02-2017.
 */
public class ServerLeftMessage extends Message {

  private static final long serialVersionUID = 3171074727414828798L;

  public ServerLeftMessage(Host localhost) {
    super(localhost);
  }
}
