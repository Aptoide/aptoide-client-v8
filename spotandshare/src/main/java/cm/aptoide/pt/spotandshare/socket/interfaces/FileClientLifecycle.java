package cm.aptoide.pt.spotandshare.socket.interfaces;

import java.io.IOException;

/**
 * Created by neuro on 14-02-2017.
 */
public interface FileClientLifecycle<T> extends ProgressCallback<T>, OnError<IOException> {

  void onStartReceiving(T t);

  void onFinishReceiving(T t);
}
