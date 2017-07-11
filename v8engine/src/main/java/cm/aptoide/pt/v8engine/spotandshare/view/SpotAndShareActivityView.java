package cm.aptoide.pt.v8engine.spotandshare.view;

import cm.aptoide.pt.v8engine.view.AnalyticsActivity;

public class SpotAndShareActivityView extends AnalyticsActivity {

  private Presenter presenter;

  protected void attachPresenter(Presenter presenter) {
    this.presenter = presenter;
    presenter.onCreate();
  }

  @Override protected void onPause() {
    super.onPause();
    presenter.onPause();
  }

  @Override protected void onResume() {
    super.onResume();
    presenter.onResume();
  }

  @Override protected void onStart() {
    super.onStart();
    presenter.onStart();
  }

  @Override protected void onStop() {
    super.onStop();
    presenter.onStop();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter.onDestroy();
  }
}
