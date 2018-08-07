package cm.aptoide.pt.app.view;

import android.text.SpannableString;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppViewAppcInfoViewHolder {
  private LinearLayout appcBillingSupported;
  private View appcRewardView;
  private TextView appcRewardValue;
  private MessageType infoType = MessageType.NONE;

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
      infoType = MessageType.BILLING;
    } else if (hasAdvertising) {
      this.appcRewardView.setVisibility(View.VISIBLE);
      this.appcRewardValue.setText(formattedMessage);
      this.appcBillingSupported.setVisibility(View.GONE);
      infoType = MessageType.ADVERTISING;
    }
  }

  public void showInfo() {
    if (infoType != MessageType.NONE) {
      if (infoType == MessageType.BILLING) {
        this.appcBillingSupported.setVisibility(View.VISIBLE);
        this.appcRewardView.setVisibility(View.GONE);
      } else {
        this.appcRewardView.setVisibility(View.VISIBLE);
        this.appcBillingSupported.setVisibility(View.GONE);
      }
    }
  }

  public void hideInfo() {
    this.appcBillingSupported.setVisibility(View.GONE);
    this.appcRewardView.setVisibility(View.GONE);
  }

  public enum MessageType {
    NONE, BILLING, ADVERTISING
  }
}
