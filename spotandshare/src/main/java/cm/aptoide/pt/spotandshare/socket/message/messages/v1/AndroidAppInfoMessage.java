package cm.aptoide.pt.spotandshare.socket.message.messages.v1;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.entities.Host;
import cm.aptoide.pt.spotandshare.socket.message.Message;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by neuro on 29-01-2017.
 */
@ToString(callSuper = true) public abstract class AndroidAppInfoMessage extends Message {

  private static final long serialVersionUID = 2694553209958755261L;

  @Getter private final AndroidAppInfo androidAppInfo;

  public AndroidAppInfoMessage(Host localhost, AndroidAppInfo androidAppInfo) {
    super(localhost);
    this.androidAppInfo = androidAppInfo;
  }
}
