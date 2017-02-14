package cm.aptoide.pt.shareapps.socket.message.server;

import cm.aptoide.pt.shareapps.socket.entities.Host;
import cm.aptoide.pt.shareapps.socket.message.AptoideMessageController;
import cm.aptoide.pt.shareapps.socket.message.HandlersFactory;
import cm.aptoide.pt.shareapps.socket.message.Message;
import cm.aptoide.pt.shareapps.socket.message.interfaces.Sender;
import lombok.Getter;

/**
 * Created by neuro on 29-01-2017.
 */

public class AptoideMessageServerController extends AptoideMessageController
    implements Sender<Message> {

  @Getter private final Host host;

  public AptoideMessageServerController(AptoideMessageServerSocket aptoideMessageServerSocket,
      Host host) {
    super(HandlersFactory.newDefaultServerHandlersList(aptoideMessageServerSocket));
    this.host = host;
  }
}
