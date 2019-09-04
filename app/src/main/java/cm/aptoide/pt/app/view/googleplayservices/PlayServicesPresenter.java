package cm.aptoide.pt.app.view.googleplayservices;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import com.google.android.exoplayer2.util.Log;

public class PlayServicesPresenter implements Presenter {

  private PlayServicesView view;
  private CrashReport crashReport;
  private PlayServicesAppsProvider playServicesAppsProvider;
  private PlayServicesManager playServicesManager;

  public PlayServicesPresenter(PlayServicesView view, CrashReport crashReport,
      PlayServicesManager playServicesManager, PlayServicesAppsProvider playServicesAppsProvider) {
    this.view = view;
    this.crashReport = crashReport;
    this.playServicesManager = playServicesManager;
    this.playServicesAppsProvider = playServicesAppsProvider;
  }

  @Override public void present() {
    handleLaterClick();
    handleInstallClick();
  }

  private void handleLaterClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickLater())
        .doOnNext(__ -> view.dismissView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleInstallClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.clickInstall())
        .flatMap(__ -> playServicesAppsProvider.getPlayServicesApps()
            .first())
        .doOnError(e -> e.printStackTrace())
        .flatMapCompletable(apps -> playServicesManager.downloadApps(apps))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }
}
