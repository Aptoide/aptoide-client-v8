package cm.aptoide.pt.editorialList;

import android.support.annotation.VisibleForTesting;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.editorial.FakeReactionModel;
import cm.aptoide.pt.editorial.ReactionsResponse;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.reactions.data.ReactionType;
import cm.aptoide.pt.reactions.network.LoadReactionModel;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

public class EditorialListPresenter implements Presenter {

  private final EditorialListView view;
  private final EditorialListManager editorialListManager;
  private final AptoideAccountManager accountManager;
  private final EditorialListNavigator editorialListNavigator;
  private final EditorialListAnalytics editorialListAnalytics;
  private final CrashReport crashReporter;
  private final Scheduler viewScheduler;

  public EditorialListPresenter(EditorialListView editorialListView,
      EditorialListManager editorialListManager, AptoideAccountManager accountManager,
      EditorialListNavigator editorialListNavigator, EditorialListAnalytics editorialListAnalytics,
      CrashReport crashReporter, Scheduler viewScheduler) {
    this.view = editorialListView;
    this.editorialListManager = editorialListManager;
    this.accountManager = accountManager;
    this.editorialListNavigator = editorialListNavigator;
    this.editorialListAnalytics = editorialListAnalytics;
    this.crashReporter = crashReporter;
    this.viewScheduler = viewScheduler;
  }

  @Override public void present() {
    onCreateLoadViewModel();
    onCardCreatedLoadReactionModel();
    handleImpressions();
    handleEditorialCardClick();
    handlePullToRefresh();
    handleRetryClick();
    handleBottomReached();
    handleUserImageClick();
    handleReactionClick();
    handleUserReaction();
    handleLogInClick();
    loadUserImage();
  }

  @VisibleForTesting public void onCardCreatedLoadReactionModel() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.cardCreated())
        .flatMapSingle(editorialHomeEvent -> loadReactionModel(editorialHomeEvent.getCardId(),
            editorialHomeEvent.getBundlePosition()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, crashReporter::log);
  }

  private Single<LoadReactionModel> loadReactionModel(String cardId, int position) {
    return editorialListManager.loadReactionModel(cardId)
        .observeOn(viewScheduler)
        .doOnSuccess(reactionModel -> view.updateReactions(reactionModel, position));
  }

  @VisibleForTesting public void onCreateLoadViewModel() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .doOnNext(created -> view.showLoading())
        .flatMapSingle(created -> loadEditorialListViewModel(false))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReporter::log);
  }

  @VisibleForTesting public void loadUserImage() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> accountManager.accountStatus())
        .flatMap(account -> getUserAvatar(account))
        .observeOn(viewScheduler)
        .doOnNext(userAvatarUrl -> {
          if (userAvatarUrl != null) {
            view.setUserImage(userAvatarUrl);
          } else {
            view.setDefaultUserImage();
          }
          view.showAvatar();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReporter.log(throwable));
  }

  @VisibleForTesting public void handleEditorialCardClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.editorialCardClicked()
            .observeOn(viewScheduler)
            .doOnNext(click -> {
              editorialListAnalytics.sendEditorialInteractEvent(click.getCardId(),
                  click.getBundlePosition());
              editorialListNavigator.navigateToEditorial(click.getCardId());
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(homeClick -> {
        }, throwable -> crashReporter.log(throwable));
  }

  @VisibleForTesting public void handlePullToRefresh() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.refreshes()
            .flatMapSingle(refreshed -> loadFreshEditorialListViewModel())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(bundles -> {
        }, crashReporter::log);
  }

  @VisibleForTesting public void handleRetryClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.retryClicked()
            .observeOn(viewScheduler)
            .doOnNext(bottom -> view.showLoading())
            .flatMapSingle(__ -> loadFreshEditorialListViewModel()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(notificationUrl -> {
        }, crashReporter::log);
  }

  @VisibleForTesting public void handleImpressions() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.visibleCards()
            .observeOn(viewScheduler)
            .doOnNext(editorialListEvent -> editorialListAnalytics.sendEditorialImpressionEvent(
                editorialListEvent.getCardId(), editorialListEvent.getPosition())))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReporter::log);
  }

  @VisibleForTesting public void handleBottomReached() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.reachesBottom()
            .filter(__ -> editorialListManager.hasMore())
            .observeOn(viewScheduler)
            .doOnNext(bottomReached -> view.showLoadMore())
            .flatMapSingle(bottomReached -> loadEditorialListViewModel(true))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(bundles -> {
        }, throwable -> crashReporter.log(throwable));
  }

  private Single<EditorialListViewModel> loadEditorialListViewModel(boolean loadMore) {
    return editorialListManager.loadEditorialListViewModel(loadMore, false)
        .observeOn(viewScheduler)
        .doOnSuccess(editorialListViewModel -> {
          if (!editorialListViewModel.isLoading()) {
            view.hideLoading();
          }
          if (editorialListViewModel.hasError()) {
            if (editorialListViewModel.getError() == EditorialListViewModel.Error.NETWORK) {
              view.showNetworkError();
            } else {
              view.showGenericError();
            }
          } else {
            view.populateView(editorialListViewModel);
          }
          view.hideLoadMore();
        })
        .map(editorialViewModel -> editorialViewModel);
  }

  private Single<EditorialListViewModel> loadFreshEditorialListViewModel() {
    return editorialListManager.loadEditorialListViewModel(false, true)
        .observeOn(viewScheduler)
        .doOnSuccess(editorialListViewModel -> {
          view.hideRefresh();
          if (!editorialListViewModel.isLoading()) {
            view.hideLoading();
          }
          if (editorialListViewModel.hasError()) {
            if (editorialListViewModel.getError() == EditorialListViewModel.Error.NETWORK) {
              view.showNetworkError();
            } else {
              view.showGenericError();
            }
          } else {
            view.update(editorialListViewModel.getCurationCards());
          }
          view.hideLoadMore();
        })
        .map(editorialViewModel -> editorialViewModel);
  }

  @VisibleForTesting public void handleReactionClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.reactionsButtonClicked())
        .observeOn(viewScheduler)
        .doOnNext(homeEvent -> {
          editorialListAnalytics.sendReactionButtonClickEvent();
          view.showReactionsPopup(homeEvent.getCardId(), homeEvent.getBundlePosition());
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, crashReporter::log);
  }

  @VisibleForTesting public void handleUserReaction() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.reactionClicked())
        .flatMapSingle(homeEvent -> editorialListManager.setReaction(homeEvent.getCardId(),
            homeEvent.getReaction())
            .doOnSuccess(reactionsResponse -> handleReactionsResponse(reactionsResponse,
                homeEvent.getBundlePosition(), homeEvent.getReaction()))
            .flatMap(__ -> loadReactionModel(homeEvent.getCardId(), homeEvent.getBundlePosition())))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, crashReporter::log);
  }

  private void handleReactionsResponse(ReactionsResponse reactionsResponse, int bundlePosition,
      String reaction) {
    if (reactionsResponse.wasSuccess()) {
      view.setUserReaction(bundlePosition, reaction);
      editorialListAnalytics.sendReactedEvent();
    } else if (reactionsResponse.reactionsExceeded()) {
      view.showLogInDialog();
    } else {
      view.showErrorToast();
    }
  }

  @VisibleForTesting public void handleLogInClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.snackLogInClick())
        .doOnNext(homeEvent -> editorialListNavigator.navigateToLogIn())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(lifecycleEvent -> {
        }, crashReporter::log);
  }

  @VisibleForTesting public void handleUserImageClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.imageClick()
            .observeOn(viewScheduler)
            .doOnNext(account -> editorialListNavigator.navigateToMyAccount())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReporter.log(throwable));
  }

  private Observable<String> getUserAvatar(Account account) {
    String userAvatarUrl = null;
    if (account != null && account.isLoggedIn()) {
      userAvatarUrl = account.getAvatar();
    }
    return Observable.just(userAvatarUrl);
  }
}
