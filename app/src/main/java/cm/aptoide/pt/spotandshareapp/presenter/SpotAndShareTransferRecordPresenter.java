package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshare.socket.entities.Friend;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.DrawableBitmapMapper;
import cm.aptoide.pt.spotandshareapp.SpotAndShareInstallManager;
import cm.aptoide.pt.spotandshareapp.SpotAndShareTransferRecordManager;
import cm.aptoide.pt.spotandshareapp.TransferAppModel;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareTransferRecordView;
import java.util.Collection;
import java.util.LinkedList;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareTransferRecordPresenter implements Presenter {

  private final SpotAndShareTransferRecordView view;
  private SpotAndShare spotAndShare;
  private SpotAndShareTransferRecordManager transferRecordManager;
  private SpotAndShareInstallManager spotAndShareInstallManager;
  private final DrawableBitmapMapper drawableBitmapMapper;

  public SpotAndShareTransferRecordPresenter(SpotAndShareTransferRecordView view,
      SpotAndShare spotAndShare, SpotAndShareTransferRecordManager transferRecordManager,
      SpotAndShareInstallManager spotAndShareInstallManager,
      DrawableBitmapMapper drawableBitmapMapper) {
    this.view = view;
    this.spotAndShare = spotAndShare;
    this.transferRecordManager = transferRecordManager;
    this.spotAndShareInstallManager = spotAndShareInstallManager;
    this.drawableBitmapMapper = drawableBitmapMapper;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .switchMap(created -> spotAndShare.observeTransfers()
            .map(transfers -> new LinkedList<>(transfers))
            .map(transfers -> transferRecordManager.getTransferAppModelList(transfers))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(transferAppModels -> view.updateReceivedAppsList(transferAppModels)))
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
        .doOnNext(transferAppModel -> spotAndShareInstallManager.listenToAppInstalation(
            transferAppModel.getPackageName()))
        .doOnNext(transferAppModel -> view.updateTransferInstallStatus(transferAppModel))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.clickedConnectedFriends())
        .doOnNext(r -> view.openConnectedFriendsFragment())
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
        .doOnNext(__ -> view.navigateBack())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> spotAndShare.observeFriends())
        .doOnNext(friendsList -> updateFriendsList(friendsList))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }

  private void updateFriendsList(Collection<Friend> friendsList) {
    if (friendsList.size() == 1) {
      view.updateFriendsAvatar(drawableBitmapMapper.convertBitmapToDrawable(
          ((Friend) friendsList.toArray()[0]).getAvatar()));
    } else {
      view.updateFriendsNumber(friendsList.size());
    }
  }

  private void leaveGroup() {
    spotAndShare.leaveGroup(err -> view.onLeaveGroupError());
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
