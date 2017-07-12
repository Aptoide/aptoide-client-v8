package cm.aptoide.pt.spotandshare.socket.message.client;

import cm.aptoide.pt.spotandshare.socket.AptoideClientSocket;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.entities.Host;
import cm.aptoide.pt.spotandshare.socket.exception.ServerLeftException;
import cm.aptoide.pt.spotandshare.socket.interfaces.FileLifecycleProvider;
import cm.aptoide.pt.spotandshare.socket.interfaces.OnError;
import cm.aptoide.pt.spotandshare.socket.interfaces.SocketBinder;
import cm.aptoide.pt.spotandshare.socket.message.Message;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.AndroidAppInfoAccepter;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.StorageCapacity;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by neuro on 29-01-2017.
 */

public class AptoideMessageClientSocket extends AptoideClientSocket {

  protected final AptoideMessageClientController aptoideMessageController;

  public AptoideMessageClientSocket(String host, int port, String rootDir,
      StorageCapacity storageCapacity, FileLifecycleProvider<AndroidAppInfo> fileLifecycleProvider,
      SocketBinder socketBinder, OnError<IOException> onError, int timeout,
      AndroidAppInfoAccepter androidAppInfoAccepter, String username) {
    super(host, port, timeout);
    this.aptoideMessageController =
        new AptoideMessageClientController(this, rootDir, storageCapacity, fileLifecycleProvider,
            socketBinder, onError, androidAppInfoAccepter, username);
    this.onError = onError;
  }

  public AptoideMessageClientSocket(String host, String fallbackHostName, int port, String rootDir,
      StorageCapacity storageCapacity, FileLifecycleProvider<AndroidAppInfo> fileLifecycleProvider,
      SocketBinder socketBinder, OnError<IOException> onError, int timeout,
      AndroidAppInfoAccepter androidAppInfoAccepter, String username) {
    super(host, fallbackHostName, port, timeout);
    this.aptoideMessageController =
        new AptoideMessageClientController(this, rootDir, storageCapacity, fileLifecycleProvider,
            socketBinder, onError, androidAppInfoAccepter, username);
    this.onError = onError;
  }

  @Override public void shutdown() {
    aptoideMessageController.disable();
    super.shutdown();
  }

  @Override protected void onConnected(Socket socket) throws IOException {
    aptoideMessageController.onConnect(socket);
  }

  public Host getHost() {
    return aptoideMessageController.getHost();
  }

  public Host getLocalhost() {
    return aptoideMessageController.getLocalhost();
  }

  public void onConnect(Socket socket) throws IOException {
    aptoideMessageController.onConnect(socket);
  }

  public boolean sendWithAck(Message message) throws InterruptedException {
    return aptoideMessageController.sendWithAck(message);
  }

  public void exit() {
    disable();
    aptoideMessageController.exit();
  }

  public void disable() {
    aptoideMessageController.disable();
    onError = null;
  }

  public boolean isEnabled() {
    return aptoideMessageController.isEnabled();
  }

  public void send(Message message) {
    aptoideMessageController.send(message);
  }

  public void serverLeft() {
    System.out.println("serverLeft called");
    if (onError != null) {
      onError.onError(new ServerLeftException("Server Left"));
    }
    disable();
  }
}
