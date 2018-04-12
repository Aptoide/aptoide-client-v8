package cm.aptoide.pt.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import java.text.DecimalFormat;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 13/03/2018.
 */

class AdInBundleViewHolder extends RecyclerView.ViewHolder {
  private final TextView nameTextView;
  private final ImageView iconView;
  private final TextView rating;
  private final PublishSubject<AdClick> adClickedEvents;
  private final DecimalFormat oneDecimalFormatter;

  public AdInBundleViewHolder(View itemView, PublishSubject<AdClick> adClickedEvents,
      DecimalFormat oneDecimalFormatter) {
    super(itemView);
    nameTextView = ((TextView) itemView.findViewById(R.id.name));
    iconView = ((ImageView) itemView.findViewById(R.id.icon));
    rating = (TextView) itemView.findViewById(R.id.rating_label);
    this.adClickedEvents = adClickedEvents;
    this.oneDecimalFormatter = oneDecimalFormatter;
  }

  public void setApp(AdClick ad) {
    nameTextView.setText(ad.getAd()
        .getData()
        .getName());
    ImageLoader.with(itemView.getContext())
        .loadWithRoundCorners(ad.getAd()
            .getData()
            .getIcon(), 8, iconView, R.drawable.placeholder_square);
    float rating = ad.getAd()
        .getData()
        .getStars();
    if (rating == 0) {
      this.rating.setText(R.string.appcardview_title_no_stars);
    } else {
      this.rating.setText(oneDecimalFormatter.format(rating));
    }
    itemView.setOnClickListener(v -> adClickedEvents.onNext(ad));
  }
}
