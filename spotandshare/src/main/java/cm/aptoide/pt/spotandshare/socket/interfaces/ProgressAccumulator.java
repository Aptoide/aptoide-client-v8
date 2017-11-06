package cm.aptoide.pt.spotandshare.socket.interfaces;

/**
 * Created by neuro on 21-02-2017.
 */

public interface ProgressAccumulator {

  void addProgress(long progress);

  void accumulate(long progress);

  void onProgressChanged(float progress);
}
