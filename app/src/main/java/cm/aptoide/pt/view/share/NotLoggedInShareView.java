package cm.aptoide.pt.view.share;

import cm.aptoide.pt.presenter.SocialLoginView;
import rx.Observable;

/**
 * Created by pedroribeiro on 30/08/17.
 */

public interface NotLoggedInShareView extends SocialLoginView {

  void initializeFacebookCallback();

  Observable<Void> closeClick();

  void closeFragment();

  Observable<Void> dontShowAgainClick();
}
