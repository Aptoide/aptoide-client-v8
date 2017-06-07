package cm.aptoide.pt.v8engine.social.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.spotandshare.socket.Log;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.social.data.Article;
import cm.aptoide.pt.v8engine.social.data.SocialManager;
import cm.aptoide.pt.v8engine.social.view.TimelineView;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class TimelinePresenter implements Presenter {

  private final TimelineView view;
  private final SocialManager socialManager;

  public TimelinePresenter(@NonNull TimelineView cardsView, @NonNull SocialManager socialManager) {
    this.view = cardsView;
    this.socialManager = socialManager;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.showProgressIndicator())
        .flatMapSingle(created -> socialManager.getCards(20, 0))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cards -> showCardsAndHideProgress(cards))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(articles -> {
        }, throwable -> Log.d(this.getClass().getCanonicalName(), "ERROR LOADING CARDS"));

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.refreshes())
        .flatMapSingle(refresh -> socialManager.getCards(20, 0))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cards -> showCardsAndHideRefresh(cards))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(articles -> {
        }, throwable -> Log.d(this.getClass().getCanonicalName(), "ERROR REFRESHING CARDS"));
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private void showCardsAndHideProgress(List<Article> cards) {
    view.hideProgressIndicator();
    view.showCards(cards);
  }

  private void showCardsAndHideRefresh(List<Article> cards) {
    view.hideRefresh();
    view.showCards(cards);
  }
}
