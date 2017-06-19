package cm.aptoide.pt.v8engine.timeline.post;

import cm.aptoide.pt.logger.Logger;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Single;

public class PostManager {
  public Completable post(String data) {
    return Completable.fromAction(() -> Logger.i("PostManager", "server received post: " + data))
        .delay(1, TimeUnit.SECONDS);
  }

  public Single<String> getSuggestion(String data) {
    return Single.just(data)
        .delay(300, TimeUnit.MILLISECONDS);
  }
}
