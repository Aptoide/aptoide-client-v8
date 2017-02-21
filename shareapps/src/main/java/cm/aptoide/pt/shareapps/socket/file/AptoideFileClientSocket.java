package cm.aptoide.pt.shareapps.socket.file;

import cm.aptoide.pt.shareapps.socket.AptoideClientSocket;
import cm.aptoide.pt.shareapps.socket.entities.FileInfo;
import cm.aptoide.pt.shareapps.socket.interfaces.FileClientLifecycle;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by neuro on 27-01-2017.
 */

public class AptoideFileClientSocket<T> extends AptoideClientSocket {

  private final List<FileInfo> fileInfos;
  private boolean startedSending = false;

  private T fileDescriptor;
  private FileClientLifecycle<T> fileClientLifecycle;


  public AptoideFileClientSocket(String host, int port, List<FileInfo> fileInfos) {
    super(host, port);
    this.fileInfos = fileInfos;
  }

  public AptoideFileClientSocket(int bufferSize, String host, int port, List<FileInfo> fileInfos) {
    super(bufferSize, host, port);
    this.fileInfos = fileInfos;
  }

  @Override protected void onConnected(Socket socket) throws IOException {

    if (!startedSending && fileClientLifecycle != null) {
      fileClientLifecycle.onStartReceiving(fileDescriptor);
      startedSending = true;
    }

    for (FileInfo fileInfo : fileInfos) {
      System.out.println(Thread.currentThread().getId() + ": Start receiving " + fileInfo);
      OutputStream out = new FileOutputStream(fileInfo.getFilePath());

      copy(socket.getInputStream(), out, fileInfo.getSize());
      out.close();
    }

    if (fileClientLifecycle != null) {
      fileClientLifecycle.onFinishReceiving(fileDescriptor);
    }
  }

  public AptoideFileClientSocket setFileClientSocket(T fileDescriptor,
      FileClientLifecycle<T> fileClientLifecycle) {

    if (fileDescriptor == null) {
      throw new IllegalArgumentException("fileDescriptor cannot be null!");
    }

    if (fileClientLifecycle == null) {
      throw new IllegalArgumentException("fileClientLifecycle cannot be null!");
    }

    this.fileDescriptor = fileDescriptor;
    this.fileClientLifecycle = fileClientLifecycle;

    return this;
  }
}
