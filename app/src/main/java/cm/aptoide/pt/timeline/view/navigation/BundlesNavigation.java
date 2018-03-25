package cm.aptoide.pt.timeline.view.navigation;

import android.os.Bundle;
import cm.aptoide.pt.navigator.TabNavigation;

/**
 * Created by jdandrade on 02/05/2017.
 */

public class BundlesNavigation implements TabNavigation {
  public static final String BUNDLE_ID = "BUNDLE_ID";
  private String bundleId;

  public BundlesNavigation(String bundleId) {
    this.bundleId = bundleId;
  }

  @Override public Bundle getBundle() {
    Bundle bundle = new Bundle();
    bundle.putString(BUNDLE_ID, bundleId);
    return bundle;
  }

  @Override public int getTab() {
    return BUNDLES;
  }

  public String getCardId() {
    return bundleId;
  }
}
