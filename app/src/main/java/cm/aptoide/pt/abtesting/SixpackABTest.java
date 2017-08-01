/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 06/05/2016.
 */

package cm.aptoide.pt.abtesting;

import cm.aptoide.pt.logger.Logger;
import com.seatgeek.sixpack.ConvertedExperiment;
import com.seatgeek.sixpack.Experiment;
import com.seatgeek.sixpack.ParticipatingExperiment;
import com.seatgeek.sixpack.PrefetchedExperiment;
import rx.Observable;
import rx.schedulers.Schedulers;

public class SixpackABTest<T> implements ABTest<T> {

  private static final String TAG = SixpackABTest.class.getSimpleName();
  private final Experiment experiment;
  private final AlternativeParser<T> alternativeParser;
  private ParticipatingExperiment participatingExperiment;
  private PrefetchedExperiment prefetchedExperiment;

  public SixpackABTest(Experiment experiment, AlternativeParser<T> alternativeParser) {
    this.experiment = experiment;
    this.alternativeParser = alternativeParser;
  }

  @Override public int hashCode() {
    return getName().hashCode();
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ABTest<?> abTest = (ABTest<?>) o;

    return getName().equals(abTest.getName());
  }

  @Override public String getName() {
    return experiment.name;
  }

  @Override public Observable<ParticipatingExperiment> participate() {
    return Observable.defer(() -> Observable.just(isParticipating()))
        .subscribeOn(Schedulers.io())
        .map(participating -> {
          if (!participating) {
            participatingExperiment = experiment.participate();
          }
          return participatingExperiment;
        })
        .doOnNext(exp -> Logger.i(TAG,
            "Participating on test and the alternative is " + alternative().toString()));
  }

  @Override public Observable<ConvertedExperiment> convert() {
    return Observable.just(isParticipating())
        .subscribeOn(Schedulers.io())
        .filter(isParticipating -> isParticipating)
        .map(isParticipating -> participatingExperiment.convert())
        .doOnNext(success -> Logger.i(TAG, "convert Test: " + alternative().toString()));
  }

  @Override public T alternative() {
    if (experiment.hasForcedChoice()) {
      return alternativeParser.parse(experiment.forcedChoice.name);
    }
    if (isParticipating()) {
      return alternativeParser.parse(participatingExperiment.selectedAlternative.name);
    } else if (isPrefetched()) {
      return alternativeParser.parse(prefetchedExperiment.selectedAlternative.name);
    }
    return alternativeParser.parse(experiment.getControlAlternative().name);
  }

  @Override public Observable<Boolean> prefetch() {
    if (!isPrefetched()) {
      return Observable.fromCallable(() -> prefetchedExperiment = experiment.prefetch())
          .map(prefetched -> true);
    } else {
      return Observable.just(false);
    }
  }

  private boolean isParticipating() {
    return participatingExperiment != null;
  }

  private boolean isPrefetched() {
    return prefetchedExperiment != null;
  }
}