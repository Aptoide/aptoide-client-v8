package cm.aptoide.pt.app.view;

import android.view.View;
import android.widget.LinearLayout;

public class AppSupportsBillingInfoViewHolder {
  private LinearLayout appcBillingSupported;

  public AppSupportsBillingInfoViewHolder(LinearLayout appcBillingSupported) {
    this.appcBillingSupported = appcBillingSupported;
  }

  public void showInfo() {
    this.appcBillingSupported.setVisibility(View.VISIBLE);
  }
}
