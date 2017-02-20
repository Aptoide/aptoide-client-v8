package cm.aptoide.pt.shareapps.socket.message.server;

import cm.aptoide.pt.shareapps.socket.AptoideServerSocket;
import cm.aptoide.pt.shareapps.socket.entities.Host;
import cm.aptoide.pt.shareapps.socket.message.Message;
import cm.aptoide.pt.shareapps.socket.message.messages.HostLeftMessage;
import cm.aptoide.pt.shareapps.socket.message.messages.ReceiveApk;
import cm.aptoide.pt.shareapps.socket.message.messages.RequestPermissionToSend;
import cm.aptoide.pt.shareapps.socket.message.messages.SendApk;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

/**
 * Created by neuro on 29-01-2017.
 */
public class AptoideMessageServerSocket extends AptoideServerSocket {

  @Getter private final ConcurrentLinkedQueue<AptoideMessageServerController>
      aptoideMessageControllers = new ConcurrentLinkedQueue<>();

  public AptoideMessageServerSocket(int port, int timeout) {
    super(port, timeout);
  }

  @Override protected void onNewClient(Socket socket) {
    try {
      AptoideMessageServerController shareAppsMessageController =
          new AptoideMessageServerController(this, Host.fromLocalhost(socket), Host.from(socket));
      aptoideMessageControllers.add(shareAppsMessageController);
      shareAppsMessageController.onConnect(socket);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void requestPermissionToSendApk(RequestPermissionToSend message) {
    int availablePort = getAvailablePort();
    // TODO: 06-02-2017 neuro may not be a bad idea to replace this null with the actual host :/
    sendWithAck(message.getLocalHost(),
        new SendApk(null, message.getAndroidAppInfo(), getConnectedHosts(), availablePort));
    sendToOthers(message.getLocalHost(),
        new ReceiveApk(new Host(message.getLocalHost().getIp(), availablePort),
            message.getAndroidAppInfo()));
  }

  private int getAvailablePort() {
    return new Random().nextInt(10000) + 20000;
  }

  public void sendWithAck(Host host, Message message) {
    dispatchServerAction(() -> {
      // TODO: 01-02-2017 neuro optimize :)
      for (AptoideMessageServerController aptoideMessageController : aptoideMessageControllers) {
        if (aptoideMessageController.getHost().equals(host)) {
          try {
            aptoideMessageController.sendWithAck(message);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });
  }

  public void sendToOthers(Host host, Message message) {
    dispatchServerAction(() -> {
      ExecutorService localExecutorService = Executors.newCachedThreadPool();
      for (AptoideMessageServerController aptoideMessageClientController : getAptoideMessageControllers()) {
        if (!host.equals(aptoideMessageClientController.getHost())) {
          localExecutorService.execute(() -> {
            try {
              aptoideMessageClientController.sendWithAck(message);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          });
        }
      }

      // TODO: 01-02-2017 neuro Fix timeout
      try {
        localExecutorService.shutdown();
        localExecutorService.awaitTermination(5, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
        System.out.println("Executor service took too long to complete requests.");
      }
    });
  }

  public void send(Host host, Message message) {
    dispatchServerAction(() -> {
      // TODO: 01-02-2017 neuro optimize :)
      for (AptoideMessageServerController aptoideMessageController : aptoideMessageControllers) {
        if (aptoideMessageController.getHost().equals(host)) {
          aptoideMessageController.send(message);
        }
      }
    });
  }

  public void removeHost(Host host) {
    Iterator<AptoideMessageServerController> iterator = aptoideMessageControllers.iterator();
    while (iterator.hasNext()) {
      AptoideMessageServerController aptoideMessageServerController = iterator.next();
      if (aptoideMessageServerController.getHost().getIp().equals(host.getIp())) {
        iterator.remove();
        System.out.println("Host " + host + " removed from the server.");
        sendToOthers(host, new HostLeftMessage(getHost(), host));
      }
    }
  }
}