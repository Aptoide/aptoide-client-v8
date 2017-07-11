package cm.aptoide.pt.spotandshare.socket.interfaces;

import java.io.IOException;

/**
 * Created by neuro on 14-02-2017.
 */
public interface TransferLifecycle<T> extends ProgressCallback<T>, OnError<IOException> {

  void onStartTransfer(T t);

  void onFinishTransfer(T t);

  @Override void onProgressChanged(T t, float progress);

  @Override void onError(IOException e);
}
