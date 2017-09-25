package cm.aptoide.pt.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.analytics.AptoideNavigationTracker;

/**
 * Created by pedroribeiro on 14/09/17.
 */

public class NavigationTrackFragment extends FragmentView {

  public static final String DO_NOT_REGISTER_VIEW = "do_not_register_view";
  protected AptoideNavigationTracker navigationTracker;
  private boolean registerFragment = false;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (navigationTracker == null) {
      navigationTracker =
          ((AptoideApplication) getContext().getApplicationContext()).getAptoideNavigationTracker();
    }
    getFragmentExtras();
  }

  @Override public void onResume() {
    super.onResume();
    if (!registerFragment) {
      navigationTracker.registerView(this.getClass()
          .getSimpleName());
    }
  }

  private void getFragmentExtras() {
    if (getArguments() != null) {
      registerFragment = getArguments().getBoolean(DO_NOT_REGISTER_VIEW);
    }
  }
}
