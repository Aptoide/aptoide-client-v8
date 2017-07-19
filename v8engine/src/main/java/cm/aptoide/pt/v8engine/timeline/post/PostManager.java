package cm.aptoide.pt.v8engine.timeline.post;

import android.support.annotation.CheckResult;
import cm.aptoide.pt.v8engine.timeline.post.PostRemoteAccessor.RelatedApp;
import cm.aptoide.pt.v8engine.timeline.post.exceptions.InvalidPostDataException;
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
    return validateInsertedText(content, packageName).flatMapCompletable(
        validPost -> postRemoteRepository.postOnTimeline(addProtocolIfNeeded(url), content,
            packageName));
  }

  private Single<Boolean> validateInsertedText(String textToShare, String packageName) {
    if (textToShare == null || textToShare.isEmpty()) {
      return Single.error(
          new InvalidPostDataException(InvalidPostDataException.ErrorCode.INVALID_TEXT));
    } else if (packageName == null || packageName.isEmpty()) {
      return Single.error(
          new InvalidPostDataException(InvalidPostDataException.ErrorCode.INVALID_PACKAGE));
    }
    return Single.just(true);
  }

  public Single<List<RelatedApp>> getLocalAppSuggestions() {
    return postLocalRepository.getRelatedApps(null);
  }

  public Single<List<RelatedApp>> getRemoteAppSuggestions(String url) {
    return postRemoteRepository.getRelatedApps(addProtocolIfNeeded(url));
  }

  @CheckResult private String addProtocolIfNeeded(String url) {
    if (url != null && !url.contains("http://") && !url.contains("https://")) {
      url = "http://".concat(url);
    }
    return url;
  }

  public Single<PostView.PostPreview> getPreview(String url) {
    return postRemoteRepository.getCardPreview(addProtocolIfNeeded(url))
        .onErrorReturn(throwable -> new PostView.PostPreview(null, url));
  }

  enum Origin {
    Installed, Remote, Searched
  }
}
