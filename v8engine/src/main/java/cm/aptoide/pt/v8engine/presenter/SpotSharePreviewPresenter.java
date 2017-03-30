package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.SpotAndShareAnalytics;
import cm.aptoide.pt.v8engine.view.SpotSharePreviewView;
import cm.aptoide.pt.v8engine.view.View;
import rx.Observable;

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
        }, err -> CrashReport.getInstance().log(err));

    if (showToolbar) {
      view.showToolbar(toolbarTitle);
    }
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private Observable<Void> startSelection() {
    return view.startSelection().doOnNext(selection -> {
      SpotAndShareAnalytics.clickShareApps();
      view.navigateToSpotShareView();
      if (showToolbar) {
        view.finish();
      }
    });
  }
}
