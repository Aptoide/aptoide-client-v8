package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import cm.aptoide.pt.spotandshareapp.view.MainActivityView;
import cm.aptoide.pt.spotandshareapp.view.View;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 07-06-2017.
 */

public class MainActivityPresenter implements Presenter {
  private MainActivityView view;

  public MainActivityPresenter(MainActivityView view) {
    this.view = view;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(created -> view.openSpotAndShareStart())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
