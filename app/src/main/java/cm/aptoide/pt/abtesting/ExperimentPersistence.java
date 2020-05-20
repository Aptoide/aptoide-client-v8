package cm.aptoide.pt.abtesting;

import rx.Completable;
import rx.Single;

/**
 * Created by franciscocalado on 20/06/18.
 */

public interface ExperimentPersistence {

  Completable save(String experimentName, Experiment experiment);

  Single<ExperimentModel> get(String identifier);
}
