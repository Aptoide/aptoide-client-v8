package cm.aptoide.pt.home;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.app.Application;
import java.text.DecimalFormat;

class AppSecondaryInfoViewHolder {
  private final DecimalFormat oneDecimalFormatter;
  private final LinearLayout appcLayout;
  private final TextView appcText;
  private final LinearLayout ratingLayout;
  private final TextView rating;

  public AppSecondaryInfoViewHolder(View itemView, DecimalFormat oneDecimalFormatter) {
    this.oneDecimalFormatter = oneDecimalFormatter;
    appcLayout = (LinearLayout) itemView.findViewById(R.id.appc_info_layout);
    appcText = (TextView) itemView.findViewById(R.id.appc_text);
    ratingLayout = (LinearLayout) itemView.findViewById(R.id.rating_info_layout);
    rating = (TextView) itemView.findViewById(R.id.rating_label);
  }

  public void setInfo(Application app) {
    if (app.hasAppcAds()) {
      appcText.setText(R.string.appc_short_get_appc);
      appcLayout.setVisibility(View.VISIBLE);
    } else if (app.hasAppcIab()) {
      appcText.setText(R.string.appc_short_spend_appc);
      appcLayout.setVisibility(View.VISIBLE);
    } else {
      float rating = app.getRating();
      if (rating == 0) {
        this.rating.setText(R.string.appcardview_title_no_stars);
      } else {
        this.rating.setText(oneDecimalFormatter.format(rating));
      }
      ratingLayout.setVisibility(View.VISIBLE);
    }
  }
}
