package cm.aptoide.pt.search.view.item;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.model.SearchAdResultWrapper;
import cm.aptoide.pt.utils.AptoideUtils;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;

public class SearchResultAdViewHolder extends SearchResultItemView<SearchAdResultWrapper> {

  public static final int LAYOUT = R.layout.search_ad;
  private final PublishRelay<SearchAdResultWrapper> onItemViewClickRelay;

  private TextView name;
  private ImageView icon;
  private TextView downloadsTextView;
  private TextView ratingBar;
  private SearchAdResultWrapper adResult;

  public SearchResultAdViewHolder(View itemView,
      PublishRelay<SearchAdResultWrapper> onItemViewClickRelay) {
    super(itemView);
    this.onItemViewClickRelay = onItemViewClickRelay;
    bind(itemView);
  }

  private void bind(View itemView) {
    name = (TextView) itemView.findViewById(R.id.app_name);
    icon = (ImageView) itemView.findViewById(R.id.app_icon);
    downloadsTextView = (TextView) itemView.findViewById(R.id.downloads);
    ratingBar = (TextView) itemView.findViewById(R.id.rating);
    RxView.clicks(itemView)
        .map(__ -> adResult)
        .subscribe(data -> onItemViewClickRelay.call(data));
  }

  @Override public void setup(SearchAdResultWrapper searchAd) {
    final Context context = itemView.getContext();
    final Resources resources = itemView.getResources();
    this.adResult = searchAd;
    setName(searchAd.getSearchAdResult());
    setIcon(searchAd.getSearchAdResult(), context);
    setDownloadsCount(searchAd.getSearchAdResult(), resources);
    setRatingStars(searchAd.getSearchAdResult());
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
