package cm.aptoide.pt.social.data;

import cm.aptoide.pt.dataprovider.ws.v7.PostRead;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Created by jdandrade on 30/10/2017.
 */

public class TimelineAnalyticsPersistence {
  private final int limit;
  private List<PostRead> postsRead;

  public TimelineAnalyticsPersistence(List<PostRead> postsRead, int limit) {
    this.postsRead = postsRead;
    this.limit = limit;
  }

  public Completable addPostRead(String postId, String postType) {
    PostRead postRead = new PostRead(postId, postType);
    postsRead.add(postRead);
    return Completable.complete();
  }

  public Observable<List<PostRead>> popBatchPostReadList() {
    return Observable.fromCallable(() -> {
      List<PostRead> posts = new ArrayList<>();
      List<PostRead> batch =
          postsRead.subList(0, limit > postsRead.size() ? postsRead.size() : limit);
      posts.addAll(batch);
      return posts;
    })
        .doOnNext(__ -> postsRead.removeAll(__));
  }

  public Observable<Boolean> postReadListEndReached() {
    return Observable.just(postsRead.isEmpty());
  }
}
