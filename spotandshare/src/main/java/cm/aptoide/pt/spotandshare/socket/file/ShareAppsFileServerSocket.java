package cm.aptoide.pt.spotandshare.socket.file;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;

/**
 * Created by neuro on 27-01-2017.
 */
public class ShareAppsFileServerSocket extends AptoideFileServerSocket<AndroidAppInfo> {

  static final int SERVER_SOCKET_TIMEOUT = 5000;
  static final int TIMEOUT = 5000;

  public ShareAppsFileServerSocket(int port, AndroidAppInfo androidAppInfo) {
    super(port, androidAppInfo.getFiles(), SERVER_SOCKET_TIMEOUT, TIMEOUT);
  }

  public ShareAppsFileServerSocket(int bufferSize, int port, AndroidAppInfo androidAppInfo) {
    super(bufferSize, port, androidAppInfo.getFiles(), SERVER_SOCKET_TIMEOUT, TIMEOUT);
  }
}
