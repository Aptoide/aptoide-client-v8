package cm.aptoide.pt.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
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
  private final PublishSubject<GetAdsResponse.Ad> adClickedEvents;
  private final DecimalFormat oneDecimalFormatter;

  public AdInBundleViewHolder(View itemView, PublishSubject<GetAdsResponse.Ad> adClickedEvents,
      DecimalFormat oneDecimalFormatter) {
    super(itemView);
    nameTextView = ((TextView) itemView.findViewById(R.id.name));
    iconView = ((ImageView) itemView.findViewById(R.id.icon));
    rating = (TextView) itemView.findViewById(R.id.rating_label);
    this.adClickedEvents = adClickedEvents;
    this.oneDecimalFormatter = oneDecimalFormatter;
  }

  public void setApp(GetAdsResponse.Ad ad) {
    nameTextView.setText(ad.getData()
        .getName());
    ImageLoader.with(itemView.getContext())
        .loadWithRoundCorners(ad.getData()
            .getIcon(), 8, iconView, R.drawable.placeholder_square);
    float rating = ad.getData()
        .getStars();
    if (rating == 0) {
      this.rating.setText(R.string.appcardview_title_no_starts);
    } else {
      this.rating.setText(oneDecimalFormatter.format(rating));
    }
    itemView.setOnClickListener(v -> adClickedEvents.onNext(ad));
  }
}
