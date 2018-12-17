package cm.aptoide.pt.promotions;

import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.Result;

public class ClaimPromotionsNavigator {
  private final FragmentNavigator fragmentNavigator;

  public ClaimPromotionsNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void popDialogWithResult(Result result) {
    fragmentNavigator.popDialogWithResult(result);
  }
}
