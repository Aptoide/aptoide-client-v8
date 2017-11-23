package cm.aptoide.pt.social.data;

import rx.Single;

/**
 * Created by jdandrade on 01/08/2017.
 */

public class TimelinePostsRepository {

  private final TimelineRemoteDataSource timelineRemoteDataSource;

  private TimelineModel cachedTimeline;

  public TimelinePostsRepository(TimelineRemoteDataSource timelineRemoteDataSource) {
    this.timelineRemoteDataSource = timelineRemoteDataSource;
  }

  public Single<TimelineModel> getCards() {
    if (cachedTimeline != null) {
      return Single.just(
          new TimelineModel(cachedTimeline.getTimelineVersion(), cachedTimeline.getPosts()));
    }
    return timelineRemoteDataSource.getTimelineModel()
        .doOnSuccess(timeline -> cachedTimeline = timeline)
        .map(timelineModel -> new TimelineModel(timelineModel.getTimelineVersion(),
            timelineModel.getPosts()));
  }

  public Single<TimelineModel> getFreshCards() {
    return timelineRemoteDataSource.getTimelineModel()
        .doOnSuccess(timelineModel -> cachedTimeline = timelineModel);
  }

  public Single<TimelineModel> getFreshCards(String postId) {
    return timelineRemoteDataSource.getTimelineModel(postId)
        .doOnSuccess(timelineModel -> cachedTimeline = timelineModel);
  }

  public Single<TimelineModel> getNextCards() {
    return timelineRemoteDataSource.getNextCards()
        .doOnSuccess(timelineModel -> cachedTimeline.addPosts(timelineModel.getPosts()));
  }

  public void clearLoading() {
    timelineRemoteDataSource.clearLoading();
  }
}
