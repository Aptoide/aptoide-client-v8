package cm.aptoide.pt.spotandshare.socket.message.client;

import cm.aptoide.pt.spotandshare.socket.AptoideClientSocket;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.entities.Host;
import cm.aptoide.pt.spotandshare.socket.exception.ServerLeftException;
import cm.aptoide.pt.spotandshare.socket.interfaces.FileClientLifecycle;
import cm.aptoide.pt.spotandshare.socket.interfaces.FileServerLifecycle;
import cm.aptoide.pt.spotandshare.socket.interfaces.SocketBinder;
import cm.aptoide.pt.spotandshare.socket.message.Message;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.StorageCapacity;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by neuro on 29-01-2017.
 */

public class AptoideMessageClientSocket extends AptoideClientSocket {

  protected final AptoideMessageClientController aptoideMessageController;

  public AptoideMessageClientSocket(String host, int port, String rootDir,
      StorageCapacity storageCapacity, FileServerLifecycle<AndroidAppInfo> serverLifecycle,
      FileClientLifecycle<AndroidAppInfo> fileClientLifecycle, SocketBinder socketBinder) {
    super(host, port);
    this.aptoideMessageController =
        new AptoideMessageClientController(this, rootDir, storageCapacity, serverLifecycle,
            fileClientLifecycle, socketBinder);
  }

  public AptoideMessageClientSocket(String host, String fallbackHostName, int port, String rootDir,
      StorageCapacity storageCapacity, FileServerLifecycle<AndroidAppInfo> serverLifecycle,
      FileClientLifecycle<AndroidAppInfo> fileClientLifecycle, SocketBinder socketBinder) {
    super(host, fallbackHostName, port);
    this.aptoideMessageController =
        new AptoideMessageClientController(this, rootDir, storageCapacity, serverLifecycle,
            fileClientLifecycle, socketBinder);
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
