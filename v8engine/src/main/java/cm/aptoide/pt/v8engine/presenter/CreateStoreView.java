package cm.aptoide.pt.v8engine.presenter;

import rx.Observable;

public interface CreateStoreView extends View {
  Observable<Void> selectStoreImage();

  Observable<Void> createStore();

  Observable<Void> skip();
}
