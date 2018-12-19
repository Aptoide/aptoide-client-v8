package cm.aptoide.pt.promotions;

import android.app.Activity;
import android.os.Bundle;
import cm.aptoide.pt.navigator.FragmentNavigator;
import rx.Observable;

public class PromotionsNavigator {
  static final int CLAIM_REQUEST_CODE = 6666;

  private final FragmentNavigator fragmentNavigator;
  private ClaimPromotionDialogFragment fragment;

  public PromotionsNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToClaimDialog(String packageName) {
    fragment = new ClaimPromotionDialogFragment();
    Bundle args = new Bundle();
    args.putString("package_name", packageName);
    fragment.setArguments(args);
    fragmentNavigator.navigateToDialogForResult(fragment, CLAIM_REQUEST_CODE);
  }

  public Observable<ClaimDialogResultWrapper> claimDialogResults() {
    return fragmentNavigator.results(CLAIM_REQUEST_CODE)
        .map(result -> new ClaimDialogResultWrapper(result.getData() != null ? result.getData()
            .getPackage() : "", result.getResultCode() == Activity.RESULT_OK));
  }
}

