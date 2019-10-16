package cm.aptoide.pt.app.view;

import cm.aptoide.pt.presenter.View;
import rx.Observable;

/**
 * Created by D01 on 30/07/2018.
 */

public interface AppCoinsInfoView extends View {

  Observable<Void> cardViewClick();

  Observable<Void> catappultButtonClick();

  Observable<Void> installButtonClick();

  Observable<Void> appCoinsWalletLinkClick();

  void openApp(String packageName);

  void setButtonText(boolean installState);

  void startCatappultDevWebView();
}
