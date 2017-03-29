package cm.aptoide.pt.spotandshare.socket.util;

import cm.aptoide.pt.spotandshare.socket.interfaces.ProgressAccumulator;
import cm.aptoide.pt.spotandshare.socket.interfaces.ProgressCallback;

/**
 * Created by neuro on 21-02-2017.
 */

public class MultiProgressAccumulator<T> implements ProgressAccumulator {

  protected final T t;
  private final ProgressCallback<T> progressCallback;
  private long totalProgress;
  private long addedProgress;

  public MultiProgressAccumulator(long totalProgress, ProgressCallback<T> progressCallback, T t) {
    this.totalProgress = totalProgress;
    this.progressCallback = progressCallback;
    this.t = t;
  }

  @Override public void addProgress(long progress) {
    addedProgress += progress;
    onProgressChanged(1.0f * addedProgress / totalProgress);
  }

  //@Override public void addProgress(long progress) {
  //  addedProgress += progress;
  //  System.out.println("Filipe PEdro: " + addedProgress + ", " + totalProgress);
  //  if (addedProgress != totalProgress) {
  //    onProgressChanged(1.0f * addedProgress / totalProgress);
  //  } else {
  //    onProgressChanged(1);
  //  }
  //}

  @Override public void accumulate(long progressToAdd) {
    totalProgress += progressToAdd;
  }

  @Override public void onProgressChanged(float progress) {
    progressCallback.onProgressChanged(t, progress);
  }
}
