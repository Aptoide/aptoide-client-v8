package cm.aptoide.pt.spotandshare.socket.message.messages.v1;

import cm.aptoide.pt.spotandshare.socket.entities.Host;
import cm.aptoide.pt.spotandshare.socket.message.Message;

/**
 * Created by neuro on 30-01-2017.
 */
public class AckMessage extends Message {

  private static final long serialVersionUID = -5038798843482552911L;

  private boolean success;

  public AckMessage(Host localhost) {
    super(localhost);
  }

  public String toString() {
    return "AckMessage(super=" + super.toString() + ", success=" + this.success + ")";
  }

  public boolean isSuccess() {
    return this.success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public AckMessage(Host localHost, boolean success) {
    super(localHost);
    this.success = success;
  }
}
