package cm.aptoide.pt.spotandshare.socket.interfaces;

/**
 * Created by neuro on 21-02-2017.
 */

public interface ProgressCallback<T> {

  void onProgressChanged(T t, float progress);
}
