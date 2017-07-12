package cm.aptoide.pt.spotandshare.socket.file;

import cm.aptoide.pt.spotandshare.socket.AptoideClientSocket;
import cm.aptoide.pt.spotandshare.socket.entities.FileInfo;
import cm.aptoide.pt.spotandshare.socket.interfaces.ProgressAccumulator;
import cm.aptoide.pt.spotandshare.socket.interfaces.TransferLifecycle;
import cm.aptoide.pt.spotandshare.socket.util.MultiProgressAccumulator;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import static cm.aptoide.pt.spotandshare.socket.util.FileInfoUtils.computeTotalSize;

/**
 * Created by neuro on 27-01-2017.
 */

public class AptoideFileClientSocket<T> extends AptoideClientSocket {

  private final List<FileInfo> fileInfos;

  private T fileDescriptor;
  private TransferLifecycle<T> TransferLifecycle;

  public AptoideFileClientSocket(String host, int port, List<FileInfo> fileInfos, int timeout) {
    super(host, port, timeout);
    this.fileInfos = fileInfos;
    // TODO: 24-03-2017 neuro fix this sheet
    onError = TransferLifecycle;
  }

  public AptoideFileClientSocket(int bufferSize, String host, int port, List<FileInfo> fileInfos,
      int timeout) {
    super(bufferSize, host, port, timeout);
    this.fileInfos = fileInfos;
    // TODO: 24-03-2017 neuro fix this sheet
    onError = TransferLifecycle;
  }

  @Override protected void onConnected(Socket socket) throws IOException {

    if (TransferLifecycle != null) {
      TransferLifecycle.onStartTransfer(fileDescriptor);
    }

    ProgressAccumulator progressAccumulator =
        new MultiProgressAccumulatorClient(computeTotalSize(fileInfos), TransferLifecycle,
            fileDescriptor);

    for (FileInfo fileInfo : fileInfos) {
      System.out.println(Thread.currentThread()
          .getId() + ": Start receiving " + fileInfo);
      OutputStream out = new FileOutputStream(fileInfo.getFilePath());

      copy(socket.getInputStream(), out, fileInfo.getSize(), progressAccumulator);
      out.close();
    }
  }

  public AptoideFileClientSocket<T> setTransferLifecycle(T fileDescriptor,
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

  public class MultiProgressAccumulatorClient extends MultiProgressAccumulator<T> {

    private final TransferLifecycle<T> TransferLifecycle;

    public MultiProgressAccumulatorClient(long totalProgress,
        TransferLifecycle<T> TransferLifecycle, T androidAppInfo) {
      super(totalProgress, TransferLifecycle, androidAppInfo, 1000);
      this.TransferLifecycle = TransferLifecycle;
    }

    @Override public void onProgressChanged(float progress) {
      super.onProgressChanged(progress);

      if (progress == 1) {
        if (TransferLifecycle != null) {
          TransferLifecycle.onFinishTransfer(t);
        }
      }
    }
  }
}
