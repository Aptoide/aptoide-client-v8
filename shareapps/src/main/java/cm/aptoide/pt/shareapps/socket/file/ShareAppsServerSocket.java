package cm.aptoide.pt.shareapps.socket.file;

import cm.aptoide.pt.shareapps.socket.AptoideSocket;
import cm.aptoide.pt.shareapps.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.shareapps.socket.interfaces.FileServerLifecycle;
import java.net.Socket;

/**
 * Created by neuro on 27-01-2017.
 */
public class ShareAppsServerSocket extends AptoideFileServerSocket {

  private final AndroidAppInfo androidAppInfo;
  private final FileServerLifecycle<AndroidAppInfo> fileServerLifecycle;
  private boolean startedSending = false;

  public ShareAppsServerSocket(int port, AndroidAppInfo androidAppInfo,
      FileServerLifecycle<AndroidAppInfo> fileServerLifecycle, int timeout) {
    super(port, androidAppInfo.getFilesPathsList(), timeout);
    this.androidAppInfo = androidAppInfo;
    this.fileServerLifecycle = fileServerLifecycle;
  }

  public ShareAppsServerSocket(int bufferSize, int port, AndroidAppInfo androidAppInfo,
      FileServerLifecycle<AndroidAppInfo> fileServerLifecycle, int timeout) {
    super(bufferSize, port, androidAppInfo.getFilesPathsList(), timeout);
    this.androidAppInfo = androidAppInfo;
    this.fileServerLifecycle = fileServerLifecycle;
  }

  @Override protected void onNewClient(Socket socket) {
    if (!startedSending) {
      fileServerLifecycle.onStartSending(androidAppInfo);
      startedSending = true;
    }
    super.onNewClient(socket);
  }

  @Override public AptoideSocket start() {
    AptoideSocket start = super.start();
    fileServerLifecycle.onFinishSending(androidAppInfo);
    return start;
  }
}
