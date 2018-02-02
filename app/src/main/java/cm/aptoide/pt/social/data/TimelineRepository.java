package cm.aptoide.pt.social.data;

import rx.Single;

/**
 * Created by jdandrade on 01/08/2017.
 */

public class TimelineRepository {

  private final TimelineRemoteDataSource timelineRemoteDataSource;

  private TimelineModel cachedTimeline;

  public TimelineRepository(TimelineRemoteDataSource timelineRemoteDataSource) {
    this.timelineRemoteDataSource = timelineRemoteDataSource;
  }

  public Single<TimelineModel> getTimeline() {
    if (cachedTimeline != null) {
      return Single.just(
          new TimelineModel(cachedTimeline.getTimelineVersion(), cachedTimeline.getPosts()));
    }
    return timelineRemoteDataSource.getTimeline()
        .doOnSuccess(timeline -> cachedTimeline = timeline)
        .map(timelineModel -> new TimelineModel(timelineModel.getTimelineVersion(),
            timelineModel.getPosts()));
  }

  public Single<TimelineModel> getFreshTimeline() {
    return timelineRemoteDataSource.getTimeline()
        .doOnSuccess(timelineModel -> cachedTimeline = timelineModel);
  }

  public Single<TimelineModel> getFreshTimeline(String postId) {
    return timelineRemoteDataSource.getTimeline(postId)
        .doOnSuccess(timelineModel -> cachedTimeline = timelineModel);
  }

  public Single<TimelineModel> getNextTimelinePage() {
    return timelineRemoteDataSource.getNextTimelinePage()
        .doOnSuccess(timelineModel -> cachedTimeline.addPosts(timelineModel.getPosts()));
  }

  public void clearLoading() {
    timelineRemoteDataSource.clearLoading();
  }

  public boolean hasMore() {
    return timelineRemoteDataSource.hasMorePosts();
  }
}
