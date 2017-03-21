package cm.aptoide.pt.spotandshare.socket.message.messages.v1;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.entities.Host;
import java.io.Serializable;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true) public class ReceiveApk extends AndroidAppInfoMessage
    implements Serializable {

  private static final long serialVersionUID = -4823114091941029036L;

  @Getter private final Host serverHost;

  public ReceiveApk(Host localhost, AndroidAppInfo androidAppInfo, Host serverHost) {
    super(localhost, androidAppInfo);
    this.serverHost = serverHost;
  }
}