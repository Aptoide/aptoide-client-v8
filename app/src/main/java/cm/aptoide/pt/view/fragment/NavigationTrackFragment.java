package cm.aptoide.pt.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.PageViewsAnalytics;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by pedroribeiro on 14/09/17.
 */

public abstract class NavigationTrackFragment extends FragmentView {

  public static final String SHOULD_REGISTER_VIEW = "should_register_view";
  protected NavigationTracker navigationTracker;
  protected PageViewsAnalytics pageViewsAnalytics;
  protected boolean shouldRegister = true;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (navigationTracker == null) {
      navigationTracker =
          ((AptoideApplication) getContext().getApplicationContext()).getNavigationTracker();
    }
    pageViewsAnalytics =
        new PageViewsAnalytics(AppEventsLogger.newLogger(getContext().getApplicationContext()),
            Analytics.getInstance());
    getFragmentExtras();
  }

  private void getFragmentExtras() {
    if (getArguments() != null && getArguments().containsKey(SHOULD_REGISTER_VIEW)) {
      shouldRegister = getArguments().getBoolean(SHOULD_REGISTER_VIEW);
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @Override public void onResume() {
    super.onResume();
    ScreenTagHistory historyTracker = getHistoryTracker();
    if (shouldRegister) {
      if (historyTracker == null) {
        throw new RuntimeException("If "
            + this.getClass()
            .getSimpleName()
            + " should be logged to screen history, it has to return a value on method NavigationTrackFragment#getHistoryTracker");
      }
      navigationTracker.registerScreen(historyTracker);
    }
  }

  protected void setRegisterFragment(boolean shouldRegister) {
    this.shouldRegister = shouldRegister;
  }

  public ScreenTagHistory getHistoryTracker() {
    return null;
  }
}
