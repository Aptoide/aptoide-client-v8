package cm.aptoide.pt.shareapps.socket.interfaces;

/**
 * Created by neuro on 14-02-2017.
 */
public interface FileServerLifecycle<T> extends ProgressCallback {

  void onStartSending(T t);

  void onFinishSending(T t);
}
