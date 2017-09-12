package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.view.ShareAptoideView;
import java.util.concurrent.TimeUnit;
import rx.Completable;

/**
 * Created by filipe on 12-09-2017.
 */

public class ShareAptoidePresenter implements Presenter {

  private ShareAptoideView view;
  private SpotAndShare spotAndShare;

  public ShareAptoidePresenter(ShareAptoideView view, SpotAndShare spotAndShare) {
    this.view = view;
    this.spotAndShare = spotAndShare;
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
  }

  private Completable createGroup() {
    return spotAndShare.createGroup(success -> {
    }, view::onCreateGroupError, null);
  }

  private void leaveGroup() {
    spotAndShare.leaveGroup(error -> view.onLeaveGroupError());
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
