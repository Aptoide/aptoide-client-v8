package cm.aptoide.pt.app.view;

import android.text.SpannableString;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppViewAppcInfoViewHolder {
  private LinearLayout appcBillingSupported;
  private View appcRewardView;
  private TextView appcRewardValue;

  public AppViewAppcInfoViewHolder(LinearLayout appcBillingSupported, View appcRewardView,
      TextView appcRewardValue) {
    this.appcBillingSupported = appcBillingSupported;
    this.appcRewardView = appcRewardView;
    this.appcRewardValue = appcRewardValue;
  }

  public void showInfo(boolean hasAdvertising, boolean hasBilling,
      SpannableString formattedMessage) {
    if (hasBilling) {
      this.appcBillingSupported.setVisibility(View.VISIBLE);
      this.appcRewardView.setVisibility(View.GONE);
    } else if (hasAdvertising) {
      this.appcRewardView.setVisibility(View.VISIBLE);
      this.appcRewardValue.setText(formattedMessage);
      this.appcBillingSupported.setVisibility(View.GONE);
    }
  }
}
