package cm.aptoide.pt.timeline.post;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.TabNavigator;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.timeline.post.exceptions.PostException;
import cm.aptoide.pt.timeline.view.navigation.AppsTimelineTabNavigation;
import cm.aptoide.pt.view.BackButton;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class PostPresenter implements Presenter, BackButton.ClickHandler {

  private static final String TAG = PostPresenter.class.getSimpleName();

  private static final String UPLOADER_PACKAGE_NAME = "pt.caixamagica.aptoide.uploader";

  private final PostView view;
  private final CrashReport crashReport;
  private final PostManager postManager;
  private final FragmentNavigator fragmentNavigator;
  private final TabNavigator tabNavigator;
  private PostAnalytics analytics;
  private UrlValidator urlValidator;
  private AccountNavigator accountNavigator;
  private boolean hasComment;
  private boolean hasUrl;
  private String url;

  public PostPresenter(PostFragment view, CrashReport crashReport, PostManager postManager,
      FragmentNavigator fragmentNavigator, UrlValidator urlValidator,
      AccountNavigator accountNavigator, TabNavigator tabNavigator, PostAnalytics analytics) {
    this.view = view;
    this.crashReport = crashReport;
    this.postManager = postManager;
    this.fragmentNavigator = fragmentNavigator;
    this.urlValidator = urlValidator;
    this.accountNavigator = accountNavigator;
    this.tabNavigator = tabNavigator;
    this.analytics = analytics;
  }

  @Override public void present() {
    if (view.isExternalOpen()) {
      showPreviewAppsOnStart();
    } else {
      showCardPreviewAfterTextChanges();
      showRelatedAppsAfterTextChanges();
    }
    showRelatedAppsOnStart();
    postOnTimelineOnButtonClick();
    handleCancelButtonClick();
    handleRelatedAppClick();
    onCreateLoginErrorHandle();
    onViewCreatedHandleAppNotFoundErrorAction();
  }

  private void onViewCreatedHandleAppNotFoundErrorAction() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(viewCreated -> view.getAppNotFoundErrorAction())
        .doOnNext(click -> fragmentNavigator.navigateTo(
            AppViewFragment.newInstance(UPLOADER_PACKAGE_NAME, AppViewFragment.OpenType.OPEN_ONLY),
            true))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(aVoid -> {
        }, crashReport::log);
  }

  private void onCreateLoginErrorHandle() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(viewCreated -> view.getLoginClick())
        .doOnNext(loginClicked -> accountNavigator.navigateToAccountView(AccountAnalytics.AccountOrigins.POST_ON_TIMELINE))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }

  private void showPreviewAppsOnStart() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.RESUME && view.isExternalOpen())
        .flatMap(__ -> loadPostPreview(view.getExternalUrlToShare()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }

  private void showRelatedAppsOnStart() {
    view.getLifecycle()
        .filter(this::getRelatedAppsLifecycleFilter)
        .doOnNext(lifecycleEvent -> view.clearAllRelated())
        .doOnNext(lifecycleEvent -> view.showRelatedAppsLoading())
        .observeOn(Schedulers.io())
        .switchMap(viewResumed -> {
          if (view.isExternalOpen()) {
            return Observable.merge(postManager.getSuggestionApps(view.getExternalUrlToShare())
                .toObservable(), postManager.getSuggestionApps()
                .toObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(view::hideRelatedAppsLoading);
          } else {
            return postManager.getSuggestionApps()
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(view::hideRelatedAppsLoading);
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(view::addRelatedApps)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }

  private boolean getRelatedAppsLifecycleFilter(View.LifecycleEvent event) {
    return view.isExternalOpen() ? event.equals(View.LifecycleEvent.RESUME)
        : event.equals(View.LifecycleEvent.CREATE);
  }

  private void handleRelatedAppClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.getClickedView()
            .flatMapCompletable(view::setRelatedAppSelected))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }

  private void handleCancelButtonClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.cancelButtonPressed()
            .doOnNext(click -> analytics.sendClosePostEvent(PostAnalytics.CloseType.X, view.isExternalOpen()))
            .doOnNext(__2 -> goBack()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }

  private void goBack() {
    if (view.isExternalOpen()) {
      view.exit();
    } else {
      fragmentNavigator.popBackStack();
    }
  }

  private void showCardPreviewAfterTextChanges() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(viewCreated -> view.onInputTextChanged()
            .debounce(1, TimeUnit.SECONDS)
            .map(insertedText -> urlValidator.getUrl(insertedText))
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .switchMap(url -> {
              if (url.isEmpty()) {
                return hidePreview();
              }
              return loadPostPreview(url);
            })
            .doOnError(throwable -> Logger.w(TAG, "showCardPreviewAfterTextChanges: ", throwable)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          view.hideCardPreviewLoading();
          crashReport.log(err);
        });
  }

  @NonNull private Observable<PostPreview> loadPostPreview(String url) {
    return Observable.just(url)
        .doOnNext(__ -> view.showCardPreviewLoading())
        .doOnNext(__ -> view.hideCardPreview())
        .observeOn(Schedulers.io())
        .flatMapSingle(postManager::getPreview)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(view::showCardPreview)
        .doOnNext(__ -> view.hideCardPreviewLoading())
        .doOnError(throwable -> view.hideCardPreviewLoading())
        .onErrorReturn(throwable -> {
          Logger.w(TAG, "showCardPreviewAfterTextChanges: ", throwable);
          view.hideCardPreview();
          view.hideCardPreviewLoading();
          return null;
        });
  }

  @NonNull private Observable<String> hidePreview() {
    return Observable.fromCallable(() -> {
      view.hideCardPreviewTitle();
      view.hideCardPreview();
      view.hideCardPreviewLoading();
      return "";
    });
  }

  private void showRelatedAppsAfterTextChanges() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(viewCreated -> view.onInputTextChanged()
            .debounce(1, TimeUnit.SECONDS)
            .map(insertedText -> urlValidator.getUrl(insertedText))
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(__ -> view.clearRemoteRelated())
            .switchMap(url -> {
              if (url.isEmpty()) {
                view.hideRelatedAppsLoading();
                return Observable.just("");
              }
              return loadRelatedApps(url);
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }

  private Observable<List<PostRemoteAccessor.RelatedApp>> loadRelatedApps(String url) {
    return Completable.fromAction(view::showRelatedAppsLoading)
        .observeOn(Schedulers.io())
        .andThen(postManager.getSuggestionApps(url))
        .observeOn(AndroidSchedulers.mainThread())
        .toObservable()
        .doOnNext(relatedApps -> {
          view.addRelatedApps(relatedApps);
          postManager.setRemoteRelatedAppsAvailable(relatedApps.size() > 0);
        })
        .doOnCompleted(view::hideRelatedAppsLoading)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(throwable -> view.hideRelatedAppsLoading())
        .onErrorReturn(throwable -> Collections.emptyList());
  }

  private void postOnTimelineOnButtonClick() {

    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.shareButtonPressed()
            .observeOn(Schedulers.io())
            .flatMapCompletable(textToShare -> {
              url = getUrl(textToShare);
              hasComment = !textToShare.isEmpty();
              hasUrl = url != null;
              final String selectedPackageName = view.getCurrentSelected() == null ? null
                  : view.getCurrentSelected()
                      .getPackageName();
              return sendPostData(textToShare, selectedPackageName);
            })
            .doOnError(this::handleError)
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          crashReport.log(err);
          goBack();
        });
  }

  @NonNull private Completable sendPostData(String textToShare, String selectedPackageName) {
    return postManager.post(url, textToShare, selectedPackageName)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess(postId -> {
          view.showSuccessMessage();
          analytics.sendPostCompleteEvent(postManager.remoteRelatedAppsAvailable(),
              view.getCurrentSelected()
                  .getPackageName(), hasComment, hasUrl, url == null ? "" : url,
              android.view.View.VISIBLE == view.getPreviewVisibility(), view.isExternalOpen());
        })
        .doOnSuccess(postId -> tabNavigator.navigate(new AppsTimelineTabNavigation(postId)))
        .doOnSuccess(postId -> goBack())
        .toCompletable();
  }

  @Nullable private String getUrl(String textToShare) {
    String url;
    if (view.isExternalOpen()) {
      url = view.getExternalUrlToShare();
    } else {
      url = urlValidator.containsUrl(textToShare) ? urlValidator.getUrl(textToShare) : null;
    }
    return url;
  }

  private void handleError(Throwable throwable) {
    Logger.e(TAG, throwable);
    boolean isSelected = view.getCurrentSelected() != null;
    String packageName = view.getCurrentSelected() == null ? "" : view.getCurrentSelected()
        .getPackageName();
    if (throwable instanceof PostException) {
      switch (((PostException) throwable).getErrorCode()) {
        case INVALID_TEXT:
          view.showInvalidTextError();
          analytics.sendPostCompleteNoTextEvent(postManager.remoteRelatedAppsAvailable(),
              isSelected, packageName, hasComment, hasUrl, url == null ? "" : url,
              android.view.View.VISIBLE == view.getPreviewVisibility(), view.isExternalOpen());
          break;
        case INVALID_PACKAGE:
          view.showInvalidPackageError();
          analytics.sendPostCompleteNoSelectedAppEvent(postManager.remoteRelatedAppsAvailable(),
              hasComment, hasUrl, url == null ? "" : url,
              android.view.View.VISIBLE == view.getPreviewVisibility(), view.isExternalOpen());
          break;
        case NO_LOGIN:
          view.showNoLoginError();
          analytics.sendPostCompleteNoLoginEvent(postManager.remoteRelatedAppsAvailable(),
              isSelected, packageName, hasComment, hasUrl, url == null ? "" : url,
              android.view.View.VISIBLE == view.getPreviewVisibility(), view.isExternalOpen());
          break;
        case NO_APP_FOUND:
          view.showAppNotFoundError();
          analytics.sendPostCompleteNoAppFoundEvent(postManager.remoteRelatedAppsAvailable(),
              isSelected, packageName, hasComment, hasUrl, url == null ? "" : url,
              android.view.View.VISIBLE == view.getPreviewVisibility(), view.isExternalOpen());
          break;
        case INVALID_URL:
          view.showInvalidUrlError();
          analytics.sendPostCompleteNoAppFoundEvent(postManager.remoteRelatedAppsAvailable(),
              isSelected, packageName, hasComment, hasUrl, url == null ? "" : url,
              android.view.View.VISIBLE == view.getPreviewVisibility(), view.isExternalOpen());
          break;
      }
    } else if (throwable instanceof AptoideWsV7Exception) {
      view.showGenericError();
      String errorCodes = parseErrorCode(((AptoideWsV7Exception) throwable).getBaseResponse()
          .getErrors());
      analytics.sendPostCompletedNetworkFailedEvent(postManager.remoteRelatedAppsAvailable(),
          isSelected, packageName, hasComment, hasUrl, url == null ? "" : url,
          android.view.View.VISIBLE == view.getPreviewVisibility(), errorCodes, view.isExternalOpen());
    } else {
      view.showGenericError();
      analytics.sendPostFailedEvent(postManager.remoteRelatedAppsAvailable(), isSelected,
          packageName, hasComment, hasUrl, url == null ? "" : url,
          android.view.View.VISIBLE == view.getPreviewVisibility(), throwable.getClass()
              .getSimpleName(), view.isExternalOpen());
    }
  }

  private String parseErrorCode(List<BaseV7Response.Error> errorList) {
    StringBuilder errorCodes = new StringBuilder();
    for (int i = 0; i < errorList.size(); i++) {
      BaseV7Response.Error error = errorList.get(i);
      errorCodes.append(error.getCode());
      if (i == errorList.size() - 1) {
        return errorCodes.toString();
      }
      errorCodes.append(',');
    }
    return errorCodes.toString();
  }

  @Override public boolean handle() {
    analytics.sendClosePostEvent(PostAnalytics.CloseType.BACK, view.isExternalOpen());
    goBack();
    return true;
  }
}
