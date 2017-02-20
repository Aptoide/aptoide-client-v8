package cm.aptoide.pt.shareapps.socket.message.messages;

import cm.aptoide.pt.shareapps.socket.entities.Host;
import cm.aptoide.pt.shareapps.socket.message.Message;

/**
 * Created by neuro on 13-02-2017.
 */

public class ExitMessage extends Message {

  public ExitMessage(Host localhost) {
    super(localhost);
  }
}
