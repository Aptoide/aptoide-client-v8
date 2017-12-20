package cm.aptoide.pt.spotandshare.socket.message.server;

import cm.aptoide.pt.spotandshare.socket.AptoideServerSocket;
import cm.aptoide.pt.spotandshare.socket.Print;
import cm.aptoide.pt.spotandshare.socket.entities.Friend;
import cm.aptoide.pt.spotandshare.socket.entities.Host;
import cm.aptoide.pt.spotandshare.socket.message.FriendsManager;
import cm.aptoide.pt.spotandshare.socket.message.Message;
import cm.aptoide.pt.spotandshare.socket.message.messages.v1.HostLeftMessage;
import cm.aptoide.pt.spotandshare.socket.message.messages.v1.NewFriendMessage;
import cm.aptoide.pt.spotandshare.socket.message.messages.v1.ReceiveApk;
import cm.aptoide.pt.spotandshare.socket.message.messages.v1.RequestPermissionToSend;
import cm.aptoide.pt.spotandshare.socket.message.messages.v1.SendApk;
import cm.aptoide.pt.spotandshare.socket.message.messages.v1.ServerLeftMessage;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by neuro on 29-01-2017.
 */
public class AptoideMessageServerSocket extends AptoideServerSocket {

  private static final String TAG = AptoideMessageServerSocket.class.getSimpleName();

  private final ConcurrentLinkedQueue<AptoideMessageServerController> aptoideMessageControllers =
      new ConcurrentLinkedQueue<>();
  private final FriendsManager friendsManager;
  private AptoideMessageServerController aptoideMessageServerController;
  private int availablePort = 10000;

  public AptoideMessageServerSocket(int port, int serverSocketTimeout, int timeout) {
    super(port, serverSocketTimeout, timeout, null);
    this.friendsManager = new FriendsManager();
  }

  @Override public void shutdown() {

    onError = null;

    Iterator<AptoideMessageServerController> iterator = getAptoideMessageControllers().iterator();
    while (iterator.hasNext()) {
      AptoideMessageServerController aptoideMessageServerController = iterator.next();
      aptoideMessageServerController.disable();
      iterator.remove();
    }
    sendToOthersWithAck(null, new ServerLeftMessage(getHost()));

    if (aptoideMessageServerController != null) {
      aptoideMessageServerController.disable();
    }

    super.shutdown();
  }

  @Override protected void onNewClient(Socket socket) throws IOException {

    if (isShutdown()) {
      Print.d(TAG, "Server already shutdown!");
      return;
    }

    aptoideMessageServerController =
        new AptoideMessageServerController(this, Host.fromLocalhost(socket), Host.from(socket),
            onError, executorService);
    aptoideMessageControllers.add(aptoideMessageServerController);
    aptoideMessageServerController.onConnect(socket);
  }

  @Override public void removeHost(Host host) {
    super.removeHost(host);

    friendsManager.removeFriend(host);

    Iterator<AptoideMessageServerController> iterator = aptoideMessageControllers.iterator();
    while (iterator.hasNext()) {
      AptoideMessageServerController aptoideMessageServerController = iterator.next();
      if (aptoideMessageServerController.getHost()
          .getIp()
          .equals(host.getIp())) {
        sendToOthers(host, new HostLeftMessage(getHost(), host));
        aptoideMessageServerController.disable();
        iterator.remove();
        System.out.println(
            "AptoideMessageServerSocket: Host " + host + " removed from the server.");
      }
    }
  }

  public void sendToOthers(Host host, Message message) {
    innerSendToOthers(host, message, Executors.newCachedThreadPool());
  }

  private void innerSendToOthers(Host host, Message message, ExecutorService localExecutorService) {
    dispatchServerAction(() -> {
      for (AptoideMessageServerController aptoideMessageClientController : getAptoideMessageControllers()) {
        if (!aptoideMessageClientController.getHost()
            .equals(host)) {
          localExecutorService.execute(() -> {
            try {
              aptoideMessageClientController.sendWithAck(message);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          });
        }
      }

      localExecutorService.shutdown();
    });
  }

  public void sendToOthersWithAck(Host host, Message message) {
    ExecutorService localExecutorService = Executors.newCachedThreadPool();
    innerSendToOthers(host, message, localExecutorService);
    try {
      // TODO: 01-02-2017 neuro Fix timeout
      localExecutorService.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      System.out.println(
          "AptoideMessageServerSocket: Executor service took too long to complete requests.");
      e.printStackTrace();
    }
  }

  public void requestPermissionToSendApk(RequestPermissionToSend message) {
    int availablePort = getAvailablePort();
    // TODO: 06-02-2017 neuro may not be a bad idea to replace this null with the actual host :/
    sendWithAck(message.getLocalHost(),
        new SendApk(null, message.getAndroidAppInfo(), getConnectedHosts(), availablePort));
    sendToOthers(message.getLocalHost(), new ReceiveApk(getHost(), message.getAndroidAppInfo(),
        new Host(message.getLocalHost()
            .getIp(), availablePort)));
  }

  private int getAvailablePort() {
    return availablePort++;
  }

  public void sendWithAck(Host host, Message message) {
    dispatchServerAction(() -> {
      boolean hostPresent = false;
      // TODO: 01-02-2017 neuro optimize :)
      for (AptoideMessageServerController aptoideMessageController : aptoideMessageControllers) {
        System.out.println(aptoideMessageController.getHost());
        if (aptoideMessageController.getHost()
            .equals(host)) {
          try {
            hostPresent = true;
            aptoideMessageController.sendWithAck(message);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
      if (!hostPresent) {
        throw new IllegalArgumentException(
            "Host " + host + " is not connected to AptoideMessageServerSocket!");
      }
    });
  }

  public void send(Host host, Message message) {
    dispatchServerAction(() -> {
      // TODO: 01-02-2017 neuro optimize :)
      for (AptoideMessageServerController aptoideMessageController : aptoideMessageControllers) {
        if (aptoideMessageController.getHost()
            .equals(host)) {
          aptoideMessageController.send(message);
        }
      }
    });
  }

  public void onNewFriend(Friend friend, Host host) {
    sendFriendsList(host);
    friendsManager.addFriend(friend, host);
    sendToOthers(host, new NewFriendMessage(host, friend));
  }

  private void sendFriendsList(Host host) {
    Set<Entry<Host, Friend>> friendsEntrySet = friendsManager.observeFriendsEntrySet()
        .toBlocking()
        .first();
    Iterator<Entry<Host, Friend>> iterator = friendsEntrySet.iterator();
    while (iterator.hasNext()) {
      Entry<Host, Friend> entry = iterator.next();
      //Friend friend = iterator.next();
      send(host, new NewFriendMessage(entry.getKey(), entry.getValue()));
    }
  }

  public ConcurrentLinkedQueue<AptoideMessageServerController> getAptoideMessageControllers() {
    return this.aptoideMessageControllers;
  }
}
