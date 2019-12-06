package cm.aptoide.pt.home.bundles.ads;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ads.data.Payout;
import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
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
  private final PublishSubject<AdHomeEvent> adClickedEvents;
  private final DecimalFormat oneDecimalFormatter;
  private final View appInfoLayout;
  private final View appcEarnLayout;
  private final TextView rewardTextView;

  public AdInBundleViewHolder(View itemView, PublishSubject<AdHomeEvent> adClickedEvents,
      DecimalFormat oneDecimalFormatter) {
    super(itemView);
    nameTextView = itemView.findViewById(R.id.name);
    iconView = itemView.findViewById(R.id.icon);
    rating = itemView.findViewById(R.id.rating_label);
    appcEarnLayout = itemView.findViewById(R.id.appc_earn_layout);
    appInfoLayout = itemView.findViewById(R.id.app_info_layout);
    rewardTextView = itemView.findViewById(R.id.reward_textview);
    this.adClickedEvents = adClickedEvents;
    this.oneDecimalFormatter = oneDecimalFormatter;
  }

  public void setApp(AdClick adClick, HomeBundle homeBundle, int bundlePosition, int position) {
    nameTextView.setText(adClick.getAd()
        .getAdTitle());
    ImageLoader.with(itemView.getContext())
        .loadWithRoundCorners(adClick.getAd()
            .getIconUrl(), 8, iconView, R.drawable.placeholder_square);
    if (adClick.getAd()
        .hasAppcPayout()) {
      Payout payout = adClick.getAd()
          .getAppcPayout();
      appInfoLayout.setVisibility(View.GONE);
      appcEarnLayout.setVisibility(View.VISIBLE);
      String earnText = itemView.getContext()
          .getString(R.string.poa_app_card_short,
              payout.getFiatSymbol() + oneDecimalFormatter.format(payout.getFiatAmount()));
      rewardTextView.setText(earnText);
    } else {
      appInfoLayout.setVisibility(View.VISIBLE);
      appcEarnLayout.setVisibility(View.GONE);
      float rating = adClick.getAd()
          .getStars();
      if (rating == 0) {
        this.rating.setText(R.string.appcardview_title_no_stars);
      } else {
        this.rating.setText(oneDecimalFormatter.format(rating));
      }
    }

    adClick.getAd()
        .registerClickableView(itemView);
    itemView.setOnClickListener(v -> adClickedEvents.onNext(
        new AdHomeEvent(adClick, position, homeBundle, bundlePosition, HomeEvent.Type.AD)));
  }
}
