package cm.aptoide.pt.social.data;

import cm.aptoide.pt.dataprovider.ws.v7.ReadPost;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;
import rx.Single;

/**
 * Created by jdandrade on 30/10/2017.
 */

public class ReadPostsPersistence {
  private List<ReadPost> readPosts;

  public ReadPostsPersistence(List<ReadPost> readPosts) {
    this.readPosts = readPosts;
  }

  public Completable addPost(String postId, String postType) {
    ReadPost readPost = new ReadPost(postId, postType);
    readPosts.add(readPost);
    return Completable.complete();
  }

  public Single<List<ReadPost>> getPosts(int limit) {
    return Single.fromCallable(() -> {
      List<ReadPost> posts = new ArrayList<>();
      List<ReadPost> batch =
          readPosts.subList(0, limit > readPosts.size() ? readPosts.size() : limit);
      posts.addAll(batch);
      return posts;
    });
  }

  public boolean isPostsEmpty() {
    return readPosts.isEmpty();
  }

  public Completable removePosts(List<ReadPost> posts) {
    return Completable.fromAction(() -> readPosts.removeAll(posts));
  }
}
