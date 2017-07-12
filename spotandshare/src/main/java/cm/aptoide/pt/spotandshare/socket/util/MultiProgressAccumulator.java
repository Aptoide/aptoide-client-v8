package cm.aptoide.pt.spotandshare.socket.util;

import cm.aptoide.pt.spotandshare.socket.interfaces.ProgressAccumulator;
import cm.aptoide.pt.spotandshare.socket.interfaces.ProgressCallback;

/**
 * Created by neuro on 21-02-2017.
 */

public class MultiProgressAccumulator<T> implements ProgressAccumulator {

  protected final T t;
  private final ProgressCallback<T> progressCallback;
  private final int interval;
  private long totalProgress;
  private long addedProgress;
  private long lastPublish;

  public MultiProgressAccumulator(long totalProgress, ProgressCallback<T> progressCallback, T t,
      int interval) {
    this.totalProgress = totalProgress;
    this.progressCallback = progressCallback;
    this.t = t;
    this.interval = interval;
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

  @Override public void addProgress(long progress) {
    addedProgress += progress;
    System.out.println("addProgress" + progress);
    onProgressChanged(1.0f * addedProgress / totalProgress);
  }

  @Override public void accumulate(long progressToAdd) {
    totalProgress += progressToAdd;
  }

  @Override public void onProgressChanged(float progress) {
    long currentTimeMillis = System.currentTimeMillis();

    if ((currentTimeMillis - lastPublish) >= interval || totalProgress == addedProgress) {
      progressCallback.onProgressChanged(t, progress);
      lastPublish = currentTimeMillis;
    }
  }
}
