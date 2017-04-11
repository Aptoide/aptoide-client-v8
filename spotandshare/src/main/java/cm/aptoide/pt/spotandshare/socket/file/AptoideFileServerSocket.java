package cm.aptoide.pt.spotandshare.socket.file;

import cm.aptoide.pt.spotandshare.socket.AptoideServerSocket;
import cm.aptoide.pt.spotandshare.socket.entities.FileInfo;
import cm.aptoide.pt.spotandshare.socket.interfaces.FileServerLifecycle;
import cm.aptoide.pt.spotandshare.socket.interfaces.ProgressAccumulator;
import cm.aptoide.pt.spotandshare.socket.util.MultiProgressAccumulator;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import static cm.aptoide.pt.spotandshare.socket.util.FileInfoUtils.computeTotalSize;

/**
 * Created by neuro on 27-01-2017.
 */
public class AptoideFileServerSocket<T> extends AptoideServerSocket {

  private final List<FileInfo> fileInfos;
  private boolean startedSending = false;

  private T fileDescriptor;
  private FileServerLifecycle<T> fileServerLifecycle;
  private ProgressAccumulator progressAccumulator;

  public AptoideFileServerSocket(int port, List<FileInfo> fileInfos, int timeout) {
    super(port, timeout);
    this.fileInfos = fileInfos;
  }

  public AptoideFileServerSocket(int bufferSize, int port, List<FileInfo> fileInfos, int timeout) {
    super(bufferSize, port, timeout);
    this.fileInfos = fileInfos;
  }

  @Override protected void onNewClient(Socket socket) {

    if (!startedSending && fileServerLifecycle != null) {
      fileServerLifecycle.onStartSending(fileDescriptor);
      startedSending = true;
    }

    if (progressAccumulator == null) {
      progressAccumulator =
          new MultiProgressAccumulatorServer(computeTotalSize(fileInfos), fileServerLifecycle,
              fileDescriptor);
    } else {
      progressAccumulator.accumulate(computeTotalSize(fileInfos));
    }

    InputStream in = null;

    try {
      for (String filePath : getFilePaths()) {
        System.out.println(Thread.currentThread().getId() + ": Started sending " + filePath);
        in = new FileInputStream(filePath);
        copy(in, socket.getOutputStream(), progressAccumulator);
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

  private List<String> getFilePaths() {
    List<String> filePaths = new LinkedList<>();

    for (FileInfo fileInfo : fileInfos) {
      filePaths.add(fileInfo.getFilePath());
    }

    return filePaths;
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
    this.onError = fileServerLifecycle;

    return this;
  }

  public class MultiProgressAccumulatorServer extends MultiProgressAccumulator<T> {

    private final FileServerLifecycle<T> fileServerLifecycle;

    public MultiProgressAccumulatorServer(long totalProgress,
        FileServerLifecycle<T> fileServerLifecycle, T androidAppInfo) {
      super(totalProgress, fileServerLifecycle, androidAppInfo);
      this.fileServerLifecycle = fileServerLifecycle;
    }

    @Override public void onProgressChanged(float progress) {
      super.onProgressChanged(progress);

      if (progress == 1) {
        if (fileServerLifecycle != null) {
          fileServerLifecycle.onFinishSending(t);
        }
      }
      System.out.println("Filipe: " + progress + ", " + (progress > 0.9999999999999999999999999));
    }
  }
}
