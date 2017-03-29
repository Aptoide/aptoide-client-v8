package cm.aptoide.pt.spotandshare.socket.message.messages.v1;

import cm.aptoide.pt.spotandshare.socket.entities.Host;
import cm.aptoide.pt.spotandshare.socket.message.Message;

/**
 * Created by neuro on 13-02-2017.
 */

public class ExitMessage extends Message {

  private static final long serialVersionUID = 3171074727404858798L;

  public ExitMessage(Host localhost) {
    super(localhost);
  }
}
