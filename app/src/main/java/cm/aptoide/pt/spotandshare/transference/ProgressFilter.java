package cm.aptoide.pt.spotandshare.transference;

/**
 * Created by neuro on 06-03-2017.
 */

public class ProgressFilter {

  private final int split;
  private float lastShownProgress = -1;

  public ProgressFilter(int split) {
    this.split = split;
  }

  public boolean shouldUpdate(float progress) {

    int innerProgress = (Math.round(progress * 100));

    boolean shouldUpdate;

    if (shouldUpdate = innerProgress % (100 / split) == 0 && lastShownProgress != innerProgress) {
      lastShownProgress = innerProgress;
    }

    return shouldUpdate;
  }
}
