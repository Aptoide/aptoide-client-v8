package cm.aptoide.pt.v8engine.social.presenter;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import cm.aptoide.pt.v8engine.social.data.AggregatedRecommendation;
import cm.aptoide.pt.v8engine.social.data.AppUpdate;
import cm.aptoide.pt.v8engine.social.data.AppUpdateCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.CardType;
import cm.aptoide.pt.v8engine.social.data.FollowStoreCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.LikesCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.Media;
import cm.aptoide.pt.v8engine.social.data.PopularApp;
import cm.aptoide.pt.v8engine.social.data.PopularAppTouchEvent;
import cm.aptoide.pt.v8engine.social.data.Post;
import cm.aptoide.pt.v8engine.social.data.RatedRecommendation;
import cm.aptoide.pt.v8engine.social.data.Recommendation;
import cm.aptoide.pt.v8engine.social.data.SocialHeaderCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.StoreAppCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.StoreCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.StoreLatestApps;
import cm.aptoide.pt.v8engine.social.data.Timeline;
import cm.aptoide.pt.v8engine.social.data.TimelineStatsPost;
import cm.aptoide.pt.v8engine.social.data.TimelineStatsTouchEvent;
import cm.aptoide.pt.v8engine.social.view.TimelineView;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.store.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.post.PostFragment;
import cm.aptoide.pt.v8engine.view.app.AppViewFragment;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class TimelinePresenter implements Presenter {

  private final TimelineView view;
  private final Timeline timeline;
  private final CrashReport crashReport;
  private final TimelineNavigation timelineNavigation;
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
  private final FragmentNavigator fragmentNavigator;

  public TimelinePresenter(@NonNull TimelineView cardsView, @NonNull Timeline timeline,
      CrashReport crashReport, TimelineNavigation timelineNavigation,
      PermissionManager permissionManager, PermissionService permissionRequest,
      InstallManager installManager, StoreRepository storeRepository,
      StoreUtilsProxy storeUtilsProxy, StoreCredentialsProviderImpl storeCredentialsProvider,
      AptoideAccountManager accountManager, TimelineAnalytics timelineAnalytics, Long userId,
      Long storeId, StoreContext storeContext, Resources resources,
      FragmentNavigator fragmentNavigator) {
    this.view = cardsView;
    this.timeline = timeline;
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
    this.fragmentNavigator = fragmentNavigator;
  }

  @Override public void present() {
    showCardsOnCreate();

    refreshCardsOnPullToRefresh();

    handleCardClickOnHeaderEvents();

    handleCardClickOnBodyEvents();

    handleCardClickOnSocialLikeEvents();

    handleCardClickOnNonSocialLikeEvents();

    handleCardClickOnSocialCommentEvent();

    handleCardClickOnNonSocialCommentsEvent();

    handleCardClickOnCommentsNumberEvent();

    handleCardClickOnShareEvents();

    handleCommentPostResponseEvent();

    handleSharePostConfirmationEvent();

    handleCardClickOnStatsEvents();

    handleCardClickOnLikesPreviewEvent();

    handleCardClickOnLoginEvent();

    showMoreCardsOnBottomReached();

    showCardsOnRetry();

    listenToScrollUp();
    listenToScrollDown();
    handleFabClick();
  }

  @Override public void saveState(Bundle state) {
  }

  @Override public void restoreState(Bundle state) {
  }

  private void listenToScrollUp() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.scrolled()
            .throttleLast(1, TimeUnit.SECONDS)
            .filter(direction -> direction.top())
            .observeOn(AndroidSchedulers.mainThread())
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
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(__2 -> view.hideFloatingActionButton()
                .toObservable()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> view.showGenericError());
  }

  private void handleFabClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(__ -> view.floatingActionButtonClicked()
            .doOnNext(__2 -> fragmentNavigator.navigateTo(new PostFragment())))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> view.showGenericError());
  }

  private void handleCardClickOnCommentsNumberEvent() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(create -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.COMMENT_NUMBER))
        .doOnNext(cardTouchEvent -> timelineNavigation.navigateToComments(cardTouchEvent.getCard()
            .getCardId()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> {
        });
  }

  private void handleCardClickOnLikesPreviewEvent() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.LIKES_PREVIEW))
        .doOnNext(cardTouchEvent -> timelineNavigation.navigateToLikesView(cardTouchEvent.getCard()
            .getCardId(), ((LikesCardTouchEvent) cardTouchEvent).getLikesNumber()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> {
        });
  }

  private void handleSharePostConfirmationEvent() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.shareConfirmation()
            .flatMapSingle(post -> timeline.sharePost(post)
                .doOnSuccess(cardId -> view.showShareSuccessMessage())))
        .doOnNext(cardid -> timelineAnalytics.sendSocialCardPreviewActionEvent(
            TimelineAnalytics.SOCIAL_CARD_ACTION_SHARE_CONTINUE))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> throwable.printStackTrace());
  }

  private void handleCommentPostResponseEvent() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.commentPosted())
        .flatMapCompletable((comment) -> timeline.sharePost(comment.getCardId())
            .flatMapCompletable(
                responseCardId -> timeline.postComment(responseCardId, comment.getCommentText()))
            .doOnCompleted(() -> view.showCommentSuccess()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> throwable.printStackTrace());
  }

  private void handleCardClickOnShareEvents() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.SHARE))
        .doOnNext(cardTouchEvent -> view.showSharePreview(cardTouchEvent.getCard()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> {
        });
  }

  private void handleCardClickOnSocialCommentEvent() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.COMMENT) && (isSocialPost(cardTouchEvent.getCard())
            || cardTouchEvent.getCard()
            .getType()
            .equals(CardType.MINIMAL_CARD)))
        .doOnNext(cardTouchEvent -> timelineNavigation.navigateToCommentsWithCommentDialogOpen(
            cardTouchEvent.getCard()
                .getCardId()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> {
        });
  }

  private void handleCardClickOnNonSocialCommentsEvent() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.COMMENT) && (isNormalPost(cardTouchEvent.getCard())))
        .doOnNext(cardTouchEvent -> view.showCommentDialog(cardTouchEvent.getCard()
            .getCardId()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> {
        });
  }

  private void handleCardClickOnLoginEvent() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.LOGIN))
        .doOnNext(cardTouchEvent -> timelineNavigation.navigateToAccountView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> {
        });
  }

  private void handleCardClickOnStatsEvents() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.TIMELINE_STATS))
        .doOnNext(cardTouchEvent -> {
          TimelineStatsTouchEvent timelineStatsTouchEvent =
              (TimelineStatsTouchEvent) cardTouchEvent;
          TimelineStatsPost timelineStatsPost =
              (TimelineStatsPost) timelineStatsTouchEvent.getCard();
          if (timelineStatsTouchEvent.getButtonClicked()
              .equals(TimelineStatsTouchEvent.ButtonClicked.FOLLOWFRIENDS)) {
            timelineAnalytics.sendFollowFriendsEvent();
            timelineNavigation.navigateToAddressBook();
          } else {
            if (timelineStatsTouchEvent.getButtonClicked()
                .equals(TimelineStatsTouchEvent.ButtonClicked.FOLLOWERS)) {
              String title = AptoideUtils.StringU.getFormattedString(
                  R.string.social_timeline_followers_fragment_title, resources,
                  timelineStatsPost.getFollowers());
              if (storeContext.equals(StoreContext.home)) {
                timelineNavigation.navigateToFollowersViewStore(0L, title);
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
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> {
        });
  }

  private void handleCardClickOnSocialLikeEvents() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.LIKE) && isSocialPost(cardTouchEvent.getCard()))
            .flatMapCompletable(cardTouchEvent -> timeline.like(cardTouchEvent.getCard()
                .getCardId())))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> {
        });
  }

  private void handleCardClickOnNonSocialLikeEvents() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.LIKE) && isNormalPost(cardTouchEvent.getCard()))
            .flatMapCompletable(cardTouchEvent -> timeline.sharePost(cardTouchEvent.getCard())
                .flatMapCompletable(cardId -> timeline.like(cardId))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> view.showShareSuccessMessage()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> {
        });
  }

  private void showCardsOnRetry() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.retry())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(created -> view.showProgressIndicator())
        .flatMapSingle(retryClicked -> timeline.getCards())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cards -> showCardsAndHideProgress(cards))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> {
          throwable.printStackTrace();
          view.showGenericError();
        });
  }

  private void showMoreCardsOnBottomReached() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(create -> view.reachesBottom()
            .debounce(300, TimeUnit.MILLISECONDS))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(create -> view.showLoadMoreProgressIndicator())
        .flatMapSingle(bottomReached -> timeline.getNextCards())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cards -> showMoreCardsAndHideLoadMoreProgress(cards))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> throwable.printStackTrace());
  }

  private void handleCardClickOnHeaderEvents() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.HEADER))
        .doOnNext(cardTouchEvent -> {
          if (cardTouchEvent.getCard()
              .getType()
              .equals(CardType.VIDEO) || cardTouchEvent.getCard()
              .getType()
              .equals(CardType.ARTICLE)) {
            Media card = (Media) cardTouchEvent.getCard();
            sendClickOnMediaHeaderEvent(card);
            card.getPublisherLink()
                .launch();
          } else if (isSocialPost(cardTouchEvent.getCard())) {
            SocialHeaderCardTouchEvent socialHeaderCardTouchEvent =
                ((SocialHeaderCardTouchEvent) cardTouchEvent);
            navigateToStoreTimeline(socialHeaderCardTouchEvent);
          } else if (cardTouchEvent.getCard()
              .getType()
              .equals(CardType.STORE)) {
            StoreLatestApps card = ((StoreLatestApps) cardTouchEvent.getCard());
            timelineAnalytics.sendStoreLatestAppsClickEvent(card.getType()
                    .name(), Analytics.AppsTimeline.OPEN_STORE, "(blank)", Analytics.AppsTimeline.BLANK,
                card.getStoreName());
            timelineNavigation.navigateToStoreHome(card.getStoreName(), card.getStoreTheme());
          } else if (cardTouchEvent.getCard()
              .getType()
              .equals(CardType.UPDATE)) {
            AppUpdate card = ((AppUpdate) cardTouchEvent.getCard());
            timelineAnalytics.sendAppUpdateCardClickEvent(card.getType()
                    .name(), Analytics.AppsTimeline.OPEN_STORE, "(blank)", card.getPackageName(),
                card.getStoreName());
            timelineAnalytics.sendAppUpdateOpenStoreEvent(card.getType()
                    .name(), TimelineAnalytics.SOURCE_APTOIDE, card.getPackageName(),
                card.getStoreName());
            timelineNavigation.navigateToStoreHome(card.getStoreName(), card.getStoreTheme());
          } else if (cardTouchEvent.getCard()
              .getType()
              .equals(CardType.POPULAR_APP)) {
            PopularAppTouchEvent popularAppTouchEvent = (PopularAppTouchEvent) cardTouchEvent;
            timelineNavigation.navigateToStoreTimeline(popularAppTouchEvent.getUserId(),
                popularAppTouchEvent.getStoreTheme());
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(articleUrl -> {
        }, throwable -> {
          throwable.printStackTrace();
          crashReport.log(throwable);
        });
  }

  private void handleCardClickOnBodyEvents() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.BODY))
        .doOnNext(cardTouchEvent -> {
          if (isMediaPost(cardTouchEvent.getCard())) {
            Media card = (Media) cardTouchEvent.getCard();
            sendClickOnMediaBodyEvent(card);
            card.getMediaLink()
                .launch();
          } else if (cardTouchEvent.getCard()
              .getType()
              .equals(CardType.RECOMMENDATION)) {
            Recommendation card = (Recommendation) cardTouchEvent.getCard();
            timelineAnalytics.sendRecommendationCardClickEvent(card.getType()
                    .name(), Analytics.AppsTimeline.OPEN_APP_VIEW, "(blank)", card.getPackageName(),
                card.getPublisherName());
            timelineAnalytics.sendRecommendedOpenAppEvent(card.getType()
                    .name(), TimelineAnalytics.SOURCE_APTOIDE, card.getRelatedToPackageName(),
                card.getPackageName());
            timelineNavigation.navigateToAppView(card.getAppId(), card.getPackageName(),
                AppViewFragment.OpenType.OPEN_ONLY);
          } else if (cardTouchEvent.getCard()
              .getType()
              .equals(CardType.STORE)) {
            StoreAppCardTouchEvent storeAppCardTouchEvent = (StoreAppCardTouchEvent) cardTouchEvent;
            timelineAnalytics.sendStoreLatestAppsClickEvent(cardTouchEvent.getCard()
                    .getType()
                    .name(), Analytics.AppsTimeline.OPEN_APP_VIEW, "(blank)",
                storeAppCardTouchEvent.getPackageName(),
                ((StoreLatestApps) cardTouchEvent.getCard()).getStoreName());
            navigateToAppView(storeAppCardTouchEvent);
          } else if (cardTouchEvent.getCard()
              .getType()
              .equals(CardType.SOCIAL_STORE) || cardTouchEvent.getCard()
              .getType()
              .equals(CardType.AGGREGATED_SOCIAL_STORE)) {
            if (cardTouchEvent instanceof StoreAppCardTouchEvent) {
              navigateToAppView((StoreAppCardTouchEvent) cardTouchEvent);
            } else if (cardTouchEvent instanceof FollowStoreCardTouchEvent) {
              FollowStoreCardTouchEvent followStoreCardTouchEvent =
                  ((FollowStoreCardTouchEvent) cardTouchEvent);
              followStore(followStoreCardTouchEvent.getStoreId(),
                  followStoreCardTouchEvent.getStoreName());
            } else if (cardTouchEvent instanceof StoreCardTouchEvent) {
              StoreCardTouchEvent storeCardTouchEvent = (StoreCardTouchEvent) cardTouchEvent;
              if (cardTouchEvent.getCard() instanceof StoreLatestApps) {
                timelineAnalytics.sendOpenStoreEvent(cardTouchEvent.getCard()
                        .getType()
                        .name(), TimelineAnalytics.SOURCE_APTOIDE,
                    ((StoreLatestApps) cardTouchEvent.getCard()).getStoreName());
              }
              timelineNavigation.navigateToStoreHome(storeCardTouchEvent.getStoreName(),
                  storeCardTouchEvent.getStoreTheme());
            }
          } else if (cardTouchEvent.getCard()
              .getType()
              .equals(CardType.UPDATE)) {
            AppUpdate card = (AppUpdate) cardTouchEvent.getCard();
            timelineAnalytics.sendAppUpdateCardClickEvent(card.getType()
                    .name(), Analytics.AppsTimeline.UPDATE_APP, "(blank)", card.getPackageName(),
                card.getStoreName());
            timelineAnalytics.sendUpdateAppEvent(card.getType()
                .name(), TimelineAnalytics.SOURCE_APTOIDE, card.getPackageName());
            permissionManager.requestExternalStoragePermission(permissionRequest)
                .flatMap(success -> {
                  if (installManager.showWarning()) {
                    view.showRootAccessDialog();
                  }
                  return timeline.updateApp(cardTouchEvent);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged(install -> install.getState())
                .doOnNext(install -> {
                  // TODO: 26/06/2017 get this logic out of here?  this is not working properly yet
                  ((AppUpdate) cardTouchEvent.getCard()).setInstallationStatus(install.getState());
                  view.updateInstallProgress(cardTouchEvent.getCard(),
                      ((AppUpdateCardTouchEvent) cardTouchEvent).getCardPosition());
                })
                .subscribe(downloadProgress -> {
                }, throwable -> Logger.d(this.getClass()
                    // TODO: 26/06/2017 error handling
                    .getName(), "error"));
          } else if (cardTouchEvent.getCard()
              .getType()
              .equals(CardType.POPULAR_APP)) {
            PopularApp card = (PopularApp) cardTouchEvent.getCard();
            timelineNavigation.navigateToAppView(card.getAppId(), card.getPackageName(),
                AppViewFragment.OpenType.OPEN_ONLY);
          } else if (cardTouchEvent.getCard()
              .getType()
              .equals(CardType.SOCIAL_RECOMMENDATION) || cardTouchEvent.getCard()
              .getType()
              .equals(CardType.SOCIAL_INSTALL)) {
            RatedRecommendation card = (RatedRecommendation) cardTouchEvent.getCard();
            timelineAnalytics.sendSocialRecommendationClickEvent(card.getType()
                    .name(), Analytics.AppsTimeline.OPEN_APP_VIEW, "(blank)", card.getPackageName(),
                card.getPoster()
                    .getPrimaryName());
            timelineNavigation.navigateToAppView(card.getAppId(), card.getPackageName(),
                AppViewFragment.OpenType.OPEN_ONLY);
          } else if (cardTouchEvent.getCard()
              .getType()
              .equals(CardType.AGGREGATED_SOCIAL_INSTALL)) {
            AggregatedRecommendation card = (AggregatedRecommendation) cardTouchEvent.getCard();
            timelineNavigation.navigateToAppView(card.getAppId(), card.getPackageName(),
                AppViewFragment.OpenType.OPEN_ONLY);
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(articleUrl -> {
        }, throwable -> {
          throwable.printStackTrace();
          crashReport.log(throwable);
        });
  }

  private void sendClickOnMediaHeaderEvent(Media card) {
    if (card.getType()
        .equals(CardType.ARTICLE)) {
      timelineAnalytics.sendOpenBlogEvent(card.getType()
          .name(), card.getMediaTitle(), card.getPublisherLink()
          .getUrl(), card.getRelatedApp()
          .getPackageName());
      timelineAnalytics.sendMediaCardClickEvent(card.getType()
              .name(), card.getMediaTitle(), card.getPublisherName(),
          Analytics.AppsTimeline.OPEN_ARTICLE_HEADER, "(blank)");
    } else if (card.getType()
        .equals(CardType.VIDEO)) {
      timelineAnalytics.sendOpenChannelEvent(card.getType()
          .name(), card.getMediaTitle(), card.getPublisherLink()
          .getUrl(), card.getRelatedApp()
          .getPackageName());
      timelineAnalytics.sendMediaCardClickEvent(card.getType()
              .name(), card.getMediaTitle(), card.getPublisherName(),
          Analytics.AppsTimeline.OPEN_VIDEO_HEADER, "(blank)");
    }
  }

  private void sendClickOnMediaBodyEvent(Media card) {
    if (card.getType()
        .equals(CardType.ARTICLE) || card.getType()
        .equals(CardType.SOCIAL_ARTICLE) || card.getType()
        .equals(CardType.AGGREGATED_SOCIAL_ARTICLE)) {
      timelineAnalytics.sendOpenArticleEvent(card.getType()
          .name(), card.getMediaTitle(), card.getMediaLink()
          .getUrl(), card.getRelatedApp()
          .getPackageName());
      timelineAnalytics.sendMediaCardClickEvent(card.getType()
              .name(), card.getMediaTitle(), card.getPublisherName(),
          Analytics.AppsTimeline.OPEN_ARTICLE, "(blank)");
    } else if (card.getType()
        .equals(CardType.VIDEO) || card.getType()
        .equals(CardType.SOCIAL_VIDEO) || card.getType()
        .equals(CardType.AGGREGATED_SOCIAL_VIDEO)) {
      timelineAnalytics.sendOpenVideoEvent(card.getType()
          .name(), card.getMediaTitle(), card.getMediaLink()
          .getUrl(), card.getRelatedApp()
          .getPackageName());
      timelineAnalytics.sendMediaCardClickEvent(card.getType()
              .name(), card.getMediaTitle(), card.getPublisherName(), Analytics.AppsTimeline.OPEN_VIDEO,
          "(blank)");
    }
  }

  private void navigateToAppView(StoreAppCardTouchEvent cardTouchEvent) {
    timelineNavigation.navigateToAppView(cardTouchEvent.getPackageName(),
        AppViewFragment.OpenType.OPEN_ONLY);
  }

  private void refreshCardsOnPullToRefresh() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.refreshes())
        .flatMapSingle(created -> Single.zip(
            accountManager.isLoggedIn() || userId != null ? timeline.getTimelineStats()
                : timeline.getTimelineLoginPost(), timeline.getCards(),
            (post, posts) -> mergeStatsPostWithPosts(post, posts)))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cards -> showCardsAndHideRefresh(cards))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> {
          throwable.printStackTrace();
          view.showGenericError();
        });
  }

  private void showCardsOnCreate() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .filter(__ -> view.isNewRefresh())
        .doOnNext(created -> view.showProgressIndicator())
        .flatMapSingle(created -> Single.zip(
            accountManager.isLoggedIn() || userId != null ? timeline.getTimelineStats()
                : timeline.getTimelineLoginPost(), timeline.getCards(),
            (post, posts) -> mergeStatsPostWithPosts(post, posts)))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cards -> showCardsAndHideProgress(cards))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> {
          throwable.printStackTrace();
          view.showGenericError();
        });
  }

  @NonNull private List<Post> mergeStatsPostWithPosts(Post post, List<Post> posts) {
    List<Post> postsWithStatsPost = new ArrayList<>();
    postsWithStatsPost.add(post);
    postsWithStatsPost.addAll(posts);
    return postsWithStatsPost;
  }

  private void showMoreCardsAndHideLoadMoreProgress(List<Post> cards) {
    view.hideLoadMoreProgressIndicator();
    view.showMoreCards(cards);
  }

  private void showCardsAndHideProgress(List<Post> cards) {
    view.hideProgressIndicator();
    view.showCards(cards);
  }

  private void showCardsAndHideRefresh(List<Post> cards) {
    view.hideRefresh();
    view.showCards(cards);
  }

  private boolean isMediaPost(Post post) {
    return post.getType()
        .equals(CardType.VIDEO) || post.getType()
        .equals(CardType.ARTICLE) || post.getType()
        .equals(CardType.SOCIAL_ARTICLE) || post.getType()
        .equals(CardType.SOCIAL_VIDEO) || post.getType()
        .equals(CardType.AGGREGATED_SOCIAL_ARTICLE) || post.getType()
        .equals(CardType.AGGREGATED_SOCIAL_VIDEO);
  }

  private boolean isSocialPost(Post post) {
    return post.getType()
        .equals(CardType.SOCIAL_ARTICLE) || post.getType()
        .equals(CardType.SOCIAL_VIDEO) || post.getType()
        .equals(CardType.SOCIAL_STORE) || post.getType()
        .equals(CardType.SOCIAL_RECOMMENDATION) || post.getType()
        .equals(CardType.SOCIAL_INSTALL);
  }

  private boolean isNormalPost(Post post) {
    return post.getType()
        .equals(CardType.RECOMMENDATION) || post.getType()
        .equals(CardType.ARTICLE) || post.getType()
        .equals(CardType.VIDEO) || post.getType()
        .equals(CardType.POPULAR_APP) || post.getType()
        .equals(CardType.STORE) || post.getType()
        .equals(CardType.UPDATE);
  }

  private void navigateToStoreTimeline(SocialHeaderCardTouchEvent socialHeaderCardTouchEvent) {
    if (socialHeaderCardTouchEvent.getStoreName() != null) {
      timelineNavigation.navigateToStoreTimeline(socialHeaderCardTouchEvent.getStoreName(),
          socialHeaderCardTouchEvent.getStoreTheme());
    } else {
      timelineNavigation.navigateToStoreTimeline(socialHeaderCardTouchEvent.getUserId(),
          socialHeaderCardTouchEvent.getStoreTheme());
    }
  }

  private void followStore(long storeId, String storeName) {
    storeRepository.isSubscribed(storeId)
        .first()
        .observeOn(AndroidSchedulers.mainThread())
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
