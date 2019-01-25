package cm.aptoide.pt.view.wizard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.LoginSignUpFragment;

import static cm.aptoide.pt.view.fragment.NavigationTrackFragment.SHOULD_REGISTER_VIEW;

/**
 * Created by tiagopedrinho on 29/11/2018.
 */

public class WizardFragmentProvider {

  private static final int WIZARD_STEP_ONE_POSITION = 0;
  private static final int WIZARD_STEP_TWO_POSITION = 1;
  private static final int WIZARD_LOGIN_POSITION = 2;

  public WizardFragmentProvider() {
  }

  public Fragment getItem(int position) {
    Fragment fragment;
    switch (position) {
      case WIZARD_STEP_ONE_POSITION:
        fragment = WizardPageOneFragment.newInstance();
        break;
      case WIZARD_STEP_TWO_POSITION:
        fragment = WizardPageTwoFragment.newInstance();
        break;
      case WIZARD_LOGIN_POSITION:
        fragment = LoginSignUpFragment.newInstance(true, false, true, true);
        break;
      default:
        throw new IllegalArgumentException("Invalid wizard fragment position: " + position);
    }
    fragment = setFragmentLogFlag(fragment);
    return fragment;
  }

  public int getCount(Boolean isLoggedIn) {
    if (isLoggedIn) {
      return 2;
    }
    return 3;
  }

  private Fragment setFragmentLogFlag(Fragment fragment) {
    Bundle bundle = fragment.getArguments();
    if (bundle == null) {
      bundle = new Bundle();
    }
    bundle.putBoolean(SHOULD_REGISTER_VIEW, false);
    fragment.setArguments(bundle);
    return fragment;
  }

  public Integer[] getTransitionColors() {
    return new Integer[] {
        R.color.wizard_color_1_blue, R.color.wizard_color_2_green, R.color.wizard_color_3_orange
    };
  }
}
