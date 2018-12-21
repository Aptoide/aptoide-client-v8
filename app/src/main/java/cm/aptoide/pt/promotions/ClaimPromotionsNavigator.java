package cm.aptoide.pt.promotions;

import android.content.Intent;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.Result;

public class ClaimPromotionsNavigator {
  private final FragmentNavigator fragmentNavigator;

  public ClaimPromotionsNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void popDialogWithResult(String packageName, int status) {
    fragmentNavigator.popDialogWithResult(new Result(PromotionsNavigator.CLAIM_REQUEST_CODE, status,
        new Intent().setPackage(packageName)));
  }
}
