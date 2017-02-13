package cm.aptoide.pt.shareapps.socket.message.messages;

import cm.aptoide.pt.shareapps.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.shareapps.socket.entities.Host;
import cm.aptoide.pt.shareapps.socket.message.Message;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by neuro on 29-01-2017.
 */
@ToString(callSuper = true) public abstract class AndroidAppInfoMessage extends Message {

  @Getter private final AndroidAppInfo androidAppInfo;

  public AndroidAppInfoMessage(Host host, AndroidAppInfo androidAppInfo) {
    super(host);
    this.androidAppInfo = androidAppInfo;
  }
}
