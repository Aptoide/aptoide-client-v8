package cm.aptoide.pt.abtesting;

/**
 * Created by franciscocalado on 15/06/18.
 */

public class ABTestManager {
  private ABTestCenter abTestCenter;

  public ABTestManager(ABTestCenter abTestCenter) {
    this.abTestCenter = abTestCenter;
  }

  public void getExperiment(Experiments experiment, String id) {
  }

  public void recordImpression(Experiments experiment, String id) {
  }

  public void recordAction(Experiments experiment, String action, String id) {
  }

  public enum Experiments {
    SHARE_DIALOG,
  }

}
