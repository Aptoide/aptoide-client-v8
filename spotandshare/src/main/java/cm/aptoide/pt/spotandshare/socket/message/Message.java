package cm.aptoide.pt.spotandshare.socket.message;

import cm.aptoide.pt.spotandshare.socket.entities.Host;
import java.io.Serializable;
import lombok.Getter;
import lombok.ToString;

@ToString public abstract class Message implements Serializable {

  private static final long serialVersionUID = -785029695224106385L;

  @Getter protected final Host localHost;

  protected Message(Host localHost) {
    this.localHost = localHost;
  }
}