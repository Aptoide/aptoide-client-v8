package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.v8engine.Install;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.download.DownloadFactory;
import java.util.List;
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

  public Completable like(Post post) {
    return service.like(post.getCardId());
  }

  public Single<Post> getTimelineStats() {
    return service.getTimelineStats();
  }
}
