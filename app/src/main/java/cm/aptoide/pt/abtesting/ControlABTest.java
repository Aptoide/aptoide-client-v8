package cm.aptoide.pt.abtesting;

import cm.aptoide.pt.logger.Logger;
import com.seatgeek.sixpack.ConvertedExperiment;
import com.seatgeek.sixpack.ParticipatingExperiment;
import rx.Observable;

public class ControlABTest<T> implements ABTest<T> {

  private String name;
  private T control;

  public ControlABTest(String name, T control) {
    this.name = name;
    this.control = control;
  }

  @Override public String getName() {
    return name;
  }

  @Override public Observable<ParticipatingExperiment> participate() {
    Logger.d("ControlABTest",
        "AB test manager not initialized. Participate called in control AB Test: " + name);
    return null;
  }

  @Override public Observable<ConvertedExperiment> convert() {
    Logger.d("ControlABTest",
        "AB test manager not initialized. Convert called in control AB Test: " + name);
    return null;
  }

  @Override public T alternative() {
    return control;
  }

  @Override public Observable<Boolean> prefetch() {
    Logger.d("ControlABTest",
        "AB test manager not initialized. Prefetch called in control AB Test: " + name);
    return null;
  }
}
