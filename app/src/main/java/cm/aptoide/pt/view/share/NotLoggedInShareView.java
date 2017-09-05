package cm.aptoide.pt.view.share;

import cm.aptoide.pt.presenter.View;
import rx.Observable;

/**
 * Created by pedroribeiro on 30/08/17.
 */

public interface NotLoggedInShareView extends View {

  Observable<Void> facebookButtonPress();

  void facebookLogin();

  void facebookInit();

  Observable<Void> closePress();

  void closeFragment();
}
