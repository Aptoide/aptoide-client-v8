package cm.aptoide.pt.v8engine.timeline.post;

import cm.aptoide.pt.model.v7.timeline.SocialCard;
import java.util.List;
import rx.Single;

interface PostAccessor {
  Single<SocialCard> postOnTimeline(String url, String content, String packageName);

  Single<List<PostRemoteAccessor.RelatedApp>> getRelatedApps(String url);

  Single<PostView.PostPreview> getCardPreview(String url);
}
