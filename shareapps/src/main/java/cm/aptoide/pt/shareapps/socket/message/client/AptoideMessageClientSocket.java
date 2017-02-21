package cm.aptoide.pt.shareapps.socket.message.client;

import cm.aptoide.pt.shareapps.socket.AptoideClientSocket;
import cm.aptoide.pt.shareapps.socket.entities.Host;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by neuro on 29-01-2017.
 */

public class AptoideMessageClientSocket extends AptoideClientSocket {

  protected final AptoideMessageClientController aptoideMessageController;
  public Host filipe;

  public AptoideMessageClientSocket(String host, int port,
      AptoideMessageClientController aptoideMessageController) {
    super(host, port);
    this.aptoideMessageController = aptoideMessageController;
  }

  @Override protected void onConnected(Socket socket) throws IOException {
    System.out.println("ShareApps: filipezzz: " + aptoideMessageController.getHost());
    System.out.println("ShareApps: filipezzz: " + filipe);
    aptoideMessageController.onConnect(socket);
    filipe = new Host(socket.getInetAddress().getHostAddress(), socket.getLocalPort());
  }
}
