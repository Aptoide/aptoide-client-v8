package cm.aptoide.pt.social.presenter;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.InstallManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialCard;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.repository.StoreRepository;
import cm.aptoide.pt.social.data.AggregatedRecommendation;
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
import cm.aptoide.pt.social.data.TimelineStatsPost;
import cm.aptoide.pt.social.data.TimelineStatsTouchEvent;
import cm.aptoide.pt.social.view.TimelineView;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.timeline.post.PostFragment;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.app.AppViewFragment;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    handlePostNavigation();
  }

  @Override public void saveState(Bundle state) {
  }

  @Override public void restoreState(Bundle state) {

  }

  private void handlePostNavigation() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> timelineNavigation.postNavigation()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(__ -> view.showPostProgressIndicator())
            .flatMapSingle(cardId -> Single.zip(
                accountManager.isLoggedIn() || userId != null ? timeline.getTimelineStats()
                    : timeline.getTimelineLoginPost(), timeline.getFreshCards(cardId),
                (post, posts) -> mergeStatsPostWithPosts(post, posts)))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(cards -> showCardsAndHidePostProgress(cards))
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
            .doOnNext(__2 -> fragmentNavigator.navigateTo(PostFragment.newInstanceFromTimeline())))
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
        .flatMapSingle(account -> Single.zip(
            account.isLoggedIn() || userId != null ? timeline.getTimelineStats()
                : timeline.getTimelineLoginPost(), timeline.getCards(),
            (statisticsPost, posts) -> mergeStatsPostWithPosts(statisticsPost, posts)))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cards -> {
          if (cards != null && cards.size() > 0) {
            showCardsAndHideProgress(cards);
          } else {
            view.showGenericViewError();
          }
        })
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
        .flatMap(created -> view.refreshes()
            .flatMapSingle(__ -> accountManager.accountStatus()
                .first()
                .toSingle())
            .observeOn(Schedulers.io())
            .flatMapSingle(account -> Single.zip(
                account.isLoggedIn() || userId != null ? timeline.getTimelineStats()
                    : timeline.getTimelineLoginPost(), timeline.getFreshCards(),
                (post, posts) -> mergeStatsPostWithPosts(post, posts)))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(cards -> showCardsAndHideRefresh(cards))
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
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(created -> view.showLoadMoreProgressIndicator())
            .flatMapSingle(bottomReached -> timeline.getNextCards())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(cards -> showMoreCardsAndHideLoadMoreProgress(cards))
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
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(__ -> view.retry()
            .doOnNext(__2 -> view.showGeneralProgressIndicator())
            .flatMapSingle(__3 -> accountManager.accountStatus()
                .first()
                .toSingle())
            .observeOn(Schedulers.io())
            .flatMapSingle(account -> Single.zip(
                account.isLoggedIn() || userId != null ? timeline.getTimelineStats()
                    : timeline.getTimelineLoginPost(), timeline.getCards(),
                (statisticsPost, posts) -> mergeStatsPostWithPosts(statisticsPost, posts)))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(posts -> {
              if (posts != null && posts.size() > 0) {
                showCardsAndHideProgress(posts);
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
                  timelineNavigation.navigateToAppView(card.getAppId(), card.getPackageName(),
                      AppViewFragment.OpenType.OPEN_ONLY);
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
                        .observeOn(AndroidSchedulers.mainThread())
                        .distinctUntilChanged(install -> install.getState())
                        .doOnNext(install -> {
                          // TODO: 26/06/2017 get this logic out of here?  this is not working properly yet
                          ((AppUpdate) post).setInstallationStatus(install.getState());
                          view.swapPost(post,
                              ((AppUpdateCardTouchEvent) cardTouchEvent).getCardPosition());
                        })
                        .subscribe(downloadProgress -> {
                        }, throwable -> Logger.d(this.getClass()
                            // TODO: 26/06/2017 error handling
                            .getName(), "error"));
                  } else {
                    timelineNavigation.navigateToAppView(card.getAppUpdateId(),
                        card.getPackageName(), AppViewFragment.OpenType.OPEN_ONLY);
                  }
                } else if (type.equals(CardType.POPULAR_APP)) {
                  PopularApp card = (PopularApp) post;
                  timelineNavigation.navigateToAppView(card.getAppId(), card.getPackageName(),
                      AppViewFragment.OpenType.OPEN_ONLY);
                } else if (type.equals(CardType.SOCIAL_RECOMMENDATION) || type.equals(
                    CardType.SOCIAL_INSTALL) || type.equals(CardType.SOCIAL_POST_RECOMMENDATION)) {
                  RatedRecommendation card = (RatedRecommendation) post;
                  timelineNavigation.navigateToAppView(card.getAppId(), card.getPackageName(),
                      AppViewFragment.OpenType.OPEN_ONLY);
                } else if (type.equals(CardType.AGGREGATED_SOCIAL_INSTALL)) {
                  AggregatedRecommendation card = (AggregatedRecommendation) post;
                  timelineNavigation.navigateToAppView(card.getAppId(), card.getPackageName(),
                      AppViewFragment.OpenType.OPEN_ONLY);
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
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(account -> {
                  if (!account.isLoggedIn()) {
                    view.showLoginPromptWithAction();
                  } else {
                    if (showCreateStore(account)) {
                      view.showCreateStoreMessage(SocialAction.LIKE);
                    } else {
                      cardTouchEvent.getCard()
                          .setLiked(true);
                      view.updatePost(((SocialCardTouchEvent) cardTouchEvent).getPostPosition());
                    }
                  }
                })
                .flatMapCompletable(account -> {
                  if (account.isLoggedIn()) {
                    if (showCreateStore(account)) {
                      return Completable.complete();
                    }

                    final Post post = cardTouchEvent.getCard();
                    return timeline.like(post, post.getCardId());
                  }
                  return Completable.complete();
                })))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(cardTouchEvent -> timeline.knockWithSixpackCredentials(cardTouchEvent.getCard()
            .getAbUrl()), throwable -> crashReport.log(throwable));
  }

  private boolean showCreateStore(Account account) {
    // user is private and does not have a store
    return account != null && !account.isPublicUser() && !account.hasStore();
  }

  private void handleLoginMessageClick() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.loginActionClick())
        .observeOn(AndroidSchedulers.mainThread())
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
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(account -> {
                  if (!account.isLoggedIn()) {
                    view.showLoginPromptWithAction();
                  } else {
                    if (showCreateStore(account)) {
                      view.showCreateStoreMessage(SocialAction.LIKE);
                    } else {
                      cardTouchEvent.getCard()
                          .setLiked(true);
                      view.updatePost(((SocialCardTouchEvent) cardTouchEvent).getPostPosition());
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
                        .flatMapCompletable(cardId -> timeline.like(post, cardId));
                  } else {
                    return Completable.complete();
                  }
                }))
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
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable(account -> {
              if (account.isLoggedIn()) {
                if (showCreateStore(account)) {
                  return Completable.fromAction(
                      () -> view.showCreateStoreMessage(SocialAction.LIKE));
                }
                return Completable.fromAction(
                    () -> timelineNavigation.navigateToCommentsWithCommentDialogOpen(
                        cardTouchEvent.getCard()
                            .getCardId()));
              }
              return Completable.fromAction(() -> view.showLoginPromptWithAction());
            }))
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
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable(account -> {
              if (account.isLoggedIn()) {
                if (showCreateStore(account)) {
                  return Completable.fromAction(
                      () -> view.showCreateStoreMessage(SocialAction.LIKE));
                }
                return Completable.fromAction(
                    () -> view.showCommentDialog((SocialCardTouchEvent) cardTouchEvent));
              }
              return Completable.fromAction(() -> view.showLoginPromptWithAction());
            }))
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
        .flatMapCompletable(cardTouchEvent -> accountManager.accountStatus()
            .first()
            .toSingle()
            .observeOn(AndroidSchedulers.mainThread())
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
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable(account -> {
              if (account.isLoggedIn()) {
                if (showCreateStore(account)) {
                  return Completable.fromAction(
                      () -> view.showCreateStoreMessage(SocialAction.LIKE));
                }
                if (cardTouchEvent instanceof MinimalPostTouchEvent) {
                  return Completable.fromAction(() -> view.showSharePreview(
                      ((MinimalPostTouchEvent) cardTouchEvent).getOriginalPost(),
                      (cardTouchEvent).getCard(), account));
                }
                return Completable.fromAction(
                    () -> view.showSharePreview(cardTouchEvent.getCard(), account));
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

  private void commentPostResponse() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.commentPosted())
        .flatMapCompletable(comment -> timeline.sharePost(comment.getPost()
            .getCardId())
            .flatMapCompletable(responseCardId -> accountManager.accountStatus()
                .first()
                .toSingle()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapCompletable(account -> {
                  comment.getPost()
                      .addComment(new SocialCard.CardComment(-1, comment.getCommentText(),
                          account.getNickname(), account.getAvatar()));
                  return Completable.fromAction(() -> view.updatePost(comment.getPostPosition()));
                })
                .andThen(timeline.postComment(responseCardId, comment.getCommentText()))))
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
            .getCardId(), ((LikesPreviewCardTouchEvent) cardTouchEvent).getLikesNumber()))
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

  @NonNull private List<Post> mergeStatsPostWithPosts(Post post, List<Post> posts) {
    List<Post> postsWithStatsPost = new ArrayList<>();
    postsWithStatsPost.add(post);
    postsWithStatsPost.addAll(posts);
    return postsWithStatsPost;
  }

  private void showCardsAndHideProgress(List<Post> cards) {
    view.hideGeneralProgressIndicator();
    view.showCards(cards);
  }

  private void showCardsAndHidePostProgress(List<Post> cards) {
    view.hidePostProgressIndicator();
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

  private void navigateToStoreTimeline(SocialHeaderCardTouchEvent socialHeaderCardTouchEvent) {
    if (socialHeaderCardTouchEvent.getStoreName() != null) {
      timelineNavigation.navigateToStoreTimeline(socialHeaderCardTouchEvent.getStoreName(),
          socialHeaderCardTouchEvent.getStoreTheme());
    } else {
      timelineNavigation.navigateToStoreTimeline(socialHeaderCardTouchEvent.getUserId(),
          socialHeaderCardTouchEvent.getStoreTheme());
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
}
