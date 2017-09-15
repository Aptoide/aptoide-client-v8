package cm.aptoide.pt.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.analytics.AptoideNavigationTracker;

/**
 * Created by pedroribeiro on 14/09/17.
 */

public class NavigationTrackFragment extends FragmentView {

  protected AptoideNavigationTracker navigationTracker;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    navigationTracker =
        ((AptoideApplication) getContext().getApplicationContext()).getAptoideNavigationTracker();
  }
}
