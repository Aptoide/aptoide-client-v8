package cm.aptoide.pt.v8engine.timeline.createpost;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.Observable;

class CreatePostPresenter implements Presenter {
  private final CreatePostView view;
  private final CrashReport crashReport;

  public CreatePostPresenter(CreatePostView view, CrashReport crashReport) {
    this.view = view;
    this.crashReport = crashReport;
  }

  @Override public void present() {

    Observable<Void> shareButton = view.shareButtonPressed()
        .flatMap(__ -> shareContent(view.getInputText()))
        .retry()
        .map(__ -> null);

    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> shareButton)
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private Observable<Void> shareContent(String content) {
    return Completable.fromAction(() -> {
      crashReport.log("send to server", content);
    })
        .toObservable();
  }
}
