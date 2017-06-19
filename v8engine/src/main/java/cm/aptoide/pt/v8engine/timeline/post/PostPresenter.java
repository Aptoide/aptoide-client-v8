package cm.aptoide.pt.v8engine.timeline.post;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class PostPresenter implements Presenter {
  private final PostView view;
  private final CrashReport crashReport;
  private final PostManager postManager;

  public PostPresenter(PostView view, CrashReport crashReport, PostManager postManager) {
    this.view = view;
    this.crashReport = crashReport;
    this.postManager = postManager;
  }

  @Override public void present() {
    postOnTimelineOnButtonClick();
    callBackendAfterTextChanges();
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private void callBackendAfterTextChanges() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.onInputTextChanged()
            .observeOn(Schedulers.io())
            .flatMapSingle(text -> postManager.getSuggestion(text)
                .retry())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(suggestion -> view.showSuggestion(suggestion)
                .toObservable()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void postOnTimelineOnButtonClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.shareButtonPressed()
            .flatMap(textToShare -> view.showLoading()
                .observeOn(Schedulers.io())
                .andThen(postManager.post(textToShare)
                    .retry())
                .observeOn(AndroidSchedulers.mainThread())
                .andThen(view.hideLoading())
                .andThen(view.showSuccessMessage())
                .andThen(view.close())
                .toObservable()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }
}
