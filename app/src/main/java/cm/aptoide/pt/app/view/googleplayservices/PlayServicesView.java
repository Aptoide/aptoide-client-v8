package cm.aptoide.pt.app.view.googleplayservices;

import cm.aptoide.pt.presenter.View;
import rx.Observable;
import rx.subjects.PublishSubject;

public interface PlayServicesView extends View {
  Observable<Void> clickLater();

  Observable<Void> clickInstall();

  void dismissView();

  void setResumeInstallInstallSubject(PublishSubject<Boolean> resumeInstallInstallSubject);

}
