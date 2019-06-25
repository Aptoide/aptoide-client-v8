package cm.aptoide.pt.view;

import android.os.Bundle;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.link.CustomTabsHelper;
import cm.aptoide.pt.navigator.ActivityNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;

/**
 * Created by D01 on 02/08/2018.
 */

public class AppCoinsInfoNavigator {

  static final String APPC_WALLET_PACKAGE_NAME = "com.appcoins.wallet";
  private final ActivityNavigator activityNavigator;
  private final FragmentNavigator fragmentNavigator;
  private final String theme;

  public AppCoinsInfoNavigator(ActivityNavigator activityNavigator,
      FragmentNavigator fragmentNavigator, String theme) {

    this.activityNavigator = activityNavigator;
    this.fragmentNavigator = fragmentNavigator;
    this.theme = theme;
  }

  public void navigateToCoinbaseLink() {
    CustomTabsHelper.getInstance()
        .openInChromeCustomTab(activityNavigator.getActivity()
            .getString(R.string.coinbase_url), activityNavigator.getActivity(), theme);
  }

  public void navigateToAppCoinsWallet() {
    AppViewFragment appViewFragment = new AppViewFragment();
    Bundle bundle = new Bundle();
    bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(), APPC_WALLET_PACKAGE_NAME);
    bundle.putString(AppViewFragment.BundleKeys.STORE_NAME.name(), "catappult");
    appViewFragment.setArguments(bundle);
    fragmentNavigator.navigateTo(appViewFragment, true);
  }
}
