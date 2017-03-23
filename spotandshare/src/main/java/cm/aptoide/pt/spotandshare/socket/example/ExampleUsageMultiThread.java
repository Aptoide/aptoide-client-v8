package cm.aptoide.pt.spotandshare.socket.example;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.entities.Host;
import cm.aptoide.pt.spotandshare.socket.message.client.AptoideMessageClientSocket;
import cm.aptoide.pt.spotandshare.socket.message.messages.v1.ExitMessage;
import cm.aptoide.pt.spotandshare.socket.message.messages.v1.RequestPermissionToSend;
import cm.aptoide.pt.spotandshare.socket.message.server.AptoideMessageServerSocket;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by neuro on 29-01-2017.
 */

public class ExampleUsageMultiThread {

  static final AtomicBoolean shouldRequestToSend = new AtomicBoolean(true);
  private final static String LOCAL_FILE_NAME = "/tmp/a.mp4";
  public static int MESSAGE_SERVER_PORT = 53531;

  public static void main(String[] args) throws InterruptedException {

    new AptoideMessageServerSocket(MESSAGE_SERVER_PORT, 5000).startAsync();

    Thread.sleep(1000);

    for (int i = 0; i < 2; i++) {
      newAptoideMessageClientSocket().startAsync();
    }
  }

  private static AptoideMessageClientSocket newAptoideMessageClientSocket() {
    return new AptoideMessageClientSocket("localhost", MESSAGE_SERVER_PORT, null, null, null, null,
        null) {

      @Override protected void onConnected(Socket socket) throws IOException {

        if (shouldRequestToSend.getAndSet(false)) {
          ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
          service.schedule(() -> {
            aptoideMessageController.send(new RequestPermissionToSend(
                new Host(socket.getInetAddress().getHostAddress(), socket.getLocalPort()),
                buildAppInfo()));

            service.schedule(() -> {
              aptoideMessageController.send(new ExitMessage(Host.from(socket)));
            }, 30, TimeUnit.SECONDS);
          }, 1, TimeUnit.SECONDS);
        }

        super.onConnected(socket);
      }

      private AndroidAppInfo buildAppInfo() {
        File apk = new File(LOCAL_FILE_NAME);

        return new AndroidAppInfo("appName", "packageName", apk);
      }
    };
  }
}
