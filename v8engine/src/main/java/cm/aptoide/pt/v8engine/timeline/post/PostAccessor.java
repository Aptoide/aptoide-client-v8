package cm.aptoide.pt.v8engine.timeline.post;

import java.util.List;
import rx.Completable;
import rx.Single;

interface PostAccessor {
  Completable postOnTimeline(String url, String content, String packageName);

  Single<List<PostRemoteAccessor.RelatedApp>> getRelatedApps(String url);

  Single<PostView.PostPreview> getCardPreview(String url);
}
