package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.timeline.AppUpdate;
import cm.aptoide.pt.v8engine.install.InstalledRepository;
import java.util.Set;
import rx.Observable;
import rx.functions.Func1;

public class PostFilter {
  private final PostDuplicateFilter duplicateFilter;
  private final InstalledRepository installedRepository;

  public PostFilter(PostDuplicateFilter duplicateFilter, InstalledRepository installedRepository) {
    this.duplicateFilter = duplicateFilter;
    this.installedRepository = installedRepository;
  }

  public void clear() {
    this.duplicateFilter.clear();
  }

  public Observable<Post> filter(Post post) {
    return Observable.just(post)
        .filter(postItem -> postItem != null)
        .filter(duplicateFilter)
        .flatMap(postItem -> filterInstalledRecommendation(postItem))
        .flatMap(postItem -> filterAlreadyDoneUpdates(postItem));
  }

  private Observable<? extends Post> filterInstalledRecommendation(Post timelineItem) {
    if (timelineItem instanceof cm.aptoide.pt.dataprovider.model.v7.timeline.Recommendation) {
      return installedRepository.isInstalled(
          ((cm.aptoide.pt.dataprovider.model.v7.timeline.Recommendation) timelineItem).getRecommendedApp()
              .getPackageName())
          .firstOrDefault(false)
          .flatMap(installed -> {
            if (!installed) {
              return Observable.just(timelineItem);
            }
            return Observable.empty();
          });
    }
    return Observable.just(timelineItem);
  }

  private Observable<? extends Post> filterAlreadyDoneUpdates(Post post) {
    if (post instanceof cm.aptoide.pt.dataprovider.model.v7.timeline.AppUpdate) {
      return installedRepository.getInstalled(
          ((cm.aptoide.pt.dataprovider.model.v7.timeline.AppUpdate) post).getPackageName())
          .firstOrDefault(null)
          .flatMap(installed -> {
            if (installed != null && installed.getVersionCode() == ((AppUpdate) post).getFile()
                .getVercode()) {
              return Observable.empty();
            }
            return Observable.just(post);
          });
    }
    return Observable.just(post);
  }

  public static class PostDuplicateFilter implements Func1<Post, Boolean> {

    private final Set<String> postIds;

    public PostDuplicateFilter(Set<String> postIds) {
      this.postIds = postIds;
    }

    public void clear() {
      postIds.clear();
    }

    @Override public Boolean call(Post post) {
      return postIds.add(post.getCardId());
    }
  }
}
