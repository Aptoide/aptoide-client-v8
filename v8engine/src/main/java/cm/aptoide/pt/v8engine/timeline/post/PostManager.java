package cm.aptoide.pt.v8engine.timeline.post;

import android.text.TextUtils;
import cm.aptoide.pt.v8engine.timeline.post.PostRemoteAccessor.RelatedApp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.Completable;
import rx.Single;

public class PostManager {

  private final PostAccessor postRemoteRepository;
  private final PostAccessor postLocalRepository;

  public PostManager(PostRemoteAccessor postRemoteRepository, PostAccessor postLocalRepository) {
    this.postRemoteRepository = postRemoteRepository;
    this.postLocalRepository = postLocalRepository;
  }

  public Completable post(String url, String content, String packageName) {
    return postRemoteRepository.postOnTimeline(url, content, packageName);
  }

  public Single<List<RelatedApp>> getAppSuggestions(String url) {

    Single<List<PostRemoteAccessor.RelatedApp>> remoteSuggestions =
        !TextUtils.isEmpty(url) ? postRemoteRepository.getRelatedApps(url)
            .onErrorReturn(throwable -> Collections.emptyList())
            : Single.just(Collections.emptyList());

    Single<List<PostRemoteAccessor.RelatedApp>> installedApps =
        postLocalRepository.getRelatedApps(url);

    return Single.zip(remoteSuggestions, installedApps, (listA, listB) -> {
      ArrayList<RelatedApp> relatedApps = new ArrayList<>(listA.size() + listB.size());
      relatedApps.addAll(listA);
      relatedApps.addAll(listB);
      return relatedApps;
    });
  }

  public Single<PostView.PostPreview> getPreview(String url) {
    return postRemoteRepository.getCardPreview(url);
  }

  enum Origin {
    Installed, Remote, Searched
  }
}
