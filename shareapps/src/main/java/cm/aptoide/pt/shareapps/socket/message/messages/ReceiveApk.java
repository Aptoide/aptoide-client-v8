package cm.aptoide.pt.shareapps.socket.message.messages;

import cm.aptoide.pt.shareapps.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.shareapps.socket.entities.Host;
import java.io.Serializable;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true) public class ReceiveApk extends AndroidAppInfoMessage
    implements Serializable {

  @Getter private final Host serverHost;

  public ReceiveApk(Host localhost, AndroidAppInfo androidAppInfo, Host serverHost) {
    super(localhost, androidAppInfo);
    this.serverHost = serverHost;
  }
}