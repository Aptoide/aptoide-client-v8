package cm.aptoide.pt.social.data;

import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.social.TimelineUserProvider;
import cm.aptoide.pt.timeline.TimelineAnalytics;
import cm.aptoide.pt.timeline.TimelineSocialActionData;
import cm.aptoide.pt.updates.UpdateRepository;
import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class Timeline {
  private final TimelineService service;
  private final InstallManager installManager;
  private final DownloadFactory downloadFactory;
  private final TimelineAnalytics timelineAnalytics;
  private final TimelinePostsRepository timelinePostsRepository;
  private final String marketName;
  private final TimelineUserProvider timelineUserProvider;
  private final UpdateRepository updateRepository;

  public Timeline(TimelineService service, InstallManager installManager,
      DownloadFactory downloadFactory, TimelineAnalytics timelineAnalytics,
      TimelinePostsRepository timelinePostsRepository, String marketName,
      TimelineUserProvider timelineUserProvider, UpdateRepository updateRepository) {
    this.service = service;
    this.installManager = installManager;
    this.downloadFactory = downloadFactory;
    this.timelineAnalytics = timelineAnalytics;
    this.timelinePostsRepository = timelinePostsRepository;
    this.marketName = marketName;
    this.timelineUserProvider = timelineUserProvider;
    this.updateRepository = updateRepository;
  }

  public Single<List<Post>> getCards() {
    return timelinePostsRepository.getCards();
  }

  public Single<List<Post>> getFreshCards() {
    return timelinePostsRepository.getFreshCards();
  }

  public Single<List<Post>> getFreshCards(String postId) {
    return timelinePostsRepository.getFreshCards(postId);
  }

  public Single<List<Post>> getNextCards() {
    return timelinePostsRepository.getNextCards();
  }

  public Observable<Install> updateApp(CardTouchEvent cardTouchEvent) {
    AppUpdate card = (AppUpdate) cardTouchEvent.getCard();
    return installManager.install(
        downloadFactory.create((cm.aptoide.pt.social.data.AppUpdate) cardTouchEvent.getCard(),
            Download.ACTION_UPDATE))
        .andThen(installManager.getInstall(card.getFile()
            .getMd5sum(), card.getPackageName(), card.getFile()
            .getVercode()));
  }

  public Completable like(Post post, String cardId) {
    sendSocialAnalyticsEvent(post, "Like");
    return service.like(cardId);
  }

  private void sendSocialAnalyticsEvent(Post post, String like) {
    String blank = "(blank)";
    if (post instanceof Media) {
      timelineAnalytics.sendSocialActionEvent(new TimelineSocialActionData(post.getType()
          .name(), blank, like, ((Media) post).getRelatedApp()
          .getPackageName(), ((Media) post).getPublisherName(), ((Media) post).getMediaTitle()));
    } else if (post instanceof AppPost) {
      if (post instanceof Recommendation) {
        timelineAnalytics.sendSocialActionEvent(new TimelineSocialActionData(post.getType()
            .name(), blank, like, ((AppPost) post).packageName, marketName, blank));
      } else if (post instanceof RatedRecommendation) {
        timelineAnalytics.sendSocialActionEvent(new TimelineSocialActionData(post.getType()
            .name(), blank, like, ((AppPost) post).packageName,
            ((RatedRecommendation) post).getPoster()
                .getPrimaryName(), blank));
      } else if (post instanceof AggregatedRecommendation) {
        timelineAnalytics.sendSocialActionEvent(new TimelineSocialActionData(post.getType()
            .name(), blank, like, ((AppPost) post).packageName, blank, blank));
      }
    } else if (post instanceof StoreLatestApps) {
      timelineAnalytics.sendSocialActionEvent(new TimelineSocialActionData(post.getType()
          .name(), blank, like, blank, blank, blank));
    } else if (post instanceof AppUpdate) {
      timelineAnalytics.sendSocialActionEvent(new TimelineSocialActionData(post.getType()
          .name(), blank, like, ((AppUpdate) post).getPackageName(),
          ((AppUpdate) post).getStoreName(), blank));
    }
  }

  public Single<String> sharePost(Post post) {
    sendSocialAnalyticsEvent(post, "Share");
    if (post instanceof AppPost) {
      return service.shareApp(post.getCardId(), ((AppPost) post).getStoreId());
    }
    return service.share(post.getCardId());
  }

  public Single<String> sharePost(String cardId) {
    return service.share(cardId);
  }

  public Completable postComment(String cardId, String commentText) {
    return service.postComment(cardId, commentText);
  }

  public void knockWithSixpackCredentials(String url) {
    if (url == null) {
      return;
    }

    String credential = Credentials.basic(BuildConfig.SIXPACK_USER, BuildConfig.SIXPACK_PASSWORD);

    OkHttpClient client = new OkHttpClient();

    Request click = new Request.Builder().url(url)
        .addHeader("authorization", credential)
        .build();

    client.newCall(click)
        .enqueue(new Callback() {
          @Override public void onFailure(Call call, IOException e) {
            Logger.d(this.getClass()
                .getSimpleName(), "sixpack request fail " + call.toString());
          }

          @Override public void onResponse(Call call, Response response) throws IOException {
            Logger.d(this.getClass()
                .getSimpleName(), "sixpack knock success");
            response.body()
                .close();
          }
        });
  }

  public Completable setPostRead(List<Post> posts, CardType postType) {
    return Observable.from(posts)
        .flatMapCompletable(
            post -> timelineAnalytics.setPostRead(post.getCardId(), postType.name()))
        .toCompletable();
  }

  public Completable setPostRead(String markAsReadUrl, String cardId, CardType cardType) {
    if (markAsReadUrl != null && !markAsReadUrl.isEmpty()) {
      return timelineAnalytics.setPostRead(cardId, cardType.name());
    }
    return Completable.complete();
  }

  public Observable<User> getUser(boolean refresh) {
    return timelineUserProvider.getUser(refresh);
  }

  public Completable notificationDismissed(int notificationType) {
    return timelineUserProvider.notificationRead(notificationType);
  }

  public Completable deletePost(String postId) {
    return service.deletePost(postId);
  }

  public Completable unfollowUser(Long userId) {
    return service.unfollowUser(userId);
  }

  public Completable ignoreUpdate(String updatePackageName) {
    return updateRepository.setExcluded(updatePackageName, true)
        .toCompletable();
  }
}

