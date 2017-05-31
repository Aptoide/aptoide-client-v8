package cm.aptoide.pt.v8engine.view.account.user;

import cm.aptoide.pt.v8engine.presenter.View;
import rx.Observable;

public interface ManageUserView extends View {
  Observable<Void> createUserButtonClick();

  Observable<Void> selectUserImageClick();

  Observable<Void> cancelButtonClick();
}
