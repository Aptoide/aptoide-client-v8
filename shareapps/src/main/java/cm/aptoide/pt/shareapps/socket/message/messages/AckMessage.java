package cm.aptoide.pt.shareapps.socket.message.messages;

import cm.aptoide.pt.shareapps.socket.entities.Host;
import cm.aptoide.pt.shareapps.socket.message.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by neuro on 30-01-2017.
 */
@ToString(callSuper = true) public class AckMessage extends Message {

  @Getter @Setter private boolean success;

  public AckMessage(Host localhost) {
    super(localhost);
  }
}
