package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import rx.Observable;

public interface MyAccountView extends View {
  Observable<Void> signOutClick();

  Observable<Void> moreNotificationsClick();

  Observable<Void> editStoreClick();

  void navigateToHome();

  Bundle inboxFragmentBundleCreator(boolean showToolbar);
}
