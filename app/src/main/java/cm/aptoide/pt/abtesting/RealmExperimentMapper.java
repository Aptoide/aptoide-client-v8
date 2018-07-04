package cm.aptoide.pt.abtesting;

import cm.aptoide.pt.database.realm.RealmExperiment;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Created by franciscocalado on 20/06/18.
 */

public class RealmExperimentMapper {

  public RealmExperimentMapper() {
  }

  public RealmExperiment map(String experimentName, Experiment experiment)
      throws JsonProcessingException {
    return new RealmExperiment(experimentName, experiment.getRequestTime(),
        experiment.getAssignment(), experiment.getPayload(), experiment.isPartOfExperiment(),
        experiment.isExperimentOver());
  }

  public Experiment map(RealmExperiment experiment) {
    return new Experiment(experiment.getRequestTime(), experiment.getAssignment(),
        experiment.getPayload(), experiment.isPartOfExperiment(), experiment.isExperimentOver());
  }
}
