package cm.aptoide.pt.spotandshare.socket.message;

import cm.aptoide.pt.spotandshare.socket.entities.Host;
import java.io.Serializable;

public abstract class Message implements Serializable {

  private static final long serialVersionUID = -785029695224106385L;

  protected final Host localHost;

  protected Message(Host localHost) {
    this.localHost = localHost;
  }

  public String toString() {
    return "Message(localHost=" + this.localHost + ")";
  }

  public Host getLocalHost() {
    return this.localHost;
  }
}
