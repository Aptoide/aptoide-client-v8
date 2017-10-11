package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareAppSelectionView;

/**
 * Created by filipe on 28-07-2017.
 */

public class SpotAndShareAppSelectionPresenter implements Presenter {

  private final SpotAndShareAppSelectionView view;
  private final SpotAndShare spotAndShare;
  private final CrashReport crashReport;

  public SpotAndShareAppSelectionPresenter(SpotAndShareAppSelectionView view,
      SpotAndShare spotAndShare, CrashReport crashReport) {
    this.view = view;
    this.spotAndShare = spotAndShare;
    this.crashReport = crashReport;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.backButtonEvent())
        .doOnNext(click -> view.showExitWarning())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.exitEvent())
        .doOnNext(clicked -> leaveGroup())
        .doOnNext(__ -> view.navigateBack())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  private void leaveGroup() {
    spotAndShare.leaveGroup(err -> view.onLeaveGroupError());
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
