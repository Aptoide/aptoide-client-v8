package cm.aptoide.pt.view;

import android.os.Bundle;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment;


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

  public void navigateToESkillsMoreGames(String title, String tag, String action, String eventName) {
    Event event = new Event();
    event.setName(Event.Name.valueOf(eventName));
    event.setAction(action);
    fragmentNavigator.navigateTo(
        StoreTabGridRecyclerFragment.newInstance(event, HomeEvent.Type.ESKILLS, title, "default",
            tag, StoreContext.home, true), true);
  }
}
