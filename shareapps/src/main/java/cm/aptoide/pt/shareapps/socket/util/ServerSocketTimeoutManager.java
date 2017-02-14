package cm.aptoide.pt.shareapps.socket.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by neuro on 14-02-2017.
 */

public class ServerSocketTimeoutManager {

  private final ScheduledExecutorService scheduledExecutorService =
      Executors.newSingleThreadScheduledExecutor();
  private final int timeout;
  private final ServerSocket serverSocket;
  private ScheduledFuture<?> schedule;

  public ServerSocketTimeoutManager(ServerSocket serverSocket, int timeout) {
    this.serverSocket = serverSocket;
    this.timeout = timeout;
  }

  public void reserTimeout() {
    if (schedule != null) {
      schedule.cancel(false);
    }
    schedule = scheduledExecutorService.schedule(stopServer(), timeout, TimeUnit.MILLISECONDS);
  }

  private Runnable stopServer() {
    return () -> {
      try {
        serverSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    };
  }
}
