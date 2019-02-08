package cm.aptoide.pt.abtesting;

import rx.Observable;

public interface AbTestRepository {

  Observable<Experiment> getExperiment(String identifier);

  Observable<Boolean> recordImpression(String identifier);

  Observable<Boolean> recordAction(String identifier);

  Observable<Void> cacheExperiment(ExperimentModel experiment, String experimentName);
}

