package cm.aptoide.pt.abtesting;

import rx.Completable;
import rx.Observable;

/**
 * Created by franciscocalado on 20/06/18.
 */

public interface ExperimentPersistence {

  Completable save(String experimentName, Experiment experiment);

  Observable<ExperimentModel> get(ABTestManager.ExperimentType experimentType);
}
