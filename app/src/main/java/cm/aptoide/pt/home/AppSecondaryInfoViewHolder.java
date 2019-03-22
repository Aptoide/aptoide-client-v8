package cm.aptoide.pt.home;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.R;
import java.text.DecimalFormat;

public class AppSecondaryInfoViewHolder {
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

  public void setInfo(boolean hasAppcBilling, float appRating, boolean showRating,
      boolean showBoth) {
    if (appcText != null) {
      appcText.setText(R.string.appc_short_spend_appc);
    }
    setRating(appRating);
    if (showBoth) {
      if (hasAppcBilling) {
        setAppcVisibility(View.VISIBLE);
      } else {
        setAppcVisibility(View.INVISIBLE);
      }
      setRatingVisibility(View.VISIBLE);
    } else {
      if (hasAppcBilling) {
        setAppcVisibility(View.VISIBLE);
        setRatingVisibility(View.INVISIBLE);
      } else if (showRating) {
        setAppcVisibility(View.INVISIBLE);
        setRatingVisibility(View.VISIBLE);
      } else {
        setAppcVisibility(View.INVISIBLE);
      }
    }
  }

  private void setAppcVisibility(int visibility) {
    if (appcLayout != null) {
      appcLayout.setVisibility(visibility);
    }
  }

  private void setRatingVisibility(int visibility) {
    if (ratingLayout != null) {
      ratingLayout.setVisibility(visibility);
    }
  }

  private void setRating(float rating) {
    if (this.rating != null) {
      if (rating == 0) {
        this.rating.setText(R.string.appcardview_title_no_stars);
      } else {
        this.rating.setText(oneDecimalFormatter.format(rating));
      }
    }
  }
}
