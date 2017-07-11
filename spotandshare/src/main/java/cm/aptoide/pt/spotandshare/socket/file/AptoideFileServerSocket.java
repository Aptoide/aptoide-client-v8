package cm.aptoide.pt.spotandshare.socket.file;

import cm.aptoide.pt.spotandshare.socket.AptoideServerSocket;
import cm.aptoide.pt.spotandshare.socket.entities.FileInfo;
import cm.aptoide.pt.spotandshare.socket.interfaces.ProgressAccumulator;
import cm.aptoide.pt.spotandshare.socket.interfaces.TransferLifecycle;
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
  private TransferLifecycle<T> TransferLifecycle;
  private ProgressAccumulator progressAccumulator;

  public AptoideFileServerSocket(int port, List<FileInfo> fileInfos, int serverSocketTimeout,
      int timeout) {
    super(port, serverSocketTimeout, timeout);
    this.fileInfos = fileInfos;
  }

  public AptoideFileServerSocket(int bufferSize, int port, List<FileInfo> fileInfos,
      int serverSocketTimeout, int timeout) {
    super(bufferSize, port, serverSocketTimeout, timeout);
    this.fileInfos = fileInfos;
  }

  @Override protected void onNewClient(Socket socket) {

    if (!startedSending && TransferLifecycle != null) {
      TransferLifecycle.onStartTransfer(fileDescriptor);
      startedSending = true;
    }

    if (progressAccumulator == null) {
      progressAccumulator =
          new MultiProgressAccumulatorServer(computeTotalSize(fileInfos), TransferLifecycle,
              fileDescriptor);
    } else {
      progressAccumulator.accumulate(computeTotalSize(fileInfos));
    }

    InputStream in = null;

    try {
      for (String filePath : getFilePaths()) {
        System.out.println(Thread.currentThread()
            .getId() + ": Started sending " + filePath);
        in = new FileInputStream(filePath);
        copy(in, socket.getOutputStream(), progressAccumulator);
        System.out.println(Thread.currentThread()
            .getId() + ": Finished sending " + filePath);
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

  public AptoideFileServerSocket<T> setTransferLifecycle(T fileDescriptor,
      TransferLifecycle<T> TransferLifecycle) {

    if (fileDescriptor == null) {
      throw new IllegalArgumentException("fileDescriptor cannot be null!");
    }

    if (TransferLifecycle == null) {
      throw new IllegalArgumentException("TransferLifecycle cannot be null!");
    }

    this.fileDescriptor = fileDescriptor;
    this.TransferLifecycle = TransferLifecycle;
    this.onError = TransferLifecycle;

    return this;
  }

  public class MultiProgressAccumulatorServer extends MultiProgressAccumulator<T> {

    private final TransferLifecycle<T> TransferLifecycle;

    public MultiProgressAccumulatorServer(long totalProgress,
        TransferLifecycle<T> TransferLifecycle, T androidAppInfo) {
      super(totalProgress, TransferLifecycle, androidAppInfo);
      this.TransferLifecycle = TransferLifecycle;
    }

    @Override public void onProgressChanged(float progress) {
      super.onProgressChanged(progress);

      if (progress == 1) {
        if (TransferLifecycle != null) {
          TransferLifecycle.onFinishTransfer(t);
        }
      }
      System.out.println("Filipe: " + progress + ", " + (progress > 0.9999999999999999999999999));
    }
  }
}
