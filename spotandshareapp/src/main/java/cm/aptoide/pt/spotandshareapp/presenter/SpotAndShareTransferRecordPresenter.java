package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareandroid.transfermanager.Transfer;
import cm.aptoide.pt.spotandshareapp.SpotAndShareInstallManager;
import cm.aptoide.pt.spotandshareapp.SpotAndShareTransferRecordManager;
import cm.aptoide.pt.spotandshareapp.TransferAppModel;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareTransferRecordView;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareTransferRecordPresenter implements Presenter {

  private final SpotAndShareTransferRecordView view;
  private SpotAndShare spotAndShare;
  private SpotAndShareTransferRecordManager transferRecordManager;
  private SpotAndShareInstallManager spotAndShareInstallManager;

  public SpotAndShareTransferRecordPresenter(SpotAndShareTransferRecordView view,
      SpotAndShare spotAndShare, SpotAndShareTransferRecordManager transferRecordManager,
      SpotAndShareInstallManager spotAndShareInstallManager) {
    this.view = view;
    this.spotAndShare = spotAndShare;
    this.transferRecordManager = transferRecordManager;
    this.spotAndShareInstallManager = spotAndShareInstallManager;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> spotAndShare.observeTransfers())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(transfers -> updateTransferRecord(transfers))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> err.printStackTrace());

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.acceptApp())
        .doOnNext(transferAppModel -> acceptedApp(transferAppModel))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.installApp())
        .doOnNext(transferAppModel -> installApp(transferAppModel))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.shareApp())
        .doOnNext(__ -> view.openAppSelectionFragment(false))
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

  private void updateTransferRecord(List<Transfer> transferList) {
    view.updateReceivedAppsList(transferRecordManager.getTransferAppModelList(transferList));
  }

  private void leaveGroup() {
    spotAndShare.leaveGroup(view::navigateBack, err -> view.onLeaveGroupError());
  }

  private void acceptedApp(TransferAppModel transferAppModel) {
    System.out.println("accepted : " + transferAppModel.getAppName());
    transferRecordManager.acceptApp(transferAppModel);
  }

  private void installApp(TransferAppModel transferAppModel) {
    System.out.println("install : " + transferAppModel.getAppName());
    spotAndShareInstallManager.installAppAsync(transferAppModel.getFilePath(),
        transferAppModel.getPackageName());
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
