package cm.aptoide.pt.v8engine.timeline.post;

import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.timeline.post.exceptions.InvalidPostDataException;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class PostPresenter implements Presenter {
  private static final String TAG = PostPresenter.class.getSimpleName();
  private final PostView view;
  private final CrashReport crashReport;
  private final PostManager postManager;
  private final FragmentNavigator fragmentNavigator;
  private UrlValidator urlValidator;

  public PostPresenter(PostView view, CrashReport crashReport, PostManager postManager,
      FragmentNavigator fragmentNavigator, UrlValidator urlValidator) {
    this.view = view;
    this.crashReport = crashReport;
    this.postManager = postManager;
    this.fragmentNavigator = fragmentNavigator;
    this.urlValidator = urlValidator;
  }

  @Override public void present() {
    showInstalledAppsOnStart();
    postOnTimelineOnButtonClick();
    showCardPreviewAfterTextChanges();
    showRelatedAppsAfterTextChanges();
    handleCancelButtonClick();
    handleRelatedAppClick();
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private void showInstalledAppsOnStart() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .doOnNext(lifecycleEvent -> view.showRelatedAppsLoading())
        .observeOn(Schedulers.io())
        .flatMapSingle(lifecycleEvent -> postManager.getLocalAppSuggestions())
        .filter(relatedApps -> relatedApps != null && !relatedApps.isEmpty())
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
            .doOnNext(__2 -> fragmentNavigator.popBackStack()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void showCardPreviewAfterTextChanges() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(viewCreated -> view.onInputTextChanged()
            .filter(insertedText -> urlValidator.containsUrl(insertedText))
            .debounce(1, TimeUnit.SECONDS)
            .switchMap(insertedUrl -> Observable.just(insertedUrl)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(url -> view.clearRemoteRelated())
                .doOnNext(__2 -> view.showCardPreviewLoading())
                .doOnNext(__2 -> view.hideCardPreview())
                .observeOn(Schedulers.io())
                .flatMapSingle(url -> postManager.getPreview(url))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(suggestion -> view.showCardPreview(suggestion))
                .doOnNext(__2 -> view.hideCardPreviewLoading())
                .doOnError(throwable -> view.hideCardPreviewLoading()))
            .doOnError(throwable -> Logger.w(TAG, "showCardPreviewAfterTextChanges: ", throwable))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          view.hideCardPreviewLoading();
          crashReport.log(err);
        });
  }

  private void showRelatedAppsAfterTextChanges() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(viewCreated -> view.onInputTextChanged()
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .switchMap(insertedText -> {
              if (urlValidator.containsUrl(insertedText)) {
                return loadRelatedApps(urlValidator.getUrl(insertedText));
              } else {
                return resetState();
              }
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  @NonNull private Observable<Integer> resetState() {
    return Observable.fromCallable(() -> {
      view.hideCardPreview();
      view.clearRemoteRelated();
      return -1;
    });
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
        .doOnError(throwable -> {
          throwable.printStackTrace();
          view.hideRelatedAppsLoading();
        });
  }

  private void postOnTimelineOnButtonClick() {

    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.shareButtonPressed()
            .observeOn(Schedulers.io())
            .flatMapCompletable(textToShare -> postManager.post(textToShare, textToShare,
                view.getCurrentSelected() == null ? null : view.getCurrentSelected()
                    .getPackageName())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> view.showSuccessMessage())
                .doOnCompleted(() -> fragmentNavigator.popBackStack()))
            .doOnError(throwable -> handleError(throwable))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          crashReport.log(err);
          fragmentNavigator.popBackStack();
        });
  }

  private void handleError(Throwable throwable) {
    Logger.e(TAG, throwable);
    if (throwable instanceof AptoideWsV7Exception) {
      view.showError(((AptoideWsV7Exception) throwable).getBaseResponse()
          .getErrors()
          .get(0)
          .getCode());
    } else if (throwable instanceof InvalidPostDataException) {
      switch (((InvalidPostDataException) throwable).getErrorCode()) {
        case INVALID_TEXT:
          view.showInvalidTextError();
          break;
        case INVALID_PACKAGE:
          view.showInvalidPackageError();
          break;
      }
    }
  }
}
