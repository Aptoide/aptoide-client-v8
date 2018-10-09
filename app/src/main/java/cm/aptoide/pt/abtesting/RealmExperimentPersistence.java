package cm.aptoide.pt.abtesting;

import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.realm.RealmExperiment;
import com.fasterxml.jackson.core.JsonProcessingException;
import rx.Observable;

/**
 * Created by franciscocalado on 20/06/18.
 */

public class RealmExperimentPersistence implements ExperimentPersistence {

  private final Database database;
  private final RealmExperimentMapper mapper;

  public RealmExperimentPersistence(Database database, RealmExperimentMapper mapper) {
    this.database = database;
    this.mapper = mapper;
  }

  @Override public void save(String experimentName, Experiment experiment) {
    try {
      database.insert(mapper.map(experimentName, experiment));
    } catch (JsonProcessingException e) {
    }
  }

  @Override public Observable<ExperimentModel> get(String identifier) {
    return database.get(RealmExperiment.class, RealmExperiment.PRIMARY_KEY_NAME,
        identifier)
        .map(experiment -> {
          if (experiment == null) {
            return new ExperimentModel(new Experiment(), true);
          } else {
            return new ExperimentModel(mapper.map(experiment), false);
          }
        });
  }
}
