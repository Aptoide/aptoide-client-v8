package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.spotandshare.SpotAndShareAnalytics;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 23/02/17.
 */
public class SpotSharePreviewPresenter implements Presenter {

  private final SpotSharePreviewView view;
  private final boolean showToolbar;
  private final String toolbarTitle;
  private final SpotAndShareAnalytics analytics;

  public SpotSharePreviewPresenter(SpotSharePreviewView view, boolean showToolbar,
      String toolbarTitle, SpotAndShareAnalytics analytics) {
    this.view = view;
    this.showToolbar = showToolbar;
    this.toolbarTitle = toolbarTitle;
    this.analytics = analytics;
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
            analytics.clickShareApps(SpotAndShareAnalytics.SPOT_AND_SHARE_START_CLICK_ORIGIN_TAB);
          } else {
            analytics.clickShareApps(
                SpotAndShareAnalytics.SPOT_AND_SHARE_START_CLICK_ORIGIN_DRAWER);
            view.finish();
          }
        });
  }
}
