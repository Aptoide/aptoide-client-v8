package cm.aptoide.pt.shareapps.socket.interfaces;

import java.io.IOException;

/**
 * Created by neuro on 14-02-2017.
 */
public interface FileServerLifecycle<T> extends ProgressCallback, OnError<IOException> {

  void onStartSending(T t);

  void onFinishSending(T t);
}
