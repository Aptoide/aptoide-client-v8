package cm.aptoide.accountmanager;

import rx.Completable;
import rx.Observable;

public interface AdultContent {
  Observable<Boolean> pinRequired();

  Completable requirePin(int pin);

  Completable removePin(int pin);

  Completable enable(boolean isLogged);

  Completable disable(boolean isLogged);

  Observable<Boolean> enabled();

  Completable enable(int pin);
}
