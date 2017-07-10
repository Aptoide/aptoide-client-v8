package cm.aptoide.pt.v8engine.timeline.post;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import rx.Single;
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
        .doOnNext(__ -> view.showRelatedAppsLoading())
        .observeOn(Schedulers.io())
        .flatMapSingle(__ -> postManager.getAppSuggestions(null))
        .filter(apps -> apps != null && !apps.isEmpty())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.hideRelatedAppsLoading())
        .flatMapCompletable(relatedApps -> adapter.setRelatedApps(relatedApps))
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
        .flatMap(__ -> view.onInputTextChanged()
            .skip(1)
            .flatMap(text -> findUrlOrNull(text).toObservable()
                .filter(url -> !TextUtils.isEmpty(url))
                .debounce(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(__2 -> view.showCardPreviewLoading())
                .doOnNext(__2 -> view.hideCardPreview())
                .observeOn(Schedulers.io())
                .flatMapSingle(url -> postManager.getPreview(url))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(__2 -> view.hideCardPreviewLoading())
                .doOnNext(suggestion -> view.showCardPreview(suggestion))
                .doOnError(throwable -> view.hideCardPreviewLoading()))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          view.hideCardPreviewLoading();
          crashReport.log(err);
        });
  }

  private Single<String> findUrlOrNull(String text) {
    return Single.just(text)
        .map(input -> {
          for (String textPart : input.split(" ")) {
            if (URL_PATTERN.matcher(textPart)
                .matches()) {
              return textPart;
            }
          }
          return null;
        });
  }

  private void showRelatedAppsAfterTextChanges() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.onInputTextChanged()
            .skip(1)
            .flatMapSingle(text -> findUrlOrNull(text))
            .filter(url -> !TextUtils.isEmpty(url))
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(__2 -> view.showRelatedAppsLoading())
            .observeOn(Schedulers.io())
            .flatMapSingle(url -> postManager.getAppSuggestions(url))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(__2 -> view.hideRelatedAppsLoading())
            .filter(relatedApps -> relatedApps != null && !relatedApps.isEmpty())
            .flatMapCompletable(relatedApps -> adapter.setRelatedApps(relatedApps)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void postOnTimelineOnButtonClick() {

    // view.onInputTextChanged()

    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.shareButtonPressed()
            .observeOn(Schedulers.io())
            .flatMapCompletable(textToShare -> findUrlOrNull(textToShare).flatMapCompletable(
                url -> postManager.post(url, textToShare, adapter.getCurrentSelected()
                    .getPackageName()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> view.showSuccessMessage())
                .doOnCompleted(() -> fragmentNavigator.popBackStack())))
        .retry()
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          crashReport.log(err);
          fragmentNavigator.popBackStack();
        });
  }
}
