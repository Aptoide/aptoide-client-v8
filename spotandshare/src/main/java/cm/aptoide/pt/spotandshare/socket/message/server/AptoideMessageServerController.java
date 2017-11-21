package cm.aptoide.pt.spotandshare.socket.message.server;

import cm.aptoide.pt.spotandshare.socket.entities.Host;
import cm.aptoide.pt.spotandshare.socket.interfaces.OnError;
import cm.aptoide.pt.spotandshare.socket.message.AptoideMessageController;
import cm.aptoide.pt.spotandshare.socket.message.Message;
import cm.aptoide.pt.spotandshare.socket.message.handlers.v1.DefaultServerHandlersListV1;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.Sender;
import java.io.IOException;

/**
 * Created by neuro on 29-01-2017.
 */

public class AptoideMessageServerController extends AptoideMessageController
    implements Sender<Message> {

  private final Host host;
  private final Host localHost;

  public AptoideMessageServerController(AptoideMessageServerSocket aptoideMessageServerSocket,
      Host localHost, Host host, OnError<IOException> onError) {
    super(DefaultServerHandlersListV1.create(aptoideMessageServerSocket), onError);
    this.localHost = localHost;
    this.host = host;
  }

  public Host getHost() {
    return this.host;
  }

  public Host getLocalHost() {
    return this.localHost;
  }
}
