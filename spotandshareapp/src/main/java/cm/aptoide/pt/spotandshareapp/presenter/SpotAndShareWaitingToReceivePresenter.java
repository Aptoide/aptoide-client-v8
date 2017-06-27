package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import android.util.Log;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareWaitingToReceiveView;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareWaitingToReceivePresenter implements Presenter {

  private SpotAndShareWaitingToReceiveView view;

  public SpotAndShareWaitingToReceivePresenter(SpotAndShareWaitingToReceiveView view) {
    this.view = view;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(created -> refreshSearch().compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> err.printStackTrace());
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private Observable<Void> refreshSearch() {
    return view.startSearch()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(selection -> {
          //// TODO: 12-06-2017 filipe call spot&share lib
          Log.i(getClass().getName(), "refreshing ... ");

          view.openSpotandShareTransferRecordFragment();
        });
  }
}
