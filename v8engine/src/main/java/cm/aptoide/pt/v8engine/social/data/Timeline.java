package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.Install;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.download.DownloadFactory;
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

  public Timeline(TimelineService service, InstallManager installManager,
      DownloadFactory downloadFactory) {
    this.service = service;
    this.installManager = installManager;
    this.downloadFactory = downloadFactory;
  }

  public Single<List<Post>> getCards() {
    return service.getCards();
  }

  public Single<List<Post>> getNextCards() {
    return service.getNextCards();
  }

  public Observable<Install> updateApp(CardTouchEvent cardTouchEvent) {
    AppUpdate card = (AppUpdate) cardTouchEvent.getCard();
    return installManager.install(downloadFactory.create(
        (cm.aptoide.pt.v8engine.social.data.AppUpdate) cardTouchEvent.getCard(),
        Download.ACTION_UPDATE))
        .andThen(installManager.getInstall(card.getFile()
            .getMd5sum(), card.getPackageName(), card.getFile()
            .getVercode()));
  }

  public Completable like(String cardId) {
    return service.like(cardId);
  }

  public Single<Post> getTimelineStats() {
    return service.getTimelineStats();
  }

  public Single<Post> getTimelineLoginPost() {
    return Single.just(new TimelineLoginPost());
  }

  public Single<String> sharePost(Post post) {
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
}
