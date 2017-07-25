package cm.aptoide.pt.v8engine.timeline.post;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.timeline.post.exceptions.PostException;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class PostPresenter implements Presenter {
  private static final String TAG = PostPresenter.class.getSimpleName();
  private final PostView view;
  private final CrashReport crashReport;
  private final PostManager postManager;
  private final FragmentNavigator fragmentNavigator;
  private final PostFragment.PostUrlProvider postUrlProvider;
  private UrlValidator urlValidator;
  private AccountNavigator accountNavigator;

  public PostPresenter(PostFragment view, CrashReport crashReport, PostManager postManager,
      FragmentNavigator fragmentNavigator, UrlValidator urlValidator,
      AccountNavigator accountNavigator, PostFragment.PostUrlProvider postUrlProvider) {
    this.view = view;
    this.crashReport = crashReport;
    this.postManager = postManager;
    this.fragmentNavigator = fragmentNavigator;
    this.urlValidator = urlValidator;
    this.accountNavigator = accountNavigator;
    this.postUrlProvider = postUrlProvider;
  }

  @Override public void present() {
    showRelatedAppsOnStart();
    showPreviewAppsOnStart();
    postOnTimelineOnButtonClick();
    handleCancelButtonClick();
    handleRelatedAppClick();
    onCreateLoginErrorHandle();
    if (!isExternalOpen()) {
      showCardPreviewAfterTextChanges();
      showRelatedAppsAfterTextChanges();
    }
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private void onCreateLoginErrorHandle() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(viewCreated -> view.getLoginClick())
        .doOnNext(loginClicked -> accountNavigator.navigateToAccountView(
            Analytics.Account.AccountOrigins.POST_ON_TIMELINE))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void showPreviewAppsOnStart() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.RESUME && isExternalOpen())
        .flatMap(__ -> loadPostPreview(postUrlProvider.getUrlToShare()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private boolean isExternalOpen() {
    return postUrlProvider.getUrlToShare() != null && !postUrlProvider.getUrlToShare()
        .isEmpty();
  }

  private void showRelatedAppsOnStart() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .doOnNext(lifecycleEvent -> view.showRelatedAppsLoading())
        .observeOn(Schedulers.io())
        .switchMap(lifecycleEvent -> getStartSuggestions().toObservable())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(relatedApps -> {
          view.addRelatedApps(relatedApps);
          view.hideRelatedAppsLoading();
        })
        .doOnError(throwable -> view.hideRelatedAppsLoading())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private Single<List<PostRemoteAccessor.RelatedApp>> getStartSuggestions() {
    if (isExternalOpen()) {
      return Single.zip(postManager.getLocalAppSuggestions(),
          postManager.getRemoteAppSuggestions(postUrlProvider.getUrlToShare())
              .onErrorResumeNext(throwable -> Single.just(Collections.emptyList())),
          (localApps, remoteApps) -> {
            ArrayList<PostRemoteAccessor.RelatedApp> list = new ArrayList<>();
            list.addAll(remoteApps);
            list.addAll(localApps);
            return list;
          });
    }
    return postManager.getLocalAppSuggestions();
  }

  private void handleRelatedAppClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.getClickedView()
            .flatMapCompletable(app -> view.setRelatedAppSelected(app)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleCancelButtonClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.cancelButtonPressed()
            .doOnNext(__2 -> goBack()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void goBack() {
    if (isExternalOpen()) {
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

  @NonNull private Observable<PostView.PostPreview> loadPostPreview(String url) {
    return Observable.just(url)
        .doOnNext(__2 -> view.showCardPreviewLoading())
        .doOnNext(__2 -> view.hideCardPreview())
        .observeOn(Schedulers.io())
        .flatMapSingle(__2 -> postManager.getPreview(url))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(suggestion -> view.showCardPreview(suggestion))
        .doOnNext(__2 -> view.hideCardPreviewLoading())
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
                return Observable.just("");
              }
              return loadRelatedApps(url);
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private Observable<List<PostRemoteAccessor.RelatedApp>> loadRelatedApps(String url) {
    return Completable.fromAction(() -> view.showRelatedAppsLoading())
        .observeOn(Schedulers.io())
        .andThen(postManager.getRemoteAppSuggestions(url))
        .observeOn(AndroidSchedulers.mainThread())
        .toObservable()
        .doOnNext(relatedApps -> view.addRelatedApps(relatedApps))
        .doOnCompleted(() -> view.hideRelatedAppsLoading())
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
              String url;
              url = getUrl(textToShare);
              return postManager.post(url, textToShare, view.getCurrentSelected() == null ? null
                  : view.getCurrentSelected()
                      .getPackageName())
                  .observeOn(AndroidSchedulers.mainThread())
                  .doOnCompleted(() -> view.showSuccessMessage())
                  .doOnCompleted(() -> goBack());
            })
            .doOnError(throwable -> handleError(throwable))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          crashReport.log(err);
          goBack();
        });
  }

  @Nullable private String getUrl(String textToShare) {
    String url;
    if (isExternalOpen()) {
      url = postUrlProvider.getUrlToShare();
    } else {
      url = urlValidator.containsUrl(textToShare) ? urlValidator.getUrl(textToShare) : null;
    }
    return url;
  }

  private void handleError(Throwable throwable) {
    Logger.e(TAG, throwable);
    if (throwable instanceof PostException) {
      switch (((PostException) throwable).getErrorCode()) {
        case INVALID_TEXT:
          view.showInvalidTextError();
          break;
        case INVALID_PACKAGE:
          view.showInvalidPackageError();
          break;
        case NO_LOGIN:
          view.showNoLoginError();
          break;
      }
    } else {
      view.showGenericError();
    }
  }
}
