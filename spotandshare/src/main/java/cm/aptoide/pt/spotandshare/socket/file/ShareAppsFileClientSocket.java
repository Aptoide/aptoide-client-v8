package cm.aptoide.pt.spotandshare.socket.file;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.entities.FileInfo;
import java.util.List;

/**
 * Created by neuro on 27-01-2017.
 */
public class ShareAppsFileClientSocket extends AptoideFileClientSocket<AndroidAppInfo> {

  public ShareAppsFileClientSocket(String host, int port, List<FileInfo> fileInfos) {
    super(host, port, fileInfos, ShareAppsFileServerSocket.TIMEOUT);
  }
}
