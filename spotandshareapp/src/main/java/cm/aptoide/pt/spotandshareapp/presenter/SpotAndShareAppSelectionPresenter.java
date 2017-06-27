package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import cm.aptoide.pt.spotandshareapp.InstalledRepositoryDummy;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareAppSelectionView;
import cm.aptoide.pt.spotandshareapp.view.View;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareAppSelectionPresenter implements Presenter {
  private final SpotAndShareAppSelectionView view;
  private InstalledRepositoryDummy installedRepositoryDummy;

  public SpotAndShareAppSelectionPresenter(SpotAndShareAppSelectionView view,
      InstalledRepositoryDummy installedRepositoryDummy) {
    this.view = view;
    this.installedRepositoryDummy = installedRepositoryDummy;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(created -> view.setupRecyclerView(installedRepositoryDummy.getInstalledApps()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
