package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.ShareApkSandbox;
import cm.aptoide.pt.spotandshareapp.view.ShareAptoideView;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import rx.Completable;

/**
 * Created by filipe on 12-09-2017.
 */

public class ShareAptoidePresenter implements Presenter {

  private ShareAptoideView view;
  private SpotAndShare spotAndShare;
  private ShareApkSandbox shareApkSandbox;

  public ShareAptoidePresenter(ShareAptoideView view, SpotAndShare spotAndShare,
      ShareApkSandbox shareApkSandbox) {
    this.view = view;
    this.spotAndShare = spotAndShare;
    this.shareApkSandbox = shareApkSandbox;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(created -> createGroup().timeout(10, TimeUnit.SECONDS)
            .toSingleDefault(2))
        .doOnError(throwable -> view.onCreateGroupError(throwable))
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
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.DESTROY))
        .doOnNext(__ -> shareApkSandbox.stop())
        .subscribe(__ -> {
        }, error -> error.printStackTrace());
  }

  private Completable createGroup() {
    return spotAndShare.createOpenGroup(success -> {
      try {
        shareApkSandbox.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  private void leaveGroup() {
    spotAndShare.leaveGroup(error -> view.onLeaveGroupError());
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
