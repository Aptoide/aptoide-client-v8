package cm.aptoide.pt.shareapps.socket.file;

import cm.aptoide.pt.shareapps.socket.AptoideClientSocket;
import cm.aptoide.pt.shareapps.socket.entities.FileInfo;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by neuro on 27-01-2017.
 */

public class AptoideFileClientSocket extends AptoideClientSocket {

  private final List<FileInfo> fileInfos;

  public AptoideFileClientSocket(String host, int port, List<FileInfo> fileInfos) {
    super(host, port);
    this.fileInfos = fileInfos;
  }

  public AptoideFileClientSocket(int bufferSize, String host, int port, List<FileInfo> fileInfos) {
    super(bufferSize, host, port);
    this.fileInfos = fileInfos;
  }

  @Override protected void onConnected(Socket socket) throws IOException {

    for (FileInfo fileInfo : fileInfos) {
      System.out.println(Thread.currentThread().getId() + ": Start receiving " + fileInfo);
      OutputStream out = new FileOutputStream(fileInfo.getFilePath());

      copy(socket.getInputStream(), out, fileInfo.getSize());
      out.close();
    }
  }
}
