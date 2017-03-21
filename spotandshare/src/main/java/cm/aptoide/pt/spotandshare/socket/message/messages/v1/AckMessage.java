package cm.aptoide.pt.spotandshare.socket.message.messages.v1;

import cm.aptoide.pt.spotandshare.socket.entities.Host;
import cm.aptoide.pt.spotandshare.socket.message.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by neuro on 30-01-2017.
 */
@ToString(callSuper = true) public class AckMessage extends Message {

  private static final long serialVersionUID = -5038798843482552911L;

  @Getter @Setter private boolean success;

  public AckMessage(Host localhost) {
    super(localhost);
  }
}
