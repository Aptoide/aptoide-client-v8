package cm.aptoide.pt.spotandshare.socket.util;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by neuro on 14-02-2017.
 */

public class ServerSocketTimeoutManager extends ScheduledStopable {

  private final ServerSocket serverSocket;

  public ServerSocketTimeoutManager(ServerSocket serverSocket, int timeout) {
    super(timeout);
    this.serverSocket = serverSocket;
  }

  @Override protected void stop() {
    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
