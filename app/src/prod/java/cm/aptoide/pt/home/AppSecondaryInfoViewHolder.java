package cm.aptoide.pt.home;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.R;
import java.text.DecimalFormat;

public class AppSecondaryInfoViewHolder {
  private final TextView rating;
  private final DecimalFormat oneDecimalFormatter;
  private final LinearLayout ratingLayout;

  public AppSecondaryInfoViewHolder(View itemView, DecimalFormat oneDecimalFormatter) {
    ratingLayout = (LinearLayout) itemView.findViewById(R.id.rating_info_layout);
    rating = (TextView) itemView.findViewById(R.id.rating_label);
    this.oneDecimalFormatter = oneDecimalFormatter;
  }

  public void setInfo(boolean hasAdvertising, boolean hasBilling, float averageRating,
      boolean showRating) {
    if (averageRating == 0) {
      this.rating.setText(R.string.appcardview_title_no_stars);
    } else {
      this.rating.setText(oneDecimalFormatter.format(averageRating));
    }
    ratingLayout.setVisibility(View.VISIBLE);
  }
}
