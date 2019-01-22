package cm.aptoide.pt.view.wizard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import cm.aptoide.pt.NavigationTrackerPagerAdapterHelper;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;

public class WizardPagerAdapter extends FragmentPagerAdapter
    implements NavigationTrackerPagerAdapterHelper {

  private final boolean isLoggedIn;
  private final WizardManager wizardManager;

  public WizardPagerAdapter(FragmentManager fragmentManager, Boolean isLoggedIn,
      WizardManager wizardManager) {
    super(fragmentManager);
    this.isLoggedIn = isLoggedIn;
    this.wizardManager = wizardManager;
  }

  @Override public Fragment getItem(int position) {
    return wizardManager.getItem(position);
  }

  @Override public int getCount() {
    return wizardManager.getCount(isLoggedIn);
  }

  public boolean isLoggedIn() {
    return isLoggedIn;
  }

  @Override public String getItemName(int position) {
    return getItem(position).getClass()
        .getSimpleName();
  }

  @Override public String getItemTag(int position) {
    return String.valueOf(position);
  }

  @Override public StoreContext getItemStore() {
    return StoreContext.home;
  }
}
