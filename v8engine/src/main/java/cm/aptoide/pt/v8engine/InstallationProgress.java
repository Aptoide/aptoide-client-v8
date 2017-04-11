package cm.aptoide.pt.v8engine;

/**
 * Created by trinkes on 10/04/2017.
 */

public class InstallationProgress {
  private int progress;
  private InstallationStatus state;
  private boolean isIndeterminate;
  private int speed;
  public InstallationProgress(int progress, InstallationStatus state, boolean isIndeterminate,
      int speed) {
    this.progress = progress;
    this.state = state;
    this.isIndeterminate = isIndeterminate;
    this.speed = speed;
  }

  public int getProgress() {
    return progress;
  }

  public InstallationStatus getState() {
    return state;
  }

  public boolean isIndeterminate() {
    return isIndeterminate;
  }

  public int getSpeed() {
    return speed;
  }

  public enum InstallationStatus {
    INSTALLING, INSTALLED, UNINSTALLED, FAILED
  }
}
