package cm.aptoide.pt.shareapps.socket.message.messages;

import cm.aptoide.pt.shareapps.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.shareapps.socket.entities.Host;
import java.io.Serializable;
import lombok.ToString;

@ToString(callSuper = true) public class ReceiveApk extends AndroidAppInfoMessage
    implements Serializable {

  public ReceiveApk(Host host, AndroidAppInfo androidAppInfo) {
    super(host, androidAppInfo);
  }
}