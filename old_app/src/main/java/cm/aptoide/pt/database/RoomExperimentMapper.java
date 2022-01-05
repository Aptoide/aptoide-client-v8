package cm.aptoide.pt.database;

import cm.aptoide.pt.abtesting.Experiment;
import cm.aptoide.pt.database.room.RoomExperiment;

public class RoomExperimentMapper {

  public RoomExperimentMapper() {
  }

  public RoomExperiment map(String experimentName, Experiment experiment) {
    return new RoomExperiment(experimentName, experiment.getRequestTime(),
        experiment.getAssignment(), experiment.getPayload(), experiment.isPartOfExperiment(),
        experiment.isExperimentOver());
  }

  public Experiment map(RoomExperiment experiment) {
    return new Experiment(experiment.getRequestTime(), experiment.getAssignment(),
        experiment.getPayload(), experiment.isPartOfExperiment(), experiment.isExperimentOver());
  }
}
