package cm.aptoide.pt.shareapps.socket.message.client;

import cm.aptoide.pt.shareapps.socket.AptoideClientSocket;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by neuro on 29-01-2017.
 */

public class AptoideMessageClientSocket extends AptoideClientSocket {

  protected final AptoideMessageClientController aptoideMessageController;

  public AptoideMessageClientSocket(String host, int port,
      AptoideMessageClientController aptoideMessageController) {
    super(host, port);
    this.aptoideMessageController = aptoideMessageController;
  }

  @Override protected void onConnected(Socket socket) throws IOException {
    aptoideMessageController.onConnect(socket);
  }
}
