package cm.aptoide.pt.social.data;

import rx.Single;

/**
 * Created by jdandrade on 01/08/2017.
 */

public class TimelinePostsRepository {

  private final PostsRemoteDataSource postsRemoteDataSource;

  private TimelineModel cachedTimeline;

  public TimelinePostsRepository(PostsRemoteDataSource postsRemoteDataSource) {
    this.postsRemoteDataSource = postsRemoteDataSource;
  }

  public Single<TimelineModel> getCards() {
    if (cachedTimeline != null) {
      return Single.just(
          new TimelineModel(cachedTimeline.getTimelineVersion(), cachedTimeline.getPosts()));
    }
    return postsRemoteDataSource.getTimelineModel()
        .doOnSuccess(timeline -> cachedTimeline = timeline)
        .map(timelineModel -> new TimelineModel(timelineModel.getTimelineVersion(),
            timelineModel.getPosts()));
  }

  public Single<TimelineModel> getFreshCards() {
    return postsRemoteDataSource.getTimelineModel()
        .doOnSuccess(timelineModel -> cachedTimeline = timelineModel);
  }

  public Single<TimelineModel> getFreshCards(String postId) {
    return postsRemoteDataSource.getTimelineModel(postId)
        .doOnSuccess(timelineModel -> cachedTimeline = timelineModel);
  }

  public Single<TimelineModel> getNextCards() {
    return postsRemoteDataSource.getNextCards()
        .doOnSuccess(timelineModel -> cachedTimeline.addPosts(timelineModel.getPosts()));
  }

  public void clearLoading() {
    postsRemoteDataSource.clearLoading();
  }
}
