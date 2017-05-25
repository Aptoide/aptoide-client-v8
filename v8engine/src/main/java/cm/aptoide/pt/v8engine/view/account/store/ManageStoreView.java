package cm.aptoide.pt.v8engine.view.account.store;

import cm.aptoide.pt.v8engine.presenter.View;
import rx.Observable;

public interface ManageStoreView extends View {
  Observable<Void> selectStoreImageClick();

  Observable<Void> createStoreClick();

  Observable<Void> skipToHomeClick();
}
