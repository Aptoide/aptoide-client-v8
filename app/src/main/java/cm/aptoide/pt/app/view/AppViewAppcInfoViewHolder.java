package cm.aptoide.pt.app.view;

import android.text.SpannableString;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppViewAppcInfoViewHolder {
  private LinearLayout appcBillingSupported;
  private View appcRewardView;
  private TextView appcRewardValue;
  private TextView appcRewardBilling;

  public AppViewAppcInfoViewHolder(LinearLayout appcBillingSupported, View appcRewardView,
      TextView appcRewardValue, TextView appcRewardBilling) {
    this.appcBillingSupported = appcBillingSupported;
    this.appcRewardView = appcRewardView;
    this.appcRewardValue = appcRewardValue;
    this.appcRewardBilling = appcRewardBilling;
  }

  public void showInfo(boolean hasAdvertising, boolean hasBilling,
      SpannableString formattedMessage) {
    if(hasAdvertising){
      this.appcRewardView.setVisibility(View.VISIBLE);
      this.appcRewardValue.setText(formattedMessage);
      this.appcBillingSupported.setVisibility(View.GONE);
      if(hasBilling){
        this.appcRewardBilling.setVisibility(View.VISIBLE);
      } else {
        this.appcRewardBilling.setVisibility(View.GONE);
      }
    } else if (hasBilling){
      this.appcBillingSupported.setVisibility(View.VISIBLE);
      this.appcRewardView.setVisibility(View.GONE);
    }
  }

  public void hideInfo() {
    this.appcBillingSupported.setVisibility(View.GONE);
    this.appcRewardView.setVisibility(View.GONE);
  }
}
