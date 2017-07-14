package cm.aptoide.pt.v8engine.timeline.post;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class PostPresenter implements Presenter {
  private static final Pattern URL_PATTERN = Patterns.WEB_URL;
  private static final String TAG = PostPresenter.class.getSimpleName();
  private final PostView view;
  private final CrashReport crashReport;
  private final PostManager postManager;
  private final RelatedAppsAdapter adapter;
  private final FragmentNavigator fragmentNavigator;

  public PostPresenter(PostView view, CrashReport crashReport, PostManager postManager,
      RelatedAppsAdapter adapter, FragmentNavigator fragmentNavigator) {
    this.view = view;
    this.crashReport = crashReport;
    this.postManager = postManager;
    this.adapter = adapter;
    this.fragmentNavigator = fragmentNavigator;
  }

  @Override public void present() {
    showInstalledAppsOnStart();
    postOnTimelineOnButtonClick();
    showCardPreviewAfterTextChanges();
    showRelatedAppsAfterTextChanges();
    handleCancelButtonClick();
    handleRelatedAppClick();
    handleNoUrlInserted();
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private void handleNoUrlInserted() {
    getUrlFromInsertedText().debounce(1, TimeUnit.SECONDS)
        .distinctUntilChanged()
        .filter(url -> url == null)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(emptyUrl -> view.hideCardPreview())
        .flatMapCompletable(emptyUrl -> loadRelatedApps(null))
        .retry()
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(viewHidden -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void showInstalledAppsOnStart() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMapCompletable(lifecycleEvent -> loadRelatedApps(null))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleRelatedAppClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> adapter.getClickedView()
            .flatMapCompletable(app -> adapter.setRelatedAppSelected(app)))
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
        .flatMap(__ -> getInsertedUrl().flatMap(insertedUrl -> Observable.just(insertedUrl)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(__2 -> view.showCardPreviewLoading())
            .doOnNext(__2 -> view.hideCardPreview())
            .observeOn(Schedulers.io())
            .flatMapSingle(url -> postManager.getPreview(url))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(__2 -> view.hideCardPreviewLoading())
            .doOnNext(suggestion -> view.showCardPreview(suggestion))
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

  @NonNull private Observable<String> getInsertedUrl() {
    return getUrlFromInsertedText().debounce(1, TimeUnit.SECONDS)
        .distinctUntilChanged()
        .filter(url -> !TextUtils.isEmpty(url))
        .map(url -> addProtocolIfNeeded(url));
  }

  @NonNull private String addProtocolIfNeeded(String url) {
    if (!url.contains("http://") && !url.contains("https://")) {
      url = "http://".concat(url);
    }
    return url;
  }

  @NonNull private Observable<String> getUrlFromInsertedText() {
    return view.onInputTextChanged()
        .skip(1)
        .map(text -> findUrlOrNull(text));
  }

  private String findUrlOrNull(String text) {
    for (String textPart : text.split(" ")) {
      if (URL_PATTERN.matcher(textPart)
          .matches()) {
        return textPart;
      }
    }
    return null;
  }

  private void showRelatedAppsAfterTextChanges() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> getInsertedUrl().flatMap(inputUrl -> Observable.just(inputUrl)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable(url -> loadRelatedApps(url))
            .retry()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  Completable loadRelatedApps(String url) {
    return Completable.fromAction(() -> view.showRelatedAppsLoading())
        .observeOn(Schedulers.io())
        .andThen(postManager.getAppSuggestions(url))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess(__2 -> view.hideRelatedAppsLoading())
        .toObservable()
        .filter(relatedApps -> relatedApps != null && !relatedApps.isEmpty())
        .flatMapCompletable(relatedApps -> adapter.setRelatedApps(relatedApps))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(throwable -> view.hideRelatedAppsLoading())
        .toCompletable();
  }

  private void postOnTimelineOnButtonClick() {

    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.shareButtonPressed()
            .observeOn(Schedulers.io())
            .flatMapCompletable(
                textToShare -> postManager.post(addProtocolIfNeeded(findUrlOrNull(textToShare)),
                    textToShare, adapter.getCurrentSelected()
                        .getPackageName())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnCompleted(() -> view.showSuccessMessage())
                    .doOnCompleted(() -> fragmentNavigator.popBackStack()))
            .doOnError(throwable -> Logger.w(TAG, "postOnTimelineOnButtonClick: ", throwable))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          crashReport.log(err);
          fragmentNavigator.popBackStack();
        });
  }
}
