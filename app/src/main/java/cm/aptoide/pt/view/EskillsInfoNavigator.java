package cm.aptoide.pt.view;

import android.os.Bundle;
import cm.aptoide.pt.CatappultNavigator;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.socialmedia.SocialMediaNavigator;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment;

/**
 * Created by D01 on 02/08/2018.
 */

public class EskillsInfoNavigator {

  static final String APPC_WALLET_PACKAGE_NAME = "com.appcoins.wallet";
  private final FragmentNavigator fragmentNavigator;

  public EskillsInfoNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToAppCoinsWallet() {
    AppViewFragment appViewFragment = new AppViewFragment();
    Bundle bundle = new Bundle();
    bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(), APPC_WALLET_PACKAGE_NAME);
    bundle.putString(AppViewFragment.BundleKeys.STORE_NAME.name(), "catappult");
    appViewFragment.setArguments(bundle);
    fragmentNavigator.navigateTo(appViewFragment, true);
  }

  public void navigateToESkills() {
    Event event = new Event();
    event.setAction(null);
    event.setData(null);
    event.setType(null);
    event.setName(Event.Name.eSkills);
    fragmentNavigator.navigateTo(
        StoreTabGridRecyclerFragment.newInstance(event, HomeEvent.Type.ESKILLS, "e-Skills",
            "default", "eskills", StoreContext.home, true), true);
  }
}
