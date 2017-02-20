package cm.aptoide.pt.shareapps.socket.interfaces;

/**
 * Created by neuro on 14-02-2017.
 */
public interface FileClientLifecycle<T> {

  void onStartReceiving(T t);

  void onFinishReceiving(T t);
}
