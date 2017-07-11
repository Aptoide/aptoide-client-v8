package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareWaitingToReceiveView;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareWaitingToReceivePresenter implements Presenter {

  private SpotAndShareWaitingToReceiveView view;
  private SpotAndShare spotAndShare;

  public SpotAndShareWaitingToReceivePresenter(SpotAndShareWaitingToReceiveView view,
      SpotAndShare spotAndShare) {
    this.view = view;
    this.spotAndShare = spotAndShare;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .doOnNext(resumed -> joinGroup())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> err.printStackTrace());
  }

  private void joinGroup() {
    spotAndShare.joinGroup(andShareSender -> {
      // TODO: 10-07-2017 filipe
      view.openSpotandShareTransferRecordFragment();
    }, view::onJoinGroupError);
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
