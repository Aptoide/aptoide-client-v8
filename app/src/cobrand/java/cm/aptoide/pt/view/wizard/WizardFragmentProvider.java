package cm.aptoide.pt.view.wizard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.LoginSignUpFragment;
import cm.aptoide.pt.store.StoreTheme;

import static cm.aptoide.pt.view.fragment.NavigationTrackFragment.SHOULD_REGISTER_VIEW;

/**
 * Created by tiagopedrinho on 29/11/2018.
 */

public class WizardFragmentProvider {

  private static final int WIZARD_STEP_ONE_POSITION = 0;
  private static final int WIZARD_LOGIN_POSITION = 1;
  private final String theme;

  public WizardFragmentProvider(String theme) {
    this.theme = theme;
  }

  public Fragment getItem(int position) {
    Fragment fragment;
    switch (position) {
      case WIZARD_STEP_ONE_POSITION:
        fragment = WizardPageOneFragment.newInstance();
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
      return 1;
    }
    return 2;
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
    int lastPageColor = R.color.wizard_color_3_orange;
    if (!theme.equals(StoreTheme.DEFAULT.toString()
        .toLowerCase())) {
      lastPageColor = StoreTheme.get(theme)
          .getPrimaryColor();
    }
    return new Integer[] {
        R.color.wizard_color_1_blue, lastPageColor
    };
  }
}
