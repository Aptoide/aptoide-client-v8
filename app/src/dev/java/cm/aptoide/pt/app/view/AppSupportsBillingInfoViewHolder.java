package cm.aptoide.pt.app.view;

import android.view.View;
import android.widget.LinearLayout;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;

public class AppSupportsBillingInfoViewHolder {
  private LinearLayout appcBillingSupported;

  public AppSupportsBillingInfoViewHolder(LinearLayout appcBillingSupported) {
    this.appcBillingSupported = appcBillingSupported;
  }

  public void showInfo() {
    this.appcBillingSupported.setVisibility(View.VISIBLE);
  }

  public Observable<Void> clickAppcInfo() {
    return RxView.clicks(appcBillingSupported);
  }
}
