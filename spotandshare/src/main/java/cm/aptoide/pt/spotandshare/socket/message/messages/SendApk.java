package cm.aptoide.pt.spotandshare.socket.message.messages;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.entities.Host;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by neuro on 29-01-2017.
 */
@ToString(callSuper = true) @Getter public class SendApk extends AndroidAppInfoMessage
    implements Serializable {

  private final List<Host> hosts;
  private final int serverPort;

  public SendApk(Host localhost, AndroidAppInfo androidAppInfo, List<Host> hosts, int serverPort) {
    super(localhost, androidAppInfo);
    this.hosts = hosts;
    this.serverPort = serverPort;
  }
}
