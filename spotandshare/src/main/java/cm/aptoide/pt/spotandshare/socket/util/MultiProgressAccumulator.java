package cm.aptoide.pt.spotandshare.socket.util;

import cm.aptoide.pt.spotandshare.socket.interfaces.ProgressAccumulator;
import cm.aptoide.pt.spotandshare.socket.interfaces.ProgressCallback;

/**
 * Created by neuro on 21-02-2017.
 */

public class MultiProgressAccumulator<T> implements ProgressAccumulator {

  private final ProgressCallback<T> progressCallback;
  private final T t;
  private long totalProgress;
  private long addedProgress;

  public MultiProgressAccumulator(long totalProgress, ProgressCallback<T> progressCallback, T t) {
    this.totalProgress = totalProgress;
    this.progressCallback = progressCallback;
    this.t = t;
  }

  @Override public void addProgress(long progress) {
    addedProgress += progress;
    progressCallback.onProgressChanged(t, 1.0f * addedProgress / totalProgress);
  }

  @Override public void accumulate(long progressToAdd) {
    totalProgress += progressToAdd;
  }
}
