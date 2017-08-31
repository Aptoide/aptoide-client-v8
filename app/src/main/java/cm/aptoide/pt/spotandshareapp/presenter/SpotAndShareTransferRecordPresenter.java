package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.SpotAndShareInstallManager;
import cm.aptoide.pt.spotandshareapp.SpotAndShareTransferRecordManager;
import cm.aptoide.pt.spotandshareapp.TransferAppModel;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareTransferRecordView;
import java.util.ArrayList;
import java.util.LinkedList;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareTransferRecordPresenter implements Presenter {

  private final SpotAndShareTransferRecordView view;
  private SpotAndShare spotAndShare;
  private SpotAndShareTransferRecordManager transferRecordManager;
  private SpotAndShareInstallManager spotAndShareInstallManager;
  private Subscription friendsObserverSubscription;

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
        .flatMap(created -> view.backButtonEvent())
        .doOnNext(click -> view.back())
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
        .flatMap(created -> view.listenBottomSheetHeaderClicks())
        .doOnNext(__ -> view.pressedBottomSheetHeader())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> spotAndShare.observeAmountOfFriends())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(friendsNumber -> view.updateFriendsNumber(friendsNumber))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.clickedFriendsInformationButton())
        .doOnNext(__ -> startListeningToFriendsChanges())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }

  private void stopListeningToFriendsChanges() {
    if (this.friendsObserverSubscription != null
        && !this.friendsObserverSubscription.isUnsubscribed()) {
      this.friendsObserverSubscription.unsubscribe();
      view.clearMenu();
    }
  }

  private void listenToFriendsMenuDismiss() {
    view.friendsMenuDismiss()
        .doOnNext(__ -> stopListeningToFriendsChanges())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }

  private void startListeningToFriendsChanges() {
    this.friendsObserverSubscription = spotAndShare.observeFriends()
        .doOnNext(friendsList -> view.showFriendsOnMenu((new ArrayList<>(friendsList))))
        .doOnNext(__ -> listenToFriendsMenuDismiss())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
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
