package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import cm.aptoide.pt.spotandshareapp.AppModel;
import cm.aptoide.pt.spotandshareapp.InstalledRepositoryDummy;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareAppSelectionView;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import java.util.LinkedList;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareAppSelectionPresenter implements Presenter {
  private final SpotAndShareAppSelectionView view;
  private InstalledRepositoryDummy installedRepositoryDummy;
  private List<AppModel> selectedApps;

  public SpotAndShareAppSelectionPresenter(SpotAndShareAppSelectionView view,
      InstalledRepositoryDummy installedRepositoryDummy) {
    this.view = view;
    this.installedRepositoryDummy = installedRepositoryDummy;
    selectedApps = new LinkedList<>();
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(created -> view.setupRecyclerView(installedRepositoryDummy.getInstalledApps()))
        .doOnNext(created -> setupAdapterListener())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }

  private void setupAdapterListener() {
    view.setupAppSelection(new SpotAndShareAppSelectionView.AppSelectionListener() {

      @Override public void onAppSelected(AppModel appModel) {
        if (selectedApps.contains(appModel)) {
          selectedApps.remove(appModel);
        } else {
          selectedApps.add(appModel);
        }
      }
    });
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
