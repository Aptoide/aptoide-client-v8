package cm.aptoide.pt.notification;

import android.content.Context;
import cm.aptoide.pt.R;

public class AppcPromotionNotificationStringProvider {

  private final Context context;

  public AppcPromotionNotificationStringProvider(Context context) {
    this.context = context;
  }

  public String getNotificationTitle() {
    return context.getString(R.string.promo_update2appc_claim_notification_title);
  }

  public String getNotificationBody() {
    return context.getString(R.string.promo_update2appc_claim_notification_body);
  }
}
