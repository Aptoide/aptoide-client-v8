package cm.aptoide.pt.v8engine.social.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.spotandshare.socket.Log;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.social.data.Card;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
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
  private final CrashReport crashReport;

  public TimelinePresenter(@NonNull TimelineView cardsView, @NonNull SocialManager socialManager,
      CrashReport crashReport) {
    this.view = cardsView;
    this.socialManager = socialManager;
    this.crashReport = crashReport;
  }

  @Override public void present() {
    showCardsOnCreate();

    refreshCardsOnPullToRefresh();

    handleCardClickEvents();

    showMoreCardsOnBottomReached();

    showCardsOnRetry();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private void showCardsOnRetry() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.retry())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(created -> view.showProgressIndicator())
        .flatMapSingle(retryClicked -> socialManager.getCards())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cards -> showCardsAndHideProgress(cards))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> view.showGenericError());
  }

  private void showMoreCardsOnBottomReached() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(create -> view.reachesBottom())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(create -> view.showLoadMoreProgressIndicator())
        .flatMapSingle(bottomReached -> socialManager.getNextCards())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cards -> showMoreCardsAndHideLoadMoreProgress(cards))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> Log.d(this.getClass()
            .getCanonicalName(), "ERROR LOADING MORE CARDS"));
  }

  private void handleCardClickEvents() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.articleClicked())
        .map(cardTouchEvent -> {
          if (cardTouchEvent.getActionType()
              .equals(CardTouchEvent.Type.ARTICLE_BODY)) {
            return cardTouchEvent.getCard()
                .getArticleLink();
          } else if (cardTouchEvent.getActionType()
              .equals(CardTouchEvent.Type.ARTICLE_HEADER)) {
            return cardTouchEvent.getCard()
                .getPublisherLink();
          }
          throw new IllegalStateException("Unknown Card Touch Event type.");
        })
        .doOnNext(link -> link.launch())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(articleUrl -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void refreshCardsOnPullToRefresh() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.refreshes())
        .flatMapSingle(refresh -> socialManager.getCards())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cards -> showCardsAndHideRefresh(cards))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> view.showGenericError());
  }

  private void showCardsOnCreate() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.showProgressIndicator())
        .flatMapSingle(created -> socialManager.getCards())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cards -> showCardsAndHideProgress(cards))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> view.showGenericError());
  }

  private void showMoreCardsAndHideLoadMoreProgress(List<Card> cards) {
    view.hideLoadMoreProgressIndicator();
    view.showMoreCards(cards);
  }

  private void showCardsAndHideProgress(List<Card> cards) {
    view.hideProgressIndicator();
    view.showCards(cards);
  }

  private void showCardsAndHideRefresh(List<Card> cards) {
    view.hideRefresh();
    view.showCards(cards);
  }
}
