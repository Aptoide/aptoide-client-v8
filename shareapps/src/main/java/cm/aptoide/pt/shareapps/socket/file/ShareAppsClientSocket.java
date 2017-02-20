package cm.aptoide.pt.shareapps.socket.file;

import cm.aptoide.pt.shareapps.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.shareapps.socket.entities.FileInfo;
import cm.aptoide.pt.shareapps.socket.interfaces.FileClientLifecycle;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * Created by neuro on 27-01-2017.
 */
public class ShareAppsClientSocket extends AptoideFileClientSocket {

  private final AndroidAppInfo androidAppInfo;
  private final FileClientLifecycle<AndroidAppInfo> fileClientLifecycle;
  private boolean startedSending = false;

  public ShareAppsClientSocket(String host, int port, List<FileInfo> fileInfos,
      AndroidAppInfo androidAppInfo, FileClientLifecycle<AndroidAppInfo> fileClientLifecycle) {
    super(host, port, fileInfos);
    this.androidAppInfo = androidAppInfo;
    this.fileClientLifecycle = fileClientLifecycle;
  }

  @Override protected void onConnected(Socket socket) throws IOException {
    if (!startedSending) {
      fileClientLifecycle.onStartReceiving(androidAppInfo);
      startedSending = true;
    }

    super.onConnected(socket);

    fileClientLifecycle.onFinishReceiving(androidAppInfo);
  }
}
