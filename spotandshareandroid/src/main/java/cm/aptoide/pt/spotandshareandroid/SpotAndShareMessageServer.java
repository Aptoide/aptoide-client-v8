package cm.aptoide.pt.spotandshareandroid;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.interfaces.FileLifecycleProvider;
import cm.aptoide.pt.spotandshare.socket.interfaces.HostsChangedCallback;
import cm.aptoide.pt.spotandshare.socket.interfaces.OnError;
import cm.aptoide.pt.spotandshare.socket.interfaces.SocketBinder;
import cm.aptoide.pt.spotandshare.socket.message.client.AptoideMessageClientSocket;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.AndroidAppInfoAccepter;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.StorageCapacity;
import cm.aptoide.pt.spotandshare.socket.message.messages.v1.RequestPermissionToSend;
import cm.aptoide.pt.spotandshare.socket.message.server.AptoideMessageServerSocket;
import cm.aptoide.pt.spotandshareandroid.util.MessageServerConfiguration;
import java.io.IOException;
import java.util.List;

/**
 * Created by neuro on 10-07-2017.
 */

public class SpotAndShareMessageServer {

  private static final String HOTSPOT_DEFAULT_ADDRESS = "192.168.43.1";

  private AptoideMessageServerSocket aptoideMessageServerSocket;
  private AptoideMessageClientSocket aptoideMessageClientSocket;

  private final int port;

  public SpotAndShareMessageServer(int port) {
    this.port = port;
  }

  public void startServer(HostsChangedCallback hostsCallbackManager) {
    if (aptoideMessageServerSocket != null && !aptoideMessageServerSocket.isShutdown()) {
      throw new IllegalStateException("Server Already started!");
    } else {
      aptoideMessageServerSocket =
          new AptoideMessageServerSocket(port, Integer.MAX_VALUE, Integer.MAX_VALUE);
      aptoideMessageServerSocket.setHostsChangedCallbackCallback(hostsCallbackManager);
      aptoideMessageServerSocket.startAsync();
    }
  }

  public void startClient(MessageServerConfiguration messageServerConfiguration) {
    if (aptoideMessageClientSocket != null && aptoideMessageClientSocket.isEnabled()) {
      throw new IllegalStateException("Client Already started!");
    } else {
      aptoideMessageClientSocket = new AptoideMessageClientSocket(HOTSPOT_DEFAULT_ADDRESS, port,
          messageServerConfiguration.getExternalStoragepath(),
          messageServerConfiguration.getStorageCapacity(),
          messageServerConfiguration.getFileLifecycleProvider(),
          messageServerConfiguration.getSocketBinder(), messageServerConfiguration.getOnError(),
          Integer.MAX_VALUE, messageServerConfiguration.getAndroidAppInfoAccepter());
      aptoideMessageClientSocket.startAsync();
    }
  }

  public void startClient(String externalStoragepath, StorageCapacity storageCapacity,
      FileLifecycleProvider<AndroidAppInfo> fileLifecycleProvider, SocketBinder socketBinder,
      OnError<IOException> onError, AndroidAppInfoAccepter androidAppInfoAccepter) {
    if (aptoideMessageClientSocket != null && aptoideMessageClientSocket.isEnabled()) {
      throw new IllegalStateException("Client Already started!");
    } else {
      aptoideMessageClientSocket =
          new AptoideMessageClientSocket(HOTSPOT_DEFAULT_ADDRESS, port, externalStoragepath,
              storageCapacity, fileLifecycleProvider, socketBinder, onError, Integer.MAX_VALUE,
              androidAppInfoAccepter);
      aptoideMessageClientSocket.startAsync();
    }
  }

  public void sendApp(AndroidAppInfo androidAppInfo) {
    aptoideMessageClientSocket.send(
        new RequestPermissionToSend(aptoideMessageClientSocket.getLocalhost(), androidAppInfo));
  }

  public void sendApps(List<AndroidAppInfo> androidAppInfoList) {
    for (AndroidAppInfo androidAppInfo : androidAppInfoList) {
      sendApp(androidAppInfo);
    }
  }

  public void exit() {
    aptoideMessageClientSocket.disable();
    aptoideMessageServerSocket.shutdown();
  }
}
