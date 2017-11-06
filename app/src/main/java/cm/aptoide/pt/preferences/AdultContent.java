package cm.aptoide.pt.preferences;

import rx.Completable;
import rx.Observable;

public interface AdultContent {
  Observable<Boolean> pinRequired();

  Completable requirePin(int pin);

  Completable removePin(int pin);

  Completable enable();

  Completable disable();

  Observable<Boolean> enabled();

  Completable enable(int pin);
}
