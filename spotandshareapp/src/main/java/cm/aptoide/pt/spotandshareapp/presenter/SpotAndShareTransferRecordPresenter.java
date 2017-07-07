package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareTransferRecordView;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareTransferRecordPresenter implements Presenter {

  private final SpotAndShareTransferRecordView view;
  private SpotAndShare spotAndShare;

  public SpotAndShareTransferRecordPresenter(SpotAndShareTransferRecordView view,
      SpotAndShare spotAndShare) {
    this.view = view;
    this.spotAndShare = spotAndShare;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.acceptApp())
        .doOnNext(androidAppInfo -> acceptedApp(androidAppInfo))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.backButtonEvent())
        .doOnNext(click -> view.showExitWarning())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.exitEvent())
        .doOnNext(clicked -> leaveGroup())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }

  private void leaveGroup() {
    spotAndShare.leaveGroup(success -> view.navigateBack(), err -> view.onLeaveGroupError());
  }

  private void acceptedApp(AndroidAppInfo androidAppInfo) {
    //// TODO: 07-07-2017 filipe inform spot and share accepted app
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
