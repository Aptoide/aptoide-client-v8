package cm.aptoide.pt.v8engine.view.wizard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.v8engine.view.account.LoginSignUpFragment;

public class WizardPagerAdapter extends FragmentPagerAdapter {

  private static final int WIZARD_STEP_ONE_POSITION = 0;
  private static final int WIZARD_STEP_TWO_POSITION = 1;
  private static final int WIZARD_LOGIN_POSITION = 2;
  private final Account account;

  public WizardPagerAdapter(FragmentManager fragmentManager, Account account) {
    super(fragmentManager);
    this.account = account;
  }

  @Override public Fragment getItem(int position) {
    switch (position) {
      case WIZARD_STEP_ONE_POSITION:
        return WizardPageOneFragment.newInstance();
      case WIZARD_STEP_TWO_POSITION:
        return WizardPageTwoFragment.newInstance();
      case WIZARD_LOGIN_POSITION:
        return LoginSignUpFragment.newInstance(true, false, true);
      default:
        throw new IllegalArgumentException("Invalid wizard fragment position: " + position);
    }
  }

  @Override public int getCount() {
    if (account.isLoggedIn()) {
      return 2;
    }
    return 3;
  }
}
