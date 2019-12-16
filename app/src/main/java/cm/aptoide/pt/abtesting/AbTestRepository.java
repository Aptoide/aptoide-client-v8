package cm.aptoide.pt.abtesting;

import rx.Observable;

public interface AbTestRepository {

  Observable<Experiment> getExperiment(String identifier, BaseExperiment.ExperimentType type);

  Observable<Boolean> recordImpression(String identifier, BaseExperiment.ExperimentType type);

  Observable<Boolean> recordAction(String identifier, BaseExperiment.ExperimentType type);

  Observable<Boolean> recordAction(String identifier, int position,
      BaseExperiment.ExperimentType type);

  Observable<Void> cacheExperiment(ExperimentModel experiment, String experimentName);

  Observable<String> getExperimentId(String id);
}

