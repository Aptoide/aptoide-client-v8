package cm.aptoide.pt.view.wizard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.NavigationTrackerPagerAdapterHelper;
import cm.aptoide.pt.account.view.LoginSignUpFragment;
import cm.aptoide.pt.analytics.AptoideNavigationTracker;

public class WizardPagerAdapter extends FragmentPagerAdapter
    implements NavigationTrackerPagerAdapterHelper {

  private static final int WIZARD_STEP_ONE_POSITION = 0;
  private static final int WIZARD_STEP_TWO_POSITION = 1;
  private static final int WIZARD_LOGIN_POSITION = 2;
  private final Account account;

  public WizardPagerAdapter(FragmentManager fragmentManager, Account account) {
    super(fragmentManager);
    this.account = account;
  }

  @Override public Fragment getItem(int position) {
    Fragment fragment;
    switch (position) {
      case WIZARD_STEP_ONE_POSITION:
        fragment = WizardPageOneFragment.newInstance();
        break;
      case WIZARD_STEP_TWO_POSITION:
        fragment = WizardPageTwoFragment.newInstance();
        break;
      case WIZARD_LOGIN_POSITION:
        fragment = LoginSignUpFragment.newInstance(true, false, true);
        break;
      default:
        throw new IllegalArgumentException("Invalid wizard fragment position: " + position);
    }
    fragment = setFragmentLogFlag(fragment);
    return fragment;
  }

  @Override public int getCount() {
    if (account.isLoggedIn()) {
      return 2;
    }
    return 3;
  }

  @Override public String getItemName(int position) {
    return getItem(position).getClass()
        .getSimpleName();
  }

  private Fragment setFragmentLogFlag(Fragment fragment) {
    Bundle bundle = fragment.getArguments();
    if (bundle == null) {
      bundle = new Bundle();
    }
    bundle.putBoolean(AptoideNavigationTracker.DO_NOT_REGISTER_VIEW, true);
    fragment.setArguments(bundle);
    return fragment;
  }
}
