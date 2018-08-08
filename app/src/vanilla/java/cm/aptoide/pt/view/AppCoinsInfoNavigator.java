package cm.aptoide.pt.view;

import android.os.Bundle;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.view.NewAppViewFragment;
import cm.aptoide.pt.link.CustomTabsHelper;
import cm.aptoide.pt.navigator.ActivityNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;

import static cm.aptoide.pt.app.view.AppCoinsInfoFragment.APPC_WALLET_PACKAGE_NAME;

/**
 * Created by D01 on 02/08/2018.
 */

public class AppCoinsInfoNavigator {

  private final ActivityNavigator activityNavigator;
  private final FragmentNavigator fragmentNavigator;

  public AppCoinsInfoNavigator(ActivityNavigator activityNavigator,
      FragmentNavigator fragmentNavigator) {

    this.activityNavigator = activityNavigator;
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToCoinbaseLink() {
    CustomTabsHelper.getInstance()
        .openInChromeCustomTab(activityNavigator.getActivity()
            .getString(R.string.coinbase_url), activityNavigator.getActivity());
  }

  public void navigateToAppCoinsBDSWallet() {
    NewAppViewFragment appViewFragment = new NewAppViewFragment();
    Bundle bundle = new Bundle();
    bundle.putString(NewAppViewFragment.BundleKeys.PACKAGE_NAME.name(), APPC_WALLET_PACKAGE_NAME);
    bundle.putString(NewAppViewFragment.BundleKeys.STORE_NAME.name(), "bds-store");
    appViewFragment.setArguments(bundle);
    fragmentNavigator.navigateTo(appViewFragment, true);
  }
}
