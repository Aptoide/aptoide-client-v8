package cm.aptoide.pt.v8engine.social;

import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.spotandshare.socket.Log;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class TimelinePresenter implements Presenter {

  private final TimelineView view;
  private final SocialManager socialManager;

  @NonNull private CompositeSubscription subscriptions;

  public TimelinePresenter(@NonNull TimelineView cardsView, @NonNull SocialManager socialManager) {
    this.view = cardsView;
    this.socialManager = socialManager;
    this.subscriptions = new CompositeSubscription();
  }

  @Override public void present() {
    subscriptions.add(view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(created -> socialManager.getCards(20, 0))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cards -> view.showCards(cards))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(articles -> {
        }, throwable -> Log.d(this.getClass()
            .getCanonicalName(), "ERROR LOADING CARDS")));
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }
}
