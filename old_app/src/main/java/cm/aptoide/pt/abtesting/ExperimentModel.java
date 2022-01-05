package cm.aptoide.pt.abtesting;

/**
 * Created by franciscocalado on 19/06/18.
 */

public class ExperimentModel {
  private final Experiment experiment;
  private final boolean hasError;

  public ExperimentModel(Experiment experiment, boolean hasError) {
    this.experiment = experiment;
    this.hasError = hasError;
  }

  public Experiment getExperiment() {
    return experiment;
  }

  public boolean hasError() {
    return hasError;
  }
}
