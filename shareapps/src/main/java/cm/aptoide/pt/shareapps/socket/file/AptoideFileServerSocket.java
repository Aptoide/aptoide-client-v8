package cm.aptoide.pt.shareapps.socket.file;

import cm.aptoide.pt.shareapps.socket.AptoideServerSocket;
import cm.aptoide.pt.shareapps.socket.AptoideSocket;
import cm.aptoide.pt.shareapps.socket.interfaces.FileServerLifecycle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by neuro on 27-01-2017.
 */
public class AptoideFileServerSocket<T> extends AptoideServerSocket {

  private final List<String> filePaths;
  private boolean startedSending = false;

  private T fileDescriptor;
  private FileServerLifecycle<T> fileServerLifecycle;

  public AptoideFileServerSocket(int port, List<String> filePaths, int timeout) {
    super(port, timeout);
    this.filePaths = filePaths;
  }

  public AptoideFileServerSocket(int bufferSize, int port, List<String> filePaths, int timeout) {
    super(bufferSize, port, timeout);
    this.filePaths = filePaths;
  }

  @Override public AptoideSocket start() {
    AptoideSocket start = super.start();
    if (fileServerLifecycle != null) {
      fileServerLifecycle.onFinishSending(fileDescriptor);
    }
    return start;
  }

  @Override protected void onNewClient(Socket socket) {

    if (!startedSending && fileServerLifecycle != null) {
      fileServerLifecycle.onStartSending(fileDescriptor);
      startedSending = true;
    }

    InputStream in = null;

    try {
      for (String filePath : filePaths) {
        System.out.println(Thread.currentThread().getId() + ": Started sending " + filePath);
        in = new FileInputStream(filePath);
        copy(in, socket.getOutputStream(), null);
        System.out.println(Thread.currentThread().getId() + ": Finished sending " + filePath);
        in.close();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public AptoideFileServerSocket<T> setFileServerLifecycle(T fileDescriptor,
      FileServerLifecycle<T> fileServerLifecycle) {

    if (fileDescriptor == null) {
      throw new IllegalArgumentException("fileDescriptor cannot be null!");
    }

    if (fileServerLifecycle == null) {
      throw new IllegalArgumentException("fileServerLifecycle cannot be null!");
    }

    this.fileDescriptor = fileDescriptor;
    this.fileServerLifecycle = fileServerLifecycle;

    return this;
  }
}
