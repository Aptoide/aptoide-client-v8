package cm.aptoide.pt.spotandshare.socket.message.messages.v1;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.entities.Host;
import java.io.Serializable;

public class ReceiveApk extends AndroidAppInfoMessage
    implements Serializable {

  private static final long serialVersionUID = -4823114091941029036L;

  private final Host serverHost;

  public ReceiveApk(Host localhost, AndroidAppInfo androidAppInfo, Host serverHost) {
    super(localhost, androidAppInfo);
    this.serverHost = serverHost;
  }

  public String toString() {
    return "ReceiveApk(super=" + super.toString() + ", serverHost=" + this.serverHost + ")";
  }

  public Host getServerHost() {
    return this.serverHost;
  }
}
