package cm.aptoide.pt.v8engine.timeline.post;

import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.v8engine.timeline.post.PostRemoteAccessor.RelatedApp;
import cm.aptoide.pt.v8engine.timeline.post.exceptions.PostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.Completable;
import rx.Single;

public class PostManager {

  public static final String APP_NOT_FOUND_ERROR_CODE = "APP-1";
  private final PostAccessor postRemoteRepository;
  private final PostAccessor postLocalRepository;
  private AptoideAccountManager accountManager;

  public PostManager(PostRemoteAccessor postRemoteRepository, PostAccessor postLocalRepository,
      AptoideAccountManager accountManager) {
    this.postRemoteRepository = postRemoteRepository;
    this.postLocalRepository = postLocalRepository;
    this.accountManager = accountManager;
  }

  public Completable post(String url, String content, String packageName) {
    return validateLogin().flatMap(userLogged -> validateInsertedText(content, packageName, url))
        .flatMapCompletable(
            validPost -> postRemoteRepository.postOnTimeline(addProtocolIfNeeded(url),
                getContent(url, content), packageName))
        .onErrorResumeNext(throwable -> handleErrors(throwable));
  }

  private Completable handleErrors(Throwable throwable) {
    if (throwable instanceof AptoideWsV7Exception) {
      if (((AptoideWsV7Exception) throwable).getBaseResponse()
          .getError()
          .getCode()
          .equals(APP_NOT_FOUND_ERROR_CODE)) {
        return Completable.error(new PostException(PostException.ErrorCode.NO_APP_FOUND));
      }
    }
    return Completable.error(throwable);
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

  private Single<Boolean> validateInsertedText(String textToShare, String packageName, String url) {
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

  public Single<List<RelatedApp>> getSuggestionAppsOnStart(@Nullable String url) {
    if (url != null && !url.isEmpty()) {
      return Single.zip(getSuggestionApps(),
          postRemoteRepository.getRelatedApps(addProtocolIfNeeded(url))
              .onErrorResumeNext(throwable -> Single.just(Collections.emptyList())),
          (localApps, remoteApps) -> {
            ArrayList<RelatedApp> list = new ArrayList<>();
            list.addAll(remoteApps);
            list.addAll(localApps);
            return list;
          });
    }
    return getSuggestionApps();
  }

  @CheckResult private String addProtocolIfNeeded(String url) {
    if (url != null && !url.contains("http://") && !url.contains("https://")) {
      url = "http://".concat(url);
    }
    return url;
  }

  public Single<PostView.PostPreview> getPreview(String url) {
    return postRemoteRepository.getCardPreview(addProtocolIfNeeded(url))
        .onErrorReturn(throwable -> new PostView.PostPreview(null, null, url));
  }

  enum Origin {
    Installed, Remote, Searched
  }
}
