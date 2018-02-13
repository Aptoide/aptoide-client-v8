package cm.aptoide.pt.social.presenter;

import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.Store;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialCard;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.link.LinksHandlerFactory;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.repository.StoreRepository;
import cm.aptoide.pt.social.data.AggregatedMedia;
import cm.aptoide.pt.social.data.AggregatedRecommendation;
import cm.aptoide.pt.social.data.AggregatedStore;
import cm.aptoide.pt.social.data.AppUpdate;
import cm.aptoide.pt.social.data.AppUpdateCardTouchEvent;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.CardType;
import cm.aptoide.pt.social.data.FollowStoreCardTouchEvent;
import cm.aptoide.pt.social.data.LikesPreviewCardTouchEvent;
import cm.aptoide.pt.social.data.Media;
import cm.aptoide.pt.social.data.MinimalPostTouchEvent;
import cm.aptoide.pt.social.data.PopularApp;
import cm.aptoide.pt.social.data.PopularAppTouchEvent;
import cm.aptoide.pt.social.data.Post;
import cm.aptoide.pt.social.data.RatedRecommendation;
import cm.aptoide.pt.social.data.Recommendation;
import cm.aptoide.pt.social.data.SocialAction;
import cm.aptoide.pt.social.data.SocialCardTouchEvent;
import cm.aptoide.pt.social.data.SocialHeaderCardTouchEvent;
import cm.aptoide.pt.social.data.StoreAppCardTouchEvent;
import cm.aptoide.pt.social.data.StoreCardTouchEvent;
import cm.aptoide.pt.social.data.StoreLatestApps;
import cm.aptoide.pt.social.data.Timeline;
import cm.aptoide.pt.social.data.TimelineStatsTouchEvent;
import cm.aptoide.pt.social.data.User;
import cm.aptoide.pt.social.data.UserUnfollowCardTouchEvent;
import cm.aptoide.pt.social.data.analytics.EventErrorHandler;
import cm.aptoide.pt.social.view.TimelineUser;
import cm.aptoide.pt.social.view.TimelineView;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.utils.AptoideUtils;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class TimelinePresenter implements Presenter {

  private final TimelineView view;
  private final Timeline timeline;
  private final CrashReport crashReport;
  private final TimelineNavigator timelineNavigation;
  private final PermissionManager permissionManager;
  private final PermissionService permissionRequest;
  private final InstallManager installManager;
  private final StoreRepository storeRepository;
  private final StoreUtilsProxy storeUtilsProxy;
  private final StoreCredentialsProviderImpl storeCredentialsProvider;
  private final AptoideAccountManager accountManager;
  private final TimelineAnalytics timelineAnalytics;
  private final Long userId;
  private final Long storeId;
  private final StoreContext storeContext;
  private final Resources resources;
  private final LinksHandlerFactory linksNavigator;
  private final Scheduler viewScheduler;

  public TimelinePresenter(@NonNull TimelineView cardsView, @NonNull Timeline timeline,
      Scheduler viewScheduler, CrashReport crashReport, TimelineNavigator timelineNavigation,
      PermissionManager permissionManager, PermissionService permissionRequest,
      InstallManager installManager, StoreRepository storeRepository,
      StoreUtilsProxy storeUtilsProxy, StoreCredentialsProviderImpl storeCredentialsProvider,
      AptoideAccountManager accountManager, TimelineAnalytics timelineAnalytics, Long userId,
      Long storeId, StoreContext storeContext, Resources resources,
      LinksHandlerFactory linksNavigator) {
    this.view = cardsView;
    this.timeline = timeline;
    this.viewScheduler = viewScheduler;
    this.crashReport = crashReport;
    this.timelineNavigation = timelineNavigation;
    this.permissionManager = permissionManager;
    this.permissionRequest = permissionRequest;
    this.installManager = installManager;
    this.storeRepository = storeRepository;
    this.storeUtilsProxy = storeUtilsProxy;
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.accountManager = accountManager;
    this.timelineAnalytics = timelineAnalytics;
    this.userId = userId;
    this.storeId = storeId;
    this.storeContext = storeContext;
    this.resources = resources;
    this.linksNavigator = linksNavigator;
  }

  @Override public void present() {
    onCreateShowPosts();

    onPullToRefreshRefreshPosts();

    onBottomReachedShowMorePosts();

    onRetryShowPosts();

    clickOnPostHeader();

    clickOnPostBody();

    clickOnLikeSocialPost();

    clickOnLikeNonSocialPost();

    clickOnCommentSocialPost();

    clickOnCommentNonSocialPost();

    clickOnCommentsNumberLabel();

    clickOnShare();

    commentPostResponse();

    sharePostConfirmation();

    clickOnTimelineStats();

    clickOnLikesPreview();

    clickOnLogin();

    handleLoginMessageClick();

    listenToScrollUp();

    listenToScrollDown();

    handleFabClick();

    onViewCreatedClickOnLastComment();

    handlePostNavigation();

    handleNativeAdError();

    onViewCreatedHandleVisibleItems();

    onViewCreatedClickOnTimelineNotification();

    onViewCreatedClickOnGeneralNotification();

    onViewCreatedClickOnNotificationCenter();

    onViewCreatedClickOnAddressBook();

    onViewCreatedShowUser();

    clickOnEmptyStateAction();

    handleScrollEvents();

    clickOnDeletePost();

    clickOnUnfollowStore();

    clickOnUnfollowUser();

    clickOnReportAbuse();

    clickOnIgnoreUpdate();
  }

  private void clickOnReportAbuse() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.REPORT_ABUSE))
            .flatMapSingle(cardTouchEvent -> view.takeFeedbackScreenShot()
                .doOnSuccess(
                    screenShotPath -> timelineNavigation.navigateToFeedbackScreen(screenShotPath,
                        cardTouchEvent.getCard()
                            .getCardId())))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void clickOnIgnoreUpdate() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.IGNORE_UPDATE))
            .flatMapCompletable(cardTouchEvent -> timeline.ignoreUpdate(
                ((AppUpdate) cardTouchEvent.getCard()).getPackageName())
                .observeOn(viewScheduler)
                .andThen(Completable.fromAction(() -> view.removePost(cardTouchEvent.getCard()))))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void clickOnUnfollowStore() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.UNFOLLOW_STORE))
            .doOnNext(cardTouchEvent -> storeUtilsProxy.unSubscribeStore(
                ((StoreLatestApps) cardTouchEvent.getCard()).getStoreName(),
                storeCredentialsProvider))
            .observeOn(viewScheduler)
            .doOnNext(cardTouchEvent -> view.showUserUnsubscribedMessage(
                ((StoreLatestApps) cardTouchEvent.getCard()).getStoreName()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void clickOnUnfollowUser() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.UNFOLLOW_USER))
            .flatMapCompletable(cardTouchEvent -> {
              if (cardTouchEvent instanceof UserUnfollowCardTouchEvent) {
                return timeline.unfollowUser(((UserUnfollowCardTouchEvent) cardTouchEvent).getId())
                    .observeOn(viewScheduler)
                    .andThen(Completable.fromAction(() -> view.showUserUnsubscribedMessage(
                        ((UserUnfollowCardTouchEvent) cardTouchEvent).getName())));
              }
              return Completable.error(new IllegalStateException(
                  "Trying to unfollow user without using the UserUnfollowCardTouchEvent "));
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void clickOnDeletePost() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.DELETE_POST))
            .flatMapCompletable(cardTouchEvent -> timeline.deletePost(cardTouchEvent.getCard()
                .getCardId())
                .observeOn(viewScheduler)
                .andThen(Completable.fromAction(() -> {
                  view.removePost(cardTouchEvent.getCard());
                })))
            .retry())
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> {
          view.showGenericError();
          crashReport.log(throwable);
        });
  }

  private void handleScrollEvents() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.getScrollEvents())
        .doOnNext(position -> timelineAnalytics.scrollToPosition(position))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void clickOnEmptyStateAction() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.POST))
            .doOnNext(__ -> timelineNavigation.navigateToCreatePost())
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void onViewCreatedShowUser() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> timeline.getUser(false, false)
            .observeOn(viewScheduler)
            .doOnSubscribe(() -> view.showUserLoading())
            .doOnNext(user -> view.showUser(convertUser(user))))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .observeOn(viewScheduler)
        .subscribe(cardTouchEvent -> {
        }, throwable -> {
          crashReport.log(throwable);
          view.hideUser();
        });
  }

  @NonNull private TimelineUser convertUser(User user) {
    return new TimelineUser(user.isLogged(), user.hasNotification(), user.getBodyMessage(),
        user.getImage(), user.getUrlAction(), user.getNotificationId(), user.hasStats(),
        user.getFollowers(), user.getFollowing(), user.getAnalyticsUrl());
  }

  private void onViewCreatedClickOnAddressBook() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.ADD_FRIEND))
            .doOnNext(cardTouchEvent -> {
              timelineAnalytics.sendFollowFriendsEvent();
              timelineNavigation.navigateToAddressBook();
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void onViewCreatedClickOnNotificationCenter() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.NOTIFICATION_CENTER))
            .doOnNext(cardTouchEvent -> timelineNavigation.navigateToNotificationCenter()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void onViewCreatedClickOnTimelineNotification() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.NOTIFICATION))
            .flatMap(cardTouchEvent -> Observable.just(
                Uri.parse(((TimelineUser) cardTouchEvent.getCard()).getNotificationUrlAction())
                    .getQueryParameter("cardId"))
                .filter(postId -> postId != null)
                .observeOn(viewScheduler)
                .doOnNext(__ -> view.showPostProgressIndicator())
                .flatMapSingle(cardId -> timeline.getFreshTimeline(cardId))
                .observeOn(viewScheduler)
                .doOnNext(timelineModel -> {
                  if (timelineModel != null
                      && timelineModel.getPosts()
                      .size() > 0) {
                    showCardsAndHidePostProgress(timelineModel.getPosts());
                  } else if (timelineModel != null
                      && timelineModel.getPosts()
                      .size() == 0) {
                    showEmptyStateAndHidePostProgress();
                  } else {
                    view.showGenericViewError();
                  }
                })
                .flatMapCompletable(cards -> timeline.notificationDismissed(
                    ((TimelineUser) cardTouchEvent.getCard()).getNotificationId())
                    .andThen(Completable.fromAction(() -> timelineAnalytics.notificationShown(
                        ((TimelineUser) cardTouchEvent.getCard()).getAnalyticsUrl()))))
                .retry()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void onViewCreatedClickOnGeneralNotification() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.NOTIFICATION))
            .filter(cardTouchEvent -> Uri.parse(
                ((TimelineUser) cardTouchEvent.getCard()).getNotificationUrlAction())
                .getQueryParameter("cardId") == null)
            .doOnNext(cardTouchEvent -> linksNavigator.get(LinksHandlerFactory.NOTIFICATION_LINK,
                ((TimelineUser) cardTouchEvent.getCard()).getNotificationUrlAction())
                .launch())
            .flatMapCompletable(cardTouchEvent -> timeline.notificationDismissed(
                ((TimelineUser) cardTouchEvent.getCard()).getNotificationId())
                .andThen(Completable.fromAction(() -> timelineAnalytics.notificationShown(
                    ((TimelineUser) cardTouchEvent.getCard()).getAnalyticsUrl())))))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleNativeAdError() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.ERROR))
            .observeOn(viewScheduler)
            .doOnNext(cardTouchEvent -> view.removePost(cardTouchEvent.getCard()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void onViewCreatedClickOnLastComment() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(viewCreated -> view.postClicked()
            .filter(cardClicked -> cardClicked.getActionType()
                .equals(CardTouchEvent.Type.LAST_COMMENT))
            .doOnNext(cardTouchEvent -> timelineNavigation.navigateToUserHome(
                cardTouchEvent.getCard()
                    .getComments()
                    .get(cardTouchEvent.getCard()
                        .getComments()
                        .size() - 1)
                    .getUserId())))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> view.showGenericError());
  }

  private void onViewCreatedHandleVisibleItems() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(lifecycleEvent -> view.getVisibleItems()
            .filter(post -> !post.getType()
                .isDummy())
            .flatMapCompletable(post -> {
              if (!post.getType()
                  .isAggregated()) {
                return timeline.setPostRead(post.getMarkAsReadUrl(), post.getCardId(),
                    post.getType());
              } else if (post.getType()
                  .isAggregated() && post.getType()
                  .isMedia()) {
                return timeline.setPostRead(((AggregatedMedia) post).getMinimalCards(),
                    post.getType());
              } else if (post.getType()
                  .isAggregated() && post.getType()
                  .isStore()) {
                return timeline.setPostRead(((AggregatedStore) post).getMinimalPosts(),
                    post.getType());
              } else if (post.getType()
                  .equals(CardType.AGGREGATED_SOCIAL_APP) || post.getType()
                  .equals(CardType.AGGREGATED_SOCIAL_INSTALL)) {
                return timeline.setPostRead(((AggregatedRecommendation) post).getMinimalPosts(),
                    post.getType());
              }
              return Completable.complete();
            })
            .doOnError(throwable -> Logger.e(this, throwable))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handlePostNavigation() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> timelineNavigation.postNavigation()
            .observeOn(viewScheduler)
            .doOnNext(__ -> view.showPostProgressIndicator())
            .flatMapSingle(cardId -> timeline.getFreshTimeline(cardId))
            .observeOn(viewScheduler)
            .doOnNext(timelineModel -> {
              if (timelineModel.getPosts() != null
                  && timelineModel.getPosts()
                  .size() > 0) {
                showCardsAndHidePostProgress(timelineModel.getPosts());
              } else if (timelineModel.getPosts() != null
                  && timelineModel.getPosts()
                  .size() == 0) {
                showEmptyStateAndHidePostProgress();
              } else {
                view.showGenericViewError();
              }
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> view.showGenericError());
  }

  private void listenToScrollUp() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.scrolled()
            .throttleLast(1, TimeUnit.SECONDS)
            .filter(direction -> direction.top())
            .observeOn(viewScheduler)
            .flatMap(__2 -> view.showFloatingActionButton()
                .toObservable()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> view.showGenericError());
  }

  private void listenToScrollDown() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.scrolled()
            .throttleLast(1, TimeUnit.SECONDS)
            .filter(direction -> direction.bottom())
            .observeOn(viewScheduler)
            .flatMap(__2 -> view.hideFloatingActionButton()
                .toObservable()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> view.showGenericError());
  }

  private void handleFabClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.floatingActionButtonClicked()
            .doOnNext(click -> timelineAnalytics.sendFabClicked())
            .doOnNext(__2 -> timelineNavigation.navigateToCreatePost()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> view.showGenericError());
  }

  private void onCreateShowPosts() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> view.showGeneralProgressIndicator())
        .flatMapSingle(__ -> accountManager.accountStatus()
            .first()
            .toSingle())
        .observeOn(Schedulers.io())
        .flatMapSingle(account -> timeline.getTimeline())
        .observeOn(viewScheduler)
        .doOnNext(timelineModel -> {
          if (timelineModel.getPosts() != null
              && timelineModel.getPosts()
              .size() > 0) {
            showCardsAndHideProgress(timelineModel.getPosts());
          } else if (timelineModel.getPosts() != null
              && timelineModel.getPosts()
              .size() == 0) {
            showEmptyStateAndHideProgress();
          } else {
            view.showGenericViewError();
          }
          String timelineVersion = timelineModel.getTimelineVersion();
          if (timelineVersion != null) {
            timelineAnalytics.setVersion(timelineVersion);
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> {
          crashReport.log(throwable);
          view.showGenericViewError();
        });
  }

  private void showEmptyStateAndHideProgress() {
    view.hideGeneralProgressIndicator();
    view.showEmptyState();
  }

  private void onPullToRefreshRefreshPosts() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.refreshes()
            .flatMapSingle(__ -> accountManager.accountStatus()
                .first()
                .toSingle())
            .observeOn(Schedulers.io())
            .flatMapSingle(account -> timeline.getFreshTimeline())
            .observeOn(viewScheduler)
            .doOnNext(timelineModel -> {
              if (timelineModel.getPosts() != null
                  && timelineModel.getPosts()
                  .size() > 0) {
                showCardsAndHideRefresh(timelineModel.getPosts());
              } else if (timelineModel.getPosts() != null
                  && timelineModel.getPosts()
                  .size() == 0) {
                showEmptyStateAndHideRefresh();
              } else {
                view.showGenericViewError();
              }
            })
            .flatMap(posts -> timeline.getUser(true, true))
            .observeOn(viewScheduler)
            .doOnNext(user -> view.showUser(convertUser(user)))
            .doOnError(throwable -> {
              crashReport.log(throwable);
              view.showGenericViewError();
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        });
  }

  private void onBottomReachedShowMorePosts() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(create -> view.reachesBottom()
            .filter(__ -> timeline.hasMore())
            .observeOn(viewScheduler)
            .doOnNext(created -> view.showLoadMoreProgressIndicator())
            .flatMapSingle(bottomReached -> timeline.getNextTimelinePage())
            .observeOn(viewScheduler)
            .doOnNext(
                timelineModel -> showMoreCardsAndHideLoadMoreProgress(timelineModel.getPosts()))
            .doOnError(throwable -> {
              crashReport.log(throwable);
              view.showGenericError();
              view.hideLoadMoreProgressIndicator();
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> {
        });
  }

  private void onRetryShowPosts() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .observeOn(viewScheduler)
        .flatMap(__ -> view.retry()
            .doOnNext(__2 -> view.showGeneralProgressIndicator())
            .flatMapSingle(__3 -> accountManager.accountStatus()
                .first()
                .toSingle())
            .observeOn(Schedulers.io())
            .flatMapSingle(account -> timeline.getTimeline())
            .observeOn(viewScheduler)
            .doOnNext(timelineModel -> {
              if (timelineModel.getPosts() != null
                  && timelineModel.getPosts()
                  .size() > 0) {
                showCardsAndHideProgress(timelineModel.getPosts());
              } else if (timelineModel.getPosts() != null
                  && timelineModel.getPosts()
                  .size() == 0) {
                showEmptyStateAndHideProgress();
              } else {
                view.showGenericViewError();
              }
            })
            .doOnError(err -> {
              crashReport.log(err);
              view.showGenericViewError();
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void clickOnPostHeader() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.HEADER))
        .doOnNext(cardTouchEvent -> timelineAnalytics.sendClickOnPostHeaderEvent(cardTouchEvent))
        .doOnNext(cardTouchEvent -> timeline.knockWithSixpackCredentials(cardTouchEvent.getCard()
            .getAbUrl()))
        .doOnNext(cardTouchEvent -> {
          final Post post = cardTouchEvent.getCard();
          final CardType postType = post.getType();
          if (postType.equals(CardType.VIDEO) || postType.equals(CardType.ARTICLE)) {
            Media card = (Media) post;
            card.getPublisherLink()
                .launch();
          } else if (postType.isSocial()) {
            SocialHeaderCardTouchEvent socialHeaderCardTouchEvent =
                ((SocialHeaderCardTouchEvent) cardTouchEvent);
            navigateToStoreTimeline(socialHeaderCardTouchEvent);
          } else if (postType.equals(CardType.STORE)) {
            StoreLatestApps card = ((StoreLatestApps) post);
            timelineNavigation.navigateToStoreHome(card.getStoreName(), card.getStoreTheme());
          } else if (postType.equals(CardType.UPDATE)) {
            AppUpdate card = ((AppUpdate) post);
            timelineNavigation.navigateToStoreHome(card.getStoreName(), card.getStoreTheme());
          } else if (postType.equals(CardType.POPULAR_APP)) {
            PopularAppTouchEvent popularAppTouchEvent = (PopularAppTouchEvent) cardTouchEvent;
            timelineNavigation.navigateToStoreTimeline(popularAppTouchEvent.getUserId(),
                popularAppTouchEvent.getStoreTheme());
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(touchEvent -> {
        }, throwable -> {
          crashReport.log(throwable);
          view.showGenericError();
        });
  }

  private void clickOnPostBody() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.BODY))
            .doOnNext(cardTouchEvent -> timelineAnalytics.sendClickOnPostBodyEvent(cardTouchEvent))
            .doOnNext(cardTouchEvent -> timeline.knockWithSixpackCredentials(
                cardTouchEvent.getCard()
                    .getAbUrl()))
            .doOnNext(cardTouchEvent -> {
              final Post post = cardTouchEvent.getCard();
              final CardType type = post.getType();
              if (type.isMedia()) {
                Media card = (Media) post;
                card.getMediaLink()
                    .launch();
              } else {
                if (type.equals(CardType.RECOMMENDATION) || type.equals(CardType.SIMILAR)) {
                  Recommendation card = (Recommendation) post;
                  timelineNavigation.navigateToAppView(card.getAppId(), card.getPackageName());
                } else if (type.equals(CardType.STORE)) {
                  StoreAppCardTouchEvent storeAppCardTouchEvent =
                      (StoreAppCardTouchEvent) cardTouchEvent;
                  navigateToAppView(storeAppCardTouchEvent);
                } else if (type.equals(CardType.SOCIAL_STORE) || type.equals(
                    CardType.AGGREGATED_SOCIAL_STORE)) {
                  if (cardTouchEvent instanceof StoreAppCardTouchEvent) {
                    navigateToAppView((StoreAppCardTouchEvent) cardTouchEvent);
                  } else if (cardTouchEvent instanceof FollowStoreCardTouchEvent) {
                    FollowStoreCardTouchEvent followStoreCardTouchEvent =
                        ((FollowStoreCardTouchEvent) cardTouchEvent);
                    followStore(followStoreCardTouchEvent.getStoreId(),
                        followStoreCardTouchEvent.getStoreName());
                  } else if (cardTouchEvent instanceof StoreCardTouchEvent) {
                    StoreCardTouchEvent storeCardTouchEvent = (StoreCardTouchEvent) cardTouchEvent;
                    timelineNavigation.navigateToStoreHome(storeCardTouchEvent.getStoreName(),
                        storeCardTouchEvent.getStoreTheme());
                  }
                } else if (type.equals(CardType.UPDATE)) {
                  AppUpdate card = (AppUpdate) post;
                  if (cardTouchEvent instanceof AppUpdateCardTouchEvent) {
                    permissionManager.requestExternalStoragePermission(permissionRequest)
                        .flatMap(success -> {
                          if (installManager.showWarning()) {
                            view.showRootAccessDialog();
                          }
                          return timeline.updateApp(cardTouchEvent);
                        })
                        .observeOn(viewScheduler)
                        .distinctUntilChanged(install -> install.getState())
                        .doOnNext(install -> {
                          // TODO: 26/06/2017 get this logic out of here?  this is not working properly yet
                          ((AppUpdate) post).setInstallationStatus(install.getState());
                          view.swapPost(post,
                              ((AppUpdateCardTouchEvent) cardTouchEvent).getPosition());
                        })
                        .subscribe(downloadProgress -> {
                        }, throwable -> Logger.d(this.getClass()
                            // TODO: 26/06/2017 error handling
                            .getName(), "error"));
                  } else {
                    timelineNavigation.navigateToAppView(card.getAppUpdateId(),
                        card.getPackageName());
                  }
                } else if (type.equals(CardType.POPULAR_APP)) {
                  PopularApp card = (PopularApp) post;
                  timelineNavigation.navigateToAppView(card.getAppId(), card.getPackageName());
                } else if (type.equals(CardType.SOCIAL_RECOMMENDATION) || type.equals(
                    CardType.SOCIAL_INSTALL) || type.equals(CardType.SOCIAL_POST_RECOMMENDATION)) {
                  RatedRecommendation card = (RatedRecommendation) post;
                  timelineNavigation.navigateToAppView(card.getAppId(), card.getPackageName());
                } else if (type.equals(CardType.AGGREGATED_SOCIAL_INSTALL) || type.equals(
                    CardType.AGGREGATED_SOCIAL_APP)) {
                  AggregatedRecommendation card = (AggregatedRecommendation) post;
                  timelineNavigation.navigateToAppView(card.getAppId(), card.getPackageName());
                }
              }
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(articleUrl -> {
        }, throwable -> {
          crashReport.log(throwable);
          view.showGenericError();
        });
  }

  private void clickOnLikeSocialPost() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.LIKE) && cardTouchEvent.getCard()
                .getType()
                .isSocial() || cardTouchEvent.getCard()
                .getType()
                .equals(CardType.MINIMAL_CARD))
            .filter(cardTouchEvent -> !cardTouchEvent.getCard()
                .isLiked())
            .flatMapCompletable(cardTouchEvent -> accountManager.accountStatus()
                .first()
                .toSingle()
                .observeOn(viewScheduler)
                .doOnSuccess(account -> {
                  if (!account.isLoggedIn()) {
                    view.showLoginPromptWithAction();
                  } else {
                    if (showCreateStore(account)) {
                      view.showCreateStoreMessage(SocialAction.LIKE);
                    } else if (showSetUserOrStoreToPublic(account)) {
                      view.showSetUserOrStorePublicMessage();
                    } else {
                      cardTouchEvent.getCard()
                          .setLiked(true);
                      view.updatePost(((SocialCardTouchEvent) cardTouchEvent).getPosition());
                    }
                  }
                })
                .flatMapCompletable(account -> {
                  if (account.isLoggedIn()) {
                    if (showCreateStore(account)) {
                      return Completable.complete();
                    }

                    final Post post = cardTouchEvent.getCard();
                    return timeline.like(post, post.getCardId())
                        .andThen(Completable.fromAction(
                            () -> timelineAnalytics.sendLikeEvent(cardTouchEvent)));
                  }
                  return Completable.complete();
                })
                .doOnError(throwable -> timelineAnalytics.sendErrorLikeEvent(cardTouchEvent,
                    EventErrorHandler.GenericErrorEvent.LOGIN))))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> timeline.knockWithSixpackCredentials(cardTouchEvent.getCard()
            .getAbUrl()), throwable -> crashReport.log(throwable));
  }

  private boolean showSetUserOrStoreToPublic(Account account) {
    final Account.Access userAccess = account.getAccess();
    final Store store = account.getStore();
    return (userAccess == Account.Access.PRIVATE || userAccess == Account.Access.UNLISTED) && (store
        != null && !account.isPublicStore());
  }

  private boolean showCreateStore(Account account) {
    // user is private and does not have a store
    return account != null && !account.isPublicUser() && !account.hasStore();
  }

  private void handleLoginMessageClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.loginActionClick())
        .observeOn(viewScheduler)
        .doOnNext(__ -> timelineNavigation.navigateToLoginView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void clickOnLikeNonSocialPost() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.LIKE) && cardTouchEvent.getCard()
                .getType()
                .isNormal())
            .filter(cardTouchEvent -> !cardTouchEvent.getCard()
                .isLiked())
            .flatMapCompletable(cardTouchEvent -> accountManager.accountStatus()
                .first()
                .toSingle()
                .observeOn(viewScheduler)
                .doOnSuccess(account -> {
                  if (!account.isLoggedIn()) {
                    view.showLoginPromptWithAction();
                  } else {
                    if (showCreateStore(account)) {
                      view.showCreateStoreMessage(SocialAction.LIKE);
                    } else if (showSetUserOrStoreToPublic(account)) {
                      view.showSetUserOrStorePublicMessage();
                    } else {
                      cardTouchEvent.getCard()
                          .setLiked(true);
                      view.updatePost(((SocialCardTouchEvent) cardTouchEvent).getPosition());
                    }
                  }
                })
                .flatMapCompletable(account -> {
                  if (account.isLoggedIn()) {
                    if (showCreateStore(account)) {
                      return Completable.complete();
                    }
                    final Post post = cardTouchEvent.getCard();
                    return timeline.sharePost(post)
                        .flatMapCompletable(cardId -> timeline.like(post, cardId))
                        .andThen(Completable.fromAction(
                            () -> timelineAnalytics.sendLikeEvent(cardTouchEvent)));
                  } else {
                    timelineAnalytics.sendErrorLikeEvent(cardTouchEvent,
                        EventErrorHandler.GenericErrorEvent.LOGIN);
                    return Completable.complete();
                  }
                })
                .doOnError(throwable -> timelineAnalytics.sendLikeEvent(cardTouchEvent)))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> timeline.knockWithSixpackCredentials(cardTouchEvent.getCard()
            .getAbUrl()), throwable -> throwable.printStackTrace());
  }

  private void clickOnCommentSocialPost() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.COMMENT) && (cardTouchEvent.getCard()
            .getType()
            .isSocial() || cardTouchEvent.getCard()
            .getType()
            .equals(CardType.MINIMAL_CARD)))
        .flatMapCompletable(cardTouchEvent -> accountManager.accountStatus()
            .first()
            .toSingle()
            .observeOn(viewScheduler)
            .flatMapCompletable(account -> {
              if (account.isLoggedIn()) {
                if (showCreateStore(account)) {
                  return Completable.fromAction(
                      () -> view.showCreateStoreMessage(SocialAction.LIKE))
                      .andThen(sendErrorCommentEvent(cardTouchEvent,
                          EventErrorHandler.GenericErrorEvent.NO_STORE));
                } else if (showSetUserOrStoreToPublic(account)) {
                  return Completable.fromAction(() -> view.showSetUserOrStorePublicMessage())
                      .andThen(sendErrorCommentEvent(cardTouchEvent,
                          EventErrorHandler.GenericErrorEvent.PRIVATE_USER));
                }
                return Completable.fromAction(
                    () -> timelineNavigation.navigateToCommentsWithCommentDialogOpen(
                        cardTouchEvent.getCard()
                            .getCardId()))
                    .andThen(sendCommentEvent(cardTouchEvent));
              }
              return Completable.fromAction(() -> view.showLoginPromptWithAction())
                  .andThen(sendErrorCommentEvent(cardTouchEvent,
                      EventErrorHandler.GenericErrorEvent.LOGIN));
            })
            .doOnError(throwable -> timelineAnalytics.sendCommentEvent(cardTouchEvent)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> timeline.knockWithSixpackCredentials(cardTouchEvent.getCard()
            .getAbUrl()), throwable -> crashReport.log(throwable));
  }

  private void clickOnCommentNonSocialPost() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.COMMENT) && cardTouchEvent.getCard()
            .getType()
            .isNormal())
        .flatMapCompletable(cardTouchEvent -> accountManager.accountStatus()
            .first()
            .toSingle()
            .observeOn(viewScheduler)
            .flatMapCompletable(account -> {
              if (account.isLoggedIn()) {
                if (showCreateStore(account)) {
                  return Completable.fromAction(
                      () -> view.showCreateStoreMessage(SocialAction.LIKE))
                      .andThen(sendErrorCommentEvent(cardTouchEvent,
                          EventErrorHandler.GenericErrorEvent.NO_STORE));
                } else if (showSetUserOrStoreToPublic(account)) {
                  return Completable.fromAction(() -> view.showSetUserOrStorePublicMessage())
                      .andThen(sendErrorCommentEvent(cardTouchEvent,
                          EventErrorHandler.GenericErrorEvent.PRIVATE_USER));
                }
                return Completable.fromAction(
                    () -> view.showCommentDialog((SocialCardTouchEvent) cardTouchEvent))
                    .andThen(sendCommentEvent(cardTouchEvent));
              }
              return Completable.fromAction(() -> view.showLoginPromptWithAction())
                  .andThen(sendErrorCommentEvent(cardTouchEvent,
                      EventErrorHandler.GenericErrorEvent.LOGIN));
            })
            .doOnError(throwable -> timelineAnalytics.sendCommentEvent(cardTouchEvent)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> timeline.knockWithSixpackCredentials(cardTouchEvent.getCard()
            .getAbUrl()), throwable -> {
        });
  }

  @NonNull private Completable sendCommentEvent(CardTouchEvent event) {
    return Completable.fromAction(() -> timelineAnalytics.sendCommentEvent(event));
  }

  @NonNull private Completable sendErrorCommentEvent(CardTouchEvent event,
      EventErrorHandler.GenericErrorEvent error) {
    return Completable.fromAction(() -> timelineAnalytics.sendErrorCommentEvent(event, error));
  }

  private void clickOnCommentsNumberLabel() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(create -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.COMMENT_NUMBER))
        .flatMapCompletable(cardTouchEvent -> accountManager.accountStatus()
            .first()
            .toSingle()
            .observeOn(viewScheduler)
            .flatMapCompletable(account -> {
              if (account.isLoggedIn()) {
                return Completable.fromAction(() -> timelineNavigation.navigateToComments(
                    cardTouchEvent.getCard()
                        .getCardId()));
              }
              return Completable.fromAction(() -> view.showLoginPromptWithAction());
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> timeline.knockWithSixpackCredentials(cardTouchEvent.getCard()
            .getAbUrl()), throwable -> {
          crashReport.log(throwable);
          view.showGenericError();
        });
  }

  private void clickOnShare() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.SHARE))
        .flatMapCompletable(cardTouchEvent -> accountManager.accountStatus()
            .first()
            .toSingle()
            .observeOn(viewScheduler)
            .flatMapCompletable(account -> {
              if (account.isLoggedIn()) {
                if (showCreateStore(account)) {
                  return Completable.fromAction(
                      () -> view.showCreateStoreMessage(SocialAction.LIKE))
                      .andThen(sendErrorShareEvent(cardTouchEvent,
                          EventErrorHandler.GenericErrorEvent.NO_STORE));
                } else if (showSetUserOrStoreToPublic(account)) {
                  return Completable.fromAction(() -> view.showSetUserOrStorePublicMessage())
                      .andThen(sendErrorShareEvent(cardTouchEvent,
                          EventErrorHandler.GenericErrorEvent.PRIVATE_USER));
                }
                if (cardTouchEvent instanceof MinimalPostTouchEvent) {
                  return Completable.fromAction(() -> view.showSharePreview(
                      ((MinimalPostTouchEvent) cardTouchEvent).getOriginalPost(),
                      (cardTouchEvent).getCard(), account))
                      .andThen(sendShareEvent(cardTouchEvent));
                }
                return Completable.fromAction(
                    () -> view.showSharePreview(cardTouchEvent.getCard(), account))
                    .andThen(sendShareEvent(cardTouchEvent));
              }
              return Completable.fromAction(() -> view.showLoginPromptWithAction())
                  .andThen(sendErrorShareEvent(cardTouchEvent,
                      EventErrorHandler.GenericErrorEvent.LOGIN));
            })
            .doOnError(throwable -> timelineAnalytics.sendShareEvent(cardTouchEvent)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> timeline.knockWithSixpackCredentials(cardTouchEvent.getCard()
            .getAbUrl()), throwable -> {
          crashReport.log(throwable);
          view.showGenericError();
        });
  }

  private Completable sendShareEvent(CardTouchEvent event) {
    return Completable.fromAction(() -> timelineAnalytics.sendShareEvent(event));
  }

  private Completable sendErrorShareEvent(CardTouchEvent event,
      EventErrorHandler.GenericErrorEvent error) {
    return Completable.fromAction(() -> timelineAnalytics.sendErrorShareEvent(event, error));
  }

  private void commentPostResponse() {
    //local comment dialog
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.commentPosted())
        .flatMapCompletable(comment -> timeline.sharePost(comment.getPost()
            .getCardId())
            .flatMapCompletable(responseCardId -> accountManager.accountStatus()
                .first()
                .toSingle()
                .observeOn(viewScheduler)
                .flatMapCompletable(account -> {
                  comment.getPost()
                      .addComment(new SocialCard.CardComment(-1, comment.getCommentText(),
                          account.getNickname(), account.getAvatar(),
                          Long.valueOf(account.getId())));
                  return Completable.fromAction(() -> view.updatePost(comment.getPostPosition()));
                })
                .andThen(timeline.postComment(responseCardId, comment.getCommentText()))))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> {
          crashReport.log(throwable);
          view.showGenericError();
          //timelineAnalytics.sendCommentCompleted(false);
        });

    //comment from comments fragment
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> timelineNavigation.commentNavigation()
            .observeOn(viewScheduler)
            .doOnNext(commentWrapper -> {
              if (commentWrapper.hasError()) {
                view.sendCommentErrorAnalytics(commentWrapper.getPostId());
              } else {
                view.sendCommentSuccessAnalytics(commentWrapper.getPostId());
                view.showLastComment(commentWrapper.getCommentText());
              }
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(comment -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void sharePostConfirmation() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.shareConfirmation()
            .flatMapSingle(shareEvent -> timeline.sharePost(shareEvent.getPost())
                .doOnSuccess(cardId -> view.showShareSuccessMessage()))
            .retry())
        .doOnNext(cardid -> timelineAnalytics.sendSocialCardPreviewActionEvent(
            TimelineAnalytics.SOCIAL_CARD_ACTION_SHARE_CONTINUE))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> {
          crashReport.log(throwable);
          view.showGenericError();
        });
  }

  private void clickOnTimelineStats() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.TIMELINE_STATS))
        .doOnNext(cardTouchEvent -> {
          TimelineStatsTouchEvent timelineStatsTouchEvent =
              (TimelineStatsTouchEvent) cardTouchEvent;
          TimelineUser timelineStatsPost = (TimelineUser) timelineStatsTouchEvent.getCard();
          if (timelineStatsTouchEvent.getButtonClicked()
              .equals(TimelineStatsTouchEvent.ButtonClicked.FOLLOWERS)) {
            String title = AptoideUtils.StringU.getFormattedString(
                R.string.social_timeline_followers_fragment_title, resources,
                timelineStatsPost.getFollowers());
            if (storeContext.equals(StoreContext.home)) {
              timelineNavigation.navigateToFollowersViewStore(title);
            } else {
              if (userId == null || userId <= 0) {
                timelineNavigation.navigateToFollowersViewStore(storeId, title);
              } else {
                timelineNavigation.navigateToFollowersViewUser(userId, title);
              }
            }
          } else {
            if (timelineStatsTouchEvent.getButtonClicked()
                .equals(TimelineStatsTouchEvent.ButtonClicked.FOLLOWING)) {
              String title = AptoideUtils.StringU.getFormattedString(
                  R.string.social_timeline_following_fragment_title, resources,
                  timelineStatsPost.getFollowing());
              if (storeContext.equals(StoreContext.home)) {
                timelineNavigation.navigateToFollowingViewUser(userId, title);
              } else {
                if (userId == null || userId <= 0) {
                  timelineNavigation.navigateToFollowingViewStore(storeId, title);
                } else {
                  timelineNavigation.navigateToFollowingViewUser(userId, title);
                }
              }
            }
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> {
          crashReport.log(throwable);
          view.showGenericError();
        });
  }

  private void clickOnLikesPreview() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.LIKES_PREVIEW))
            .doOnNext(cardTouchEvent -> timelineNavigation.navigateToLikesView(
                cardTouchEvent.getCard()
                    .getCardId(), ((LikesPreviewCardTouchEvent) cardTouchEvent).getLikesNumber()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> timeline.knockWithSixpackCredentials(cardTouchEvent.getCard()
            .getAbUrl()), throwable -> {
          crashReport.log(throwable);
          view.showGenericError();
        });
  }

  private void clickOnLogin() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.LOGIN))
        .flatMapSingle(__ -> accountManager.accountStatus()
            .first()
            .toSingle())
        .doOnNext(account -> {
          if (account.isLoggedIn()) {
            timelineNavigation.navigateToMyAccountView();
          } else {
            timelineNavigation.navigateToLoginView();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> {
          crashReport.log(throwable);
          view.showGenericError();
        });
  }

  private void showCardsAndHideProgress(List<Post> cards) {
    view.hideGeneralProgressIndicator();
    view.showCards(cards);
  }

  private void showCardsAndHidePostProgress(List<Post> cards) {
    view.hidePostProgressIndicator();
    view.showCards(cards);
  }

  private void showEmptyStateAndHidePostProgress() {
    view.hidePostProgressIndicator();
    view.showEmptyState();
  }

  private void showCardsAndHideRefresh(List<Post> cards) {
    view.hideRefresh();
    view.showCards(cards);
  }

  private void showEmptyStateAndHideRefresh() {
    view.hideRefresh();
    view.showEmptyState();
  }

  private void showMoreCardsAndHideLoadMoreProgress(List<Post> cards) {
    view.hideLoadMoreProgressIndicator();
    view.showMoreCards(cards);
  }

  private void navigateToStoreTimeline(SocialHeaderCardTouchEvent socialHeaderCardTouchEvent) {
    if (!TextUtils.isEmpty(socialHeaderCardTouchEvent.getStoreName())) {
      timelineNavigation.navigateToStoreTimeline(socialHeaderCardTouchEvent.getStoreName(),
          socialHeaderCardTouchEvent.getStoreTheme());
    } else {
      timelineNavigation.navigateToStoreTimeline(socialHeaderCardTouchEvent.getUserId(),
          socialHeaderCardTouchEvent.getStoreTheme());
    }
  }

  private void navigateToAppView(StoreAppCardTouchEvent cardTouchEvent) {
    timelineNavigation.navigateToAppView(cardTouchEvent.getPackageName());
  }

  private void followStore(long storeId, String storeName) {
    storeRepository.isSubscribed(storeId)
        .first()
        .observeOn(viewScheduler)
        .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE))
        .subscribe(isSubscribed -> {
          if (isSubscribed) {
            storeUtilsProxy.unSubscribeStore(storeName, storeCredentialsProvider);
            view.showStoreUnsubscribedMessage(storeName);
          } else {
            storeUtilsProxy.subscribeStore(storeName);
            view.showStoreSubscribedMessage(storeName);
          }
        }, (throwable) -> throwable.printStackTrace());
  }
}
