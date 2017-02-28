package cm.aptoide.pt.shareapps.socket.util;

import cm.aptoide.pt.shareapps.socket.interfaces.ProgressAccumulator;
import cm.aptoide.pt.shareapps.socket.interfaces.ProgressCallback;

/**
 * Created by neuro on 21-02-2017.
 */

public class MultiProgressAccumulator implements ProgressAccumulator {

  private final long totalProgress;
  private final ProgressCallback progressCallback;

  private long addedProgress;

  public MultiProgressAccumulator(long totalProgress, ProgressCallback progressCallback) {
    this.totalProgress = totalProgress;
    this.progressCallback = progressCallback;
  }

  @Override public void addProgress(long progress) {
    addedProgress += progress;
    progressCallback.onProgressChanged(1.0f * addedProgress / totalProgress);
  }
}
