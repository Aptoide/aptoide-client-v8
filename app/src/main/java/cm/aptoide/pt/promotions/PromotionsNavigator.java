package cm.aptoide.pt.promotions;

import android.os.Bundle;
import cm.aptoide.pt.navigator.FragmentNavigator;

public class PromotionsNavigator {

  private final FragmentNavigator fragmentNavigator;

  public PromotionsNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToClaimDialog(String packageName) {
    ClaimPromotionDialogFragment fragment = new ClaimPromotionDialogFragment();
    Bundle args = new Bundle();
    args.putString("package_name", packageName);
    fragment.setArguments(args);
    fragmentNavigator.navigateToDialogFragment(fragment);
  }
}

