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
import cm.aptoide.pt.v8engine.view.app.AppViewFragment;
import java.util.ArrayList;
import java.util.List;
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

  public TimelinePresenter(@NonNull TimelineView cardsView, @NonNull Timeline timeline,
      CrashReport crashReport, TimelineNavigation timelineNavigation,
      PermissionManager permissionManager, PermissionService permissionRequest,
      InstallManager installManager, StoreRepository storeRepository,
      StoreUtilsProxy storeUtilsProxy, StoreCredentialsProviderImpl storeCredentialsProvider,
      AptoideAccountManager accountManager, TimelineAnalytics timelineAnalytics, Long userId,
      Long storeId, StoreContext storeContext, Resources resources) {
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
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private void onCreateShowPosts() {
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
          crashReport.log(throwable);
          view.showGenericViewError();
        });
  }

  private void onPullToRefreshRefreshPosts() {
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
          crashReport.log(throwable);
          view.showGenericViewError();
        });
  }

  private void onBottomReachedShowMorePosts() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(create -> view.reachesBottom())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(create -> view.showLoadMoreProgressIndicator())
        .flatMapSingle(bottomReached -> timeline.getNextCards())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cards -> showMoreCardsAndHideLoadMoreProgress(cards))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cards -> {
        }, throwable -> {
          crashReport.log(throwable);
          view.showGenericError();
        });
  }

  private void onRetryShowPosts() {
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
          crashReport.log(throwable);
          view.showGenericViewError();
        });
  }

  private void clickOnPostHeader() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.HEADER))
        .doOnNext(cardTouchEvent -> {
          timeline.knockWithSixpackCredentials(cardTouchEvent.getCard()
              .getAbUrl());
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
            Analytics.AppsTimeline.clickOnCard(socialHeaderCardTouchEvent.getCard()
                    .getType()
                    .name(), Analytics.AppsTimeline.BLANK, Analytics.AppsTimeline.BLANK,
                socialHeaderCardTouchEvent.getStoreName(), Analytics.AppsTimeline.OPEN_STORE);
            navigateToStoreTimeline(socialHeaderCardTouchEvent);
          } else if (cardTouchEvent.getCard()
              .getType()
              .equals(CardType.STORE)) {
            StoreLatestApps card = ((StoreLatestApps) cardTouchEvent.getCard());
            Analytics.AppsTimeline.clickOnCard(card.getType()
                    .name(), Analytics.AppsTimeline.BLANK, Analytics.AppsTimeline.BLANK,
                card.getStoreName(), Analytics.AppsTimeline.OPEN_STORE);
            timelineAnalytics.sendStoreLatestAppsClickEvent(card.getType()
                    .name(), Analytics.AppsTimeline.OPEN_STORE, "(blank)", Analytics.AppsTimeline.BLANK,
                card.getStoreName());
            timelineNavigation.navigateToStoreHome(card.getStoreName(), card.getStoreTheme());
          } else if (cardTouchEvent.getCard()
              .getType()
              .equals(CardType.UPDATE)) {
            AppUpdate card = ((AppUpdate) cardTouchEvent.getCard());
            Analytics.AppsTimeline.clickOnCard(card.getType()
                    .name(), card.getPackageName(), Analytics.AppsTimeline.BLANK, card.getStoreName(),
                Analytics.AppsTimeline.OPEN_STORE);
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
            Analytics.AppsTimeline.clickOnCard(popularAppTouchEvent.getCard()
                    .getType()
                    .name(), ((PopularApp) popularAppTouchEvent.getCard()).getPackageName(),
                Analytics.AppsTimeline.BLANK, String.valueOf(popularAppTouchEvent.getUserId()),
                Analytics.AppsTimeline.OPEN_STORE);
            timelineAnalytics.sendPopularAppOpenUserStoreEvent(cardTouchEvent.getCard()
                    .getType()
                    .name(), TimelineAnalytics.SOURCE_APTOIDE,
                ((PopularApp) popularAppTouchEvent.getCard()).getPackageName(),
                String.valueOf(popularAppTouchEvent.getUserId()));
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
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.BODY))
        .doOnNext(cardTouchEvent -> {
          timeline.knockWithSixpackCredentials(cardTouchEvent.getCard()
              .getAbUrl());
          if (isMediaPost(cardTouchEvent.getCard())) {
            Media card = (Media) cardTouchEvent.getCard();
            sendClickOnMediaBodyEvent(card);
            card.getMediaLink()
                .launch();
          } else if (cardTouchEvent.getCard()
              .getType()
              .equals(CardType.RECOMMENDATION)) {
            Recommendation card = (Recommendation) cardTouchEvent.getCard();
            Analytics.AppsTimeline.clickOnCard(card.getType()
                    .name(), card.getPackageName(), Analytics.AppsTimeline.BLANK,
                card.getPublisherName(), Analytics.AppsTimeline.OPEN_APP_VIEW);
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
            if (storeAppCardTouchEvent.getCard() instanceof StoreLatestApps) {
              Analytics.AppsTimeline.clickOnCard(storeAppCardTouchEvent.getCard()
                      .getType()
                      .name(), storeAppCardTouchEvent.getPackageName(), Analytics.AppsTimeline.BLANK,
                  ((StoreLatestApps) storeAppCardTouchEvent.getCard()).getStoreName(),
                  Analytics.AppsTimeline.OPEN_APP_VIEW);
            }
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
              Analytics.AppsTimeline.clickOnCard(cardTouchEvent.getCard()
                      .getType()
                      .name(), ((StoreAppCardTouchEvent) cardTouchEvent).getPackageName(),
                  Analytics.AppsTimeline.BLANK,
                  ((StoreLatestApps) cardTouchEvent.getCard()).getStoreName(),
                  Analytics.AppsTimeline.OPEN_APP_VIEW);
              navigateToAppView((StoreAppCardTouchEvent) cardTouchEvent);
            } else if (cardTouchEvent instanceof FollowStoreCardTouchEvent) {
              FollowStoreCardTouchEvent followStoreCardTouchEvent =
                  ((FollowStoreCardTouchEvent) cardTouchEvent);
              followStore(followStoreCardTouchEvent.getStoreId(),
                  followStoreCardTouchEvent.getStoreName());
            } else if (cardTouchEvent instanceof StoreCardTouchEvent) {
              StoreCardTouchEvent storeCardTouchEvent = (StoreCardTouchEvent) cardTouchEvent;
              if (cardTouchEvent.getCard() instanceof StoreLatestApps) {
                Analytics.AppsTimeline.clickOnCard(cardTouchEvent.getCard()
                        .getType()
                        .name(), Analytics.AppsTimeline.BLANK, Analytics.AppsTimeline.BLANK,
                    ((StoreLatestApps) cardTouchEvent.getCard()).getStoreName(),
                    Analytics.AppsTimeline.OPEN_STORE);
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
            if (cardTouchEvent instanceof AppUpdateCardTouchEvent) {
              Analytics.AppsTimeline.clickOnCard(cardTouchEvent.getCard()
                      .getType()
                      .name(), card.getPackageName(), Analytics.AppsTimeline.BLANK, card.getStoreName(),
                  Analytics.AppsTimeline.UPDATE_APP);
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
                    ((AppUpdate) cardTouchEvent.getCard()).setInstallationStatus(
                        install.getState());
                    view.updateInstallProgress(cardTouchEvent.getCard(),
                        ((AppUpdateCardTouchEvent) cardTouchEvent).getCardPosition());
                  })
                  .subscribe(downloadProgress -> {
                  }, throwable -> Logger.d(this.getClass()
                      // TODO: 26/06/2017 error handling
                      .getName(), "error"));
            } else {
              Analytics.AppsTimeline.clickOnCard(card.getType()
                      .name(), card.getPackageName(), Analytics.AppsTimeline.BLANK, card.getStoreName(),
                  Analytics.AppsTimeline.OPEN_APP_VIEW);
              timelineAnalytics.sendRecommendationCardClickEvent(card.getType()
                      .name(), Analytics.AppsTimeline.OPEN_APP_VIEW, Analytics.AppsTimeline.BLANK,
                  card.getPackageName(), card.getStoreName());
              timelineAnalytics.sendRecommendedOpenAppEvent(card.getType()
                      .name(), TimelineAnalytics.SOURCE_APTOIDE, Analytics.AppsTimeline.BLANK,
                  card.getPackageName());
              timelineNavigation.navigateToAppView(card.getAppUpdateId(), card.getPackageName(),
                  AppViewFragment.OpenType.OPEN_ONLY);
            }
          } else if (cardTouchEvent.getCard()
              .getType()
              .equals(CardType.POPULAR_APP)) {
            PopularApp card = (PopularApp) cardTouchEvent.getCard();
            Analytics.AppsTimeline.clickOnCard(cardTouchEvent.getCard()
                    .getType()
                    .name(), card.getPackageName(), Analytics.AppsTimeline.BLANK,
                Analytics.AppsTimeline.BLANK, Analytics.AppsTimeline.OPEN_APP_VIEW);
            timelineNavigation.navigateToAppView(card.getAppId(), card.getPackageName(),
                AppViewFragment.OpenType.OPEN_ONLY);
          } else if (cardTouchEvent.getCard()
              .getType()
              .equals(CardType.SOCIAL_RECOMMENDATION) || cardTouchEvent.getCard()
              .getType()
              .equals(CardType.SOCIAL_INSTALL)) {
            RatedRecommendation card = (RatedRecommendation) cardTouchEvent.getCard();
            Analytics.AppsTimeline.clickOnCard(cardTouchEvent.getCard()
                    .getType()
                    .name(), card.getPackageName(), Analytics.AppsTimeline.BLANK,
                Analytics.AppsTimeline.BLANK, Analytics.AppsTimeline.OPEN_APP_VIEW);
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
            Analytics.AppsTimeline.clickOnCard(cardTouchEvent.getCard()
                    .getType()
                    .name(), card.getPackageName(), Analytics.AppsTimeline.BLANK,
                Analytics.AppsTimeline.BLANK, Analytics.AppsTimeline.OPEN_APP_VIEW);
            timelineNavigation.navigateToAppView(card.getAppId(), card.getPackageName(),
                AppViewFragment.OpenType.OPEN_ONLY);
          }
        })
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
                .equals(CardTouchEvent.Type.LIKE) && isSocialPost(cardTouchEvent.getCard()))
            .flatMapCompletable(cardTouchEvent -> timeline.like(cardTouchEvent.getCard(),
                cardTouchEvent.getCard()
                    .getCardId())))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> timeline.knockWithSixpackCredentials(cardTouchEvent.getCard()
            .getAbUrl()), throwable -> crashReport.log(throwable));
  }

  private void clickOnLikeNonSocialPost() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked()
            .filter(cardTouchEvent -> cardTouchEvent.getActionType()
                .equals(CardTouchEvent.Type.LIKE) && isNormalPost(cardTouchEvent.getCard()))
            .flatMapCompletable(cardTouchEvent -> timeline.sharePost(cardTouchEvent.getCard())
                .flatMapCompletable(cardId -> timeline.like(cardTouchEvent.getCard(), cardId))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> view.showShareSuccessMessage()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> timeline.knockWithSixpackCredentials(cardTouchEvent.getCard()
            .getAbUrl()), throwable -> crashReport.log(throwable));
  }

  private void clickOnCommentSocialPost() {
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
        .subscribe(cardTouchEvent -> timeline.knockWithSixpackCredentials(cardTouchEvent.getCard()
            .getAbUrl()), throwable -> crashReport.log(throwable));
  }

  private void clickOnCommentNonSocialPost() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.COMMENT) && (isNormalPost(cardTouchEvent.getCard())))
        .doOnNext(cardTouchEvent -> view.showCommentDialog(cardTouchEvent.getCard()
            .getCardId()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> timeline.knockWithSixpackCredentials(cardTouchEvent.getCard()
            .getAbUrl()), throwable -> {
        });
  }

  private void clickOnCommentsNumberLabel() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(create -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.COMMENT_NUMBER))
        .doOnNext(cardTouchEvent -> timelineNavigation.navigateToComments(cardTouchEvent.getCard()
            .getCardId()))
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
        .doOnNext(cardTouchEvent -> view.showSharePreview(cardTouchEvent.getCard()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> timeline.knockWithSixpackCredentials(cardTouchEvent.getCard()
            .getAbUrl()), throwable -> {
          crashReport.log(throwable);
          view.showGenericError();
        });
  }

  private void commentPostResponse() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.commentPosted())
        .flatMapCompletable((comment) -> timeline.sharePost(comment.getCardId())
            .flatMapCompletable(
                responseCardId -> timeline.postComment(responseCardId, comment.getCommentText()))
            .doOnCompleted(() -> view.showCommentSuccess()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> {
          crashReport.log(throwable);
          view.showGenericError();
        });
  }

  private void sharePostConfirmation() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.shareConfirmation()
            .flatMapSingle(post -> timeline.sharePost(post)
                .doOnSuccess(cardId -> view.showShareSuccessMessage())))
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
          crashReport.log(throwable);
          view.showGenericError();
        });
  }

  private void clickOnLikesPreview() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.postClicked())
        .filter(cardTouchEvent -> cardTouchEvent.getActionType()
            .equals(CardTouchEvent.Type.LIKES_PREVIEW))
        .doOnNext(cardTouchEvent -> timelineNavigation.navigateToLikesView(cardTouchEvent.getCard()
            .getCardId(), ((LikesCardTouchEvent) cardTouchEvent).getLikesNumber()))
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
        .doOnNext(cardTouchEvent -> timelineNavigation.navigateToAccountView())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> {
        }, throwable -> {
          crashReport.log(throwable);
          view.showGenericError();
        });
  }

  @NonNull private List<Post> mergeStatsPostWithPosts(Post post, List<Post> posts) {
    List<Post> postsWithStatsPost = new ArrayList<>();
    postsWithStatsPost.add(post);
    postsWithStatsPost.addAll(posts);
    return postsWithStatsPost;
  }

  private void showCardsAndHideProgress(List<Post> cards) {
    view.hideProgressIndicator();
    view.showCards(cards);
  }

  private void showCardsAndHideRefresh(List<Post> cards) {
    view.hideRefresh();
    view.showCards(cards);
  }

  private void showMoreCardsAndHideLoadMoreProgress(List<Post> cards) {
    view.hideLoadMoreProgressIndicator();
    view.showMoreCards(cards);
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
      Analytics.AppsTimeline.clickOnCard(card.getType()
              .name(), Analytics.AppsTimeline.BLANK, card.getMediaTitle(), card.getPublisherName(),
          Analytics.AppsTimeline.OPEN_ARTICLE_HEADER);
    } else if (card.getType()
        .equals(CardType.VIDEO)) {
      timelineAnalytics.sendOpenChannelEvent(card.getType()
          .name(), card.getMediaTitle(), card.getPublisherLink()
          .getUrl(), card.getRelatedApp()
          .getPackageName());
      timelineAnalytics.sendMediaCardClickEvent(card.getType()
              .name(), card.getMediaTitle(), card.getPublisherName(),
          Analytics.AppsTimeline.OPEN_VIDEO_HEADER, "(blank)");
      Analytics.AppsTimeline.clickOnCard(card.getType()
              .name(), Analytics.AppsTimeline.BLANK, card.getMediaTitle(), card.getPublisherName(),
          Analytics.AppsTimeline.OPEN_VIDEO_HEADER);
    }
  }

  private boolean isSocialPost(Post post) {
    return post.getType()
        .equals(CardType.SOCIAL_ARTICLE) || post.getType()
        .equals(CardType.SOCIAL_VIDEO) || post.getType()
        .equals(CardType.SOCIAL_STORE) || post.getType()
        .equals(CardType.SOCIAL_RECOMMENDATION) || post.getType()
        .equals(CardType.SOCIAL_INSTALL);
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

  private boolean isMediaPost(Post post) {
    return post.getType()
        .equals(CardType.VIDEO) || post.getType()
        .equals(CardType.ARTICLE) || post.getType()
        .equals(CardType.SOCIAL_ARTICLE) || post.getType()
        .equals(CardType.SOCIAL_VIDEO) || post.getType()
        .equals(CardType.AGGREGATED_SOCIAL_ARTICLE) || post.getType()
        .equals(CardType.AGGREGATED_SOCIAL_VIDEO);
  }

  private void sendClickOnMediaBodyEvent(Media card) {
    if (card.getType()
        .equals(CardType.ARTICLE) || card.getType()
        .equals(CardType.SOCIAL_ARTICLE) || card.getType()
        .equals(CardType.AGGREGATED_SOCIAL_ARTICLE)) {
      Analytics.AppsTimeline.clickOnCard(card.getType()
              .name(), Analytics.AppsTimeline.BLANK, card.getMediaTitle(), card.getPublisherName(),
          Analytics.AppsTimeline.OPEN_ARTICLE);
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
      Analytics.AppsTimeline.clickOnCard(card.getType()
              .name(), Analytics.AppsTimeline.BLANK, card.getMediaTitle(), card.getPublisherName(),
          Analytics.AppsTimeline.OPEN_VIDEO);
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

  private boolean isNormalPost(Post post) {
    return post.getType()
        .equals(CardType.RECOMMENDATION) || post.getType()
        .equals(CardType.ARTICLE) || post.getType()
        .equals(CardType.VIDEO) || post.getType()
        .equals(CardType.POPULAR_APP) || post.getType()
        .equals(CardType.STORE) || post.getType()
        .equals(CardType.UPDATE);
  }
}
