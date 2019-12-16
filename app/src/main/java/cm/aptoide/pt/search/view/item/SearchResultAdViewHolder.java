package cm.aptoide.pt.search.view.item;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ads.data.Payout;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.model.SearchAdResultWrapper;
import cm.aptoide.pt.utils.AptoideUtils;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import java.text.DecimalFormat;

public class SearchResultAdViewHolder extends SearchResultItemView<SearchAdResult> {

  public static final int LAYOUT = R.layout.search_ad;
  private final PublishRelay<SearchAdResultWrapper> onItemViewClickRelay;

  private TextView name;
  private ImageView icon;
  private TextView downloadsTextView;
  private TextView ratingBar;
  private SearchAdResult adResult;
  private View appcEarnLayout;
  private TextView adTextView;
  private TextView rewardTextView;
  private DecimalFormat oneDecimalFormatter;

  public SearchResultAdViewHolder(View itemView,
      PublishRelay<SearchAdResultWrapper> onItemViewClickRelay, DecimalFormat oneDecimalFormatter) {
    super(itemView);
    this.onItemViewClickRelay = onItemViewClickRelay;
    this.oneDecimalFormatter = oneDecimalFormatter;
    bind(itemView);
  }

  private void bind(View itemView) {
    name = itemView.findViewById(R.id.app_name);
    icon = itemView.findViewById(R.id.app_icon);
    appcEarnLayout = itemView.findViewById(R.id.appc_earn_layout);
    adTextView = itemView.findViewById(R.id.ad_label);
    rewardTextView = itemView.findViewById(R.id.reward_textview);
    downloadsTextView = itemView.findViewById(R.id.downloads);
    ratingBar = itemView.findViewById(R.id.rating);
    RxView.clicks(itemView)
        .map(__ -> adResult)
        .subscribe(data -> onItemViewClickRelay.call(
            new SearchAdResultWrapper(data, getAdapterPosition())));
  }

  @Override public void setup(SearchAdResult searchAd) {
    final Context context = itemView.getContext();
    final Resources resources = itemView.getResources();
    this.adResult = searchAd;
    setName(searchAd);
    setIcon(searchAd, context);
    setDownloadsCount(searchAd, resources);
    setRatingStars(searchAd);

    if (searchAd.getPayout() != null) {
      adTextView.setVisibility(View.GONE);
      appcEarnLayout.setVisibility(View.VISIBLE);
      Payout payout = searchAd.getPayout();
      String earnText = itemView.getContext()
          .getString(R.string.poa_app_card_short,
              payout.getFiatSymbol() + oneDecimalFormatter.format(payout.getFiatAmount()));
      rewardTextView.setText(earnText);
    } else {
      adTextView.setVisibility(View.VISIBLE);
      appcEarnLayout.setVisibility(View.GONE);
    }
  }

  private void setIcon(SearchAdResult searchAd, Context context) {
    ImageLoader.with(context)
        .load(searchAd.getIcon(), icon);
  }

  private void setName(SearchAdResult searchAd) {
    name.setText(searchAd.getAppName());
  }

  private void setDownloadsCount(SearchAdResult searchAd, Resources resources) {
    String downloadNumber =
        AptoideUtils.StringU.withSuffix(searchAd.getTotalDownloads()) + " " + resources.getString(
            R.string.downloads);
    downloadsTextView.setText(downloadNumber);
  }

  private void setRatingStars(SearchAdResult searchAd) {
    float avg = searchAd.getStarRating();
    if (avg <= 0) {
      ratingBar.setText(R.string.appcardview_title_no_stars);
    } else {
      ratingBar.setVisibility(View.VISIBLE);
      ratingBar.setText(Float.toString(avg));
    }
  }
}
