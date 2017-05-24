package cm.aptoide.pt.v8engine.presenter;

import rx.Observable;

public interface CreateUserView extends View {
  Observable<Void> createUserButtonClick();

  Observable<Void> selectUserImageClick();

  Observable<Void> cancelButtonClick();
}
