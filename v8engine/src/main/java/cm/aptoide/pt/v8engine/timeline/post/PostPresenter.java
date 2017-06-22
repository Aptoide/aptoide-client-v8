package cm.aptoide.pt.v8engine.timeline.post;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class PostPresenter implements Presenter {
  private static final Pattern urlPattern = Patterns.WEB_URL;
  private final PostView view;
  private final CrashReport crashReport;
  private final PostManager postManager;
  private final InstallManager installManager;
  private final RelatedAppsAdapter adapter;

  public PostPresenter(PostView view, CrashReport crashReport, PostManager postManager,
      InstallManager installManager, RelatedAppsAdapter adapter) {
    this.view = view;
    this.crashReport = crashReport;
    this.postManager = postManager;
    this.installManager = installManager;
    this.adapter = adapter;
  }

  @Override public void present() {
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
            .flatMapCompletable(__2 -> view.close()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void showCardPreviewAfterTextChanges() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.onInputTextChanged()
            .doOnNext(__2 -> view.showCardPreviewLoading())
            .observeOn(Schedulers.io())
            .flatMapSingle(text -> findUrlOrNull(text))
            .filter(url -> {
              if (!TextUtils.isEmpty(url)) {
                return true;
              } else {
                view.hideCardPreviewLoading();
                return false;
              }
            })
            .flatMapSingle(url -> postManager.getPreview(url)
                .retry())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(__2 -> view.hideCardPreviewLoading())
            .flatMap(suggestion -> view.showCardPreview(suggestion)
                .toObservable()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private Single<String> findUrlOrNull(String text) {
    return Single.just(text)
        .map(input -> {
          for (String textPart : input.split(" ")) {
            if (urlPattern.matcher(textPart)
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
            .doOnNext(__2 -> view.showRelatedAppsLoading())
            .flatMapSingle(text -> findUrlOrNull(text))
            .filter(url -> {
              if (!TextUtils.isEmpty(url)) {
                return true;
              } else {
                view.hideRelatedAppsLoading();
                return false;
              }
            })
            .flatMapSingle(url -> view.showContainsUrlMessage()
                .observeOn(Schedulers.io())
                .andThen(getAppSuggestions(url).retry()))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(__2 -> view.hideRelatedAppsLoading())
            .filter(relatedApps -> relatedApps != null && !relatedApps.isEmpty())
            .flatMapCompletable(relatedApps -> adapter.setRelatedApps(relatedApps)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private Single<List<PostManager.RelatedApp>> getAppSuggestions(String text) {
    Single<List<PostManager.RelatedApp>> remoteSuggestions = postManager.getAppSuggestions(text);
    Single<List<PostManager.RelatedApp>> searchSuggestions = Single.just(Collections.emptyList());
    Single<List<PostManager.RelatedApp>> installedApps = installManager.getInstallations()
        .map(downloadProgress -> convertDownloadToRelatedApp(downloadProgress))
        .toList()
        .first()
        .toSingle();

    return remoteSuggestions;

    /*
    return Single.zip(remoteSuggestions, installedApps, searchSuggestions,
        (listA, listB, listC) -> {
          ArrayList<PostManager.RelatedApp> relatedApps =
              new ArrayList<>(listA.size() + listB.size() + listC.size());
          relatedApps.addAll(listA);
          relatedApps.addAll(listB);
          relatedApps.addAll(listC);
          return relatedApps;
        });
        */
  }

  private PostManager.RelatedApp convertDownloadToRelatedApp(Progress<Download> progressDownload) {
    Download download = progressDownload.getRequest();
    return new PostManager.RelatedApp(download.getIcon(), download.getAppName(),
        PostManager.Origin.Installed, false);
  }

  private void postOnTimelineOnButtonClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.shareButtonPressed()
            .observeOn(Schedulers.io())
            .flatMap(textToShare -> postManager.post(textToShare)
                .observeOn(AndroidSchedulers.mainThread())
                .andThen(view.showSuccessMessage())
                .andThen(view.close())
                .toObservable()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          crashReport.log(err);
          view.close();
        });
  }
}
