package cm.aptoide.pt.app.view;

import android.text.SpannableString;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Do not remove unused variables from this class,
 * it will break other flavors if you change method signatures
 **/
public class AppViewAppcInfoViewHolder {
  private final TextView appcRewardValue;
  private LinearLayout appcBillingSupported;
  private View appcRewardView;

  public AppViewAppcInfoViewHolder(LinearLayout appcBillingSupported, View appcRewardView,
      TextView appcRewardValue) {
    this.appcBillingSupported = appcBillingSupported;
    this.appcRewardView = appcRewardView;
    this.appcRewardValue = appcRewardValue;
  }

  public void showInfo(boolean hasAdvertising, boolean hasBilling,
      SpannableString spannableString) {
    this.appcBillingSupported.setVisibility(View.GONE);
    this.appcRewardView.setVisibility(View.GONE);
  }
}
