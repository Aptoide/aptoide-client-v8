package cm.aptoide.pt.v8engine.view.account.store;

import rx.Observable;

public interface UseCase<R> {
  Observable<R> execute();
}
