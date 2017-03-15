package cm.aptoide.pt.spotandshare.socket.interfaces;

/**
 * Created by neuro on 23-02-2017.
 */

public interface OnError<T extends Exception> {

  void onError(T e);
}
