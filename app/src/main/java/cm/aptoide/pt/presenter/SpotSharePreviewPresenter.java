package cm.aptoide.pt.presenter;

import android.os.Bundle;
import cm.aptoide.pt.crashreports.CrashReport;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 23/02/17.
 */
public class SpotSharePreviewPresenter implements Presenter {

  private final SpotSharePreviewView view;
  private final boolean showToolbar;
  private final String toolbarTitle;

  public SpotSharePreviewPresenter(SpotSharePreviewView view, boolean showToolbar,
      String toolbarTitle) {
    this.view = view;
    this.showToolbar = showToolbar;
    this.toolbarTitle = toolbarTitle;
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(
            resumed -> startSelection().compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));

    if (showToolbar) {
      view.showToolbar(toolbarTitle);
    }
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private Observable<Void> startSelection() {
    return view.startSelection()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(selection -> {
          view.navigateToSpotShareView();
          if (!showToolbar) {
          } else {
            view.finish();
          }
        });
  }
}
