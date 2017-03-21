package cm.aptoide.pt.spotandshare.socket.message.messages.v1;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.entities.Host;
import java.io.Serializable;
import lombok.ToString;

/**
 * Created by neuro on 29-01-2017.
 */
@ToString(callSuper = true) public class RequestPermissionToSend extends AndroidAppInfoMessage
    implements Serializable {

  private static final long serialVersionUID = -7964770150534506715L;

  public RequestPermissionToSend(Host localhost, AndroidAppInfo androidAppInfo) {
    super(localhost, androidAppInfo);
  }
}
