package cm.aptoide.pt.view.wizard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import cm.aptoide.pt.NavigationTrackerPagerAdapterHelper;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;

public class WizardPagerAdapter extends FragmentPagerAdapter
    implements NavigationTrackerPagerAdapterHelper {

  private final boolean isLoggedIn;
  private final WizardFragmentProvider wizardFragmentProvider;

  public WizardPagerAdapter(FragmentManager fragmentManager, Boolean isLoggedIn,
      WizardFragmentProvider wizardFragmentProvider) {
    super(fragmentManager);
    this.isLoggedIn = isLoggedIn;
    this.wizardFragmentProvider = wizardFragmentProvider;
  }

  @Override public Fragment getItem(int position) {
    return wizardFragmentProvider.getItem(position);
  }

  @Override public int getCount() {
    return wizardFragmentProvider.getCount(isLoggedIn);
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
