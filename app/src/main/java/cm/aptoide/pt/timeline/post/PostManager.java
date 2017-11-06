package cm.aptoide.pt.timeline.post;

import android.support.annotation.CheckResult;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.timeline.post.PostRemoteAccessor.RelatedApp;
import cm.aptoide.pt.timeline.post.exceptions.PostException;
import java.util.List;
import rx.Single;

public class PostManager {

  private final PostAccessor postRemoteRepository;
  private final PostAccessor postLocalRepository;
  private AptoideAccountManager accountManager;
  private boolean remoteRelatedAppsAvailable = false;

  PostManager(PostRemoteAccessor postRemoteRepository, PostAccessor postLocalRepository,
      AptoideAccountManager accountManager) {
    this.postRemoteRepository = postRemoteRepository;
    this.postLocalRepository = postLocalRepository;
    this.accountManager = accountManager;
  }

  public Single<String> post(String url, String content, String packageName) {
    return validateLogin().flatMap(__ -> hasValidDescriptionAndPackage(content, packageName, url))
        .flatMap(__ -> postRemoteRepository.postOnTimeline(addProtocolIfNeeded(url),
            getContent(url, content), packageName))
        .onErrorResumeNext(this::handleErrors);
  }

  private Single handleErrors(Throwable throwable) {
    if (AptoideWsV7Exception.class.isAssignableFrom(throwable.getClass())) {
      final AptoideWsV7Exception wsV7Exception = (AptoideWsV7Exception) throwable;
      final String errorCode = wsV7Exception.getBaseResponse()
          .getError()
          .getCode();
      switch (errorCode) {
        default:
        case Errors.APP_NOT_FOUND_ERROR_CODE:
          return Single.error(new PostException(PostException.ErrorCode.NO_APP_FOUND));
        case Errors.USER_TIMELINECARD_1:
          return Single.error(new PostException(PostException.ErrorCode.INVALID_URL));
        case Errors.USER_TIMELINECARD_2:
          return Single.error(new PostException(PostException.ErrorCode.INVALID_URL));
      }
    }
    return Single.error(throwable);
  }

  private String getContent(String url, String content) {
    return content.replace(url + " ", "");
  }

  private Single<Boolean> validateLogin() {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMap(account -> {
          if (account.isLoggedIn()) {
            return Single.just(true);
          } else {
            return Single.error(new PostException(PostException.ErrorCode.NO_LOGIN));
          }
        });
  }

  private Single<Boolean> hasValidDescriptionAndPackage(String textToShare, String packageName,
      String url) {
    if ((textToShare == null || textToShare.isEmpty()) && (url == null || url.isEmpty())) {
      return Single.error(new PostException(PostException.ErrorCode.INVALID_TEXT));
    } else if (packageName == null || packageName.isEmpty()) {
      return Single.error(new PostException(PostException.ErrorCode.INVALID_PACKAGE));
    }
    return Single.just(true);
  }

  public Single<List<RelatedApp>> getSuggestionApps() {
    return postLocalRepository.getRelatedApps(null);
  }

  public Single<List<RelatedApp>> getSuggestionApps(String url) {
    return postRemoteRepository.getRelatedApps(addProtocolIfNeeded(url));
  }

  @CheckResult private String addProtocolIfNeeded(String url) {
    if (url != null && !url.contains("http://") && !url.contains("https://")) {
      url = "http://".concat(url);
    }
    return url;
  }

  Single<PostPreview> getPreview(String url) {
    return postRemoteRepository.getCardPreview(addProtocolIfNeeded(url))
        .onErrorReturn(throwable -> new PostPreview(null, null, url));
  }

  boolean remoteRelatedAppsAvailable() {
    return remoteRelatedAppsAvailable;
  }

  void setRemoteRelatedAppsAvailable(boolean remoteRelatedAppsAvailable) {
    this.remoteRelatedAppsAvailable = remoteRelatedAppsAvailable;
  }

  enum Origin {
    Installed, Remote, Searched
  }

  private static final class Errors {
    private static final String APP_NOT_FOUND_ERROR_CODE = "APP-1";
    private static final String USER_TIMELINECARD_1 = "USERTIMELINECARD-1";
    private static final String USER_TIMELINECARD_2 = "USERTIMELINECARD-2";
  }
}
