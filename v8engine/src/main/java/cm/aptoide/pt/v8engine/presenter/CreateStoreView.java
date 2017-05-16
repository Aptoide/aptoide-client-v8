package cm.aptoide.pt.v8engine.presenter;

import rx.Observable;

public interface CreateStoreView extends View {
  Observable<Void> selectStoreImageClick();

  Observable<Void> createStoreClick();

  Observable<Void> skipToHomeClick();
}
