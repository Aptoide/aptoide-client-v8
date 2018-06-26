package cm.aptoide.pt.abtesting;

import rx.Observable;

/**
 * Created by franciscocalado on 20/06/18.
 */

public interface ExperimentPersistence {

  void save(String experimentName, Experiment experiment);

  Observable<ExperimentModel> get(ABTestManager.ExperimentType experimentType);
}
