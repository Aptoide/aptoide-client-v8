package cm.aptoide.pt.app.view.googleplayservices;

import cm.aptoide.pt.presenter.View;
import rx.Observable;

public interface PlayServicesView extends View {
  Observable<Void> clickLater();

  Observable<Void> clickInstall();

  void dismissView();
}
