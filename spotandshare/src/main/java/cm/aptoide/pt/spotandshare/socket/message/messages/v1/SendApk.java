package cm.aptoide.pt.spotandshare.socket.message.messages.v1;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.entities.Host;
import java.io.Serializable;
import java.util.List;

/**
 * Created by neuro on 29-01-2017.
 */
public class SendApk extends AndroidAppInfoMessage implements Serializable {

  private static final long serialVersionUID = -68949027607957107L;

  private final List<Host> hosts;
  private final int serverPort;

  public SendApk(Host localhost, AndroidAppInfo androidAppInfo, List<Host> hosts, int serverPort) {
    super(localhost, androidAppInfo);
    this.hosts = hosts;
    this.serverPort = serverPort;
  }

  public List<Host> getHosts() {
    return this.hosts;
  }

  public int getServerPort() {
    return this.serverPort;
  }

  public String toString() {
    return "SendApk(super="
        + super.toString()
        + ", hosts="
        + this.getHosts()
        + ", serverPort="
        + this.getServerPort()
        + ")";
  }
}
