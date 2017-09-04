package cm.aptoide.pt.abtesting;

import com.seatgeek.sixpack.ConvertedExperiment;
import com.seatgeek.sixpack.ParticipatingExperiment;
import rx.Observable;

/**
 * Created by marcelobenites on 6/9/16.
 */
public interface ABTest<T> {
  String getName();

  Observable<ParticipatingExperiment> participate();

  Observable<ConvertedExperiment> convert();

  T alternative();

  Observable<Boolean> prefetch();
}
