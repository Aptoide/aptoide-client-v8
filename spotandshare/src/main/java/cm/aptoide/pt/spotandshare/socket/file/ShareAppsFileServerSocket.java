package cm.aptoide.pt.spotandshare.socket.file;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;

/**
 * Created by neuro on 27-01-2017.
 */
public class ShareAppsFileServerSocket extends AptoideFileServerSocket<AndroidAppInfo> {

  public ShareAppsFileServerSocket(int port, AndroidAppInfo androidAppInfo, int timeout) {
    super(port, androidAppInfo.getFiles(), timeout);
  }

  public ShareAppsFileServerSocket(int bufferSize, int port, AndroidAppInfo androidAppInfo,
      int timeout) {
    super(bufferSize, port, androidAppInfo.getFiles(), timeout);
  }
}
