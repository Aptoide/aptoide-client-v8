package cm.aptoide.pt.spotandshareandroid;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.entities.Friend;
import cm.aptoide.pt.spotandshare.socket.interfaces.HostsChangedCallback;
import cm.aptoide.pt.spotandshare.socket.interfaces.OnError;
import cm.aptoide.pt.spotandshare.socket.interfaces.SocketBinder;
import cm.aptoide.pt.spotandshare.socket.interfaces.TransferLifecycleProvider;
import cm.aptoide.pt.spotandshare.socket.message.client.AptoideMessageClientSocket;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.AndroidAppInfoAccepter;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.StorageCapacity;
import cm.aptoide.pt.spotandshare.socket.message.messages.v1.RequestPermissionToSend;
import cm.aptoide.pt.spotandshare.socket.message.server.AptoideMessageServerSocket;
import cm.aptoide.pt.spotandshareandroid.util.MessageServerConfiguration;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import rx.Observable;

/**
 * Created by neuro on 10-07-2017.
 */

public class SpotAndShareMessageServer {

  private static final String HOTSPOT_DEFAULT_ADDRESS = "192.168.43.1";

  private AptoideMessageServerSocket aptoideMessageServerSocket;
  private AptoideMessageClientSocket aptoideMessageClientSocket;
  private final Friend friend;

  private final int port;

  public SpotAndShareMessageServer(int port, Friend friend) {
    this.friend = friend;
    this.port = port;
  }

  public Observable<Collection<Friend>> observeFriends() {
    return aptoideMessageClientSocket.observeFriends();
  }

  public void startServer(HostsChangedCallback hostsCallbackManager) {
    if (aptoideMessageServerSocket != null && !aptoideMessageServerSocket.isShutdown()) {
      throw new IllegalStateException("Server Already started!");
    } else {
      aptoideMessageServerSocket =
          new AptoideMessageServerSocket(port, Integer.MAX_VALUE, Integer.MAX_VALUE);
      aptoideMessageServerSocket.startAsync();
    }
  }

  public void startClient(MessageServerConfiguration messageServerConfiguration,
      TransferLifecycleProvider<AndroidAppInfo> transferLifecycleProvider) {
    if (aptoideMessageClientSocket != null && aptoideMessageClientSocket.isEnabled()) {
      throw new IllegalStateException("Client Already started!");
    } else {
      aptoideMessageClientSocket = new AptoideMessageClientSocket(HOTSPOT_DEFAULT_ADDRESS, port,
          messageServerConfiguration.getExternalStoragepath(),
          messageServerConfiguration.getStorageCapacity(), transferLifecycleProvider,
          messageServerConfiguration.getSocketBinder(), messageServerConfiguration.getOnError(),
          Integer.MAX_VALUE, messageServerConfiguration.getAndroidAppInfoAccepter(), friend);
      aptoideMessageClientSocket.startAsync();
    }
  }

  public void startClient(String externalStoragepath, StorageCapacity storageCapacity,
      TransferLifecycleProvider<AndroidAppInfo> transferLifecycleProvider,
      SocketBinder socketBinder, OnError<IOException> onError,
      AndroidAppInfoAccepter androidAppInfoAccepter) {
    if (aptoideMessageClientSocket != null && aptoideMessageClientSocket.isEnabled()) {
      throw new IllegalStateException("Client Already started!");
    } else {
      aptoideMessageClientSocket =
          new AptoideMessageClientSocket(HOTSPOT_DEFAULT_ADDRESS, port, externalStoragepath,
              storageCapacity, transferLifecycleProvider, socketBinder, onError, Integer.MAX_VALUE,
              androidAppInfoAccepter, friend);
      aptoideMessageClientSocket.startAsync();
    }
  }

  public void sendApp(AndroidAppInfo androidAppInfo) {
    androidAppInfo.setFriend(friend);
    aptoideMessageClientSocket.send(
        new RequestPermissionToSend(aptoideMessageClientSocket.getLocalhost(), androidAppInfo));
  }

  public void sendApps(List<AndroidAppInfo> androidAppInfoList) {
    for (AndroidAppInfo androidAppInfo : androidAppInfoList) {
      sendApp(androidAppInfo);
    }
  }

  public void exit() {
    if (aptoideMessageClientSocket != null) {
      aptoideMessageClientSocket.disable();
    }
    if (aptoideMessageServerSocket != null) {
      aptoideMessageServerSocket.shutdown();
    }
  }
}
