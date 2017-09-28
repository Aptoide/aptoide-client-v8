package cm.aptoide.pt.search.view.item;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.Date;

public class SearchResultAdViewHolder extends SearchResultItemView<MinimalAd> {

  public static final int LAYOUT = R.layout.search_ad;
  private final PublishRelay<MinimalAd> onItemViewClickRelay;

  private TextView name;
  private ImageView icon;
  private TextView downloadsTextView;
  private RatingBar ratingBar;
  private TextView timeTextView;
  private MinimalAd minimalAd;

  public SearchResultAdViewHolder(View itemView, PublishRelay<MinimalAd> onItemViewClickRelay) {
    super(itemView);
    this.onItemViewClickRelay = onItemViewClickRelay;
    bind(itemView);
  }

  private void bind(View itemView) {
    name = (TextView) itemView.findViewById(R.id.name);
    icon = (ImageView) itemView.findViewById(R.id.icon);
    downloadsTextView = (TextView) itemView.findViewById(R.id.downloads);
    ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
    timeTextView = (TextView) itemView.findViewById(R.id.search_time);
    RxView.clicks(itemView)
        .map(__ -> minimalAd)
        .subscribe(data -> onItemViewClickRelay.call(data));
  }

  @Override public void setup(MinimalAd minimalAd) {
    final Context context = itemView.getContext();
    final Resources resources = itemView.getResources();
    this.minimalAd = minimalAd;
    setName(minimalAd);
    setIcon(minimalAd, context);
    setDownloadsCount(minimalAd, resources);
    setRatingStars(minimalAd);
    setModifiedDate(minimalAd, resources);
  }

  private void setIcon(MinimalAd minimalAd, Context context) {
    ImageLoader.with(context)
        .load(minimalAd.getIconPath(), icon);
  }

  private void setName(MinimalAd minimalAd) {
    name.setText(minimalAd.getName());
  }

  private void setDownloadsCount(MinimalAd minimalAd, Resources resources) {
    String downloadNumber =
        AptoideUtils.StringU.withSuffix(minimalAd.getDownloads()) + " " + resources.getString(
            R.string.downloads);
    downloadsTextView.setText(downloadNumber);
  }

  private void setRatingStars(MinimalAd minimalAd) {
    ratingBar.setRating(minimalAd.getStars());
  }

  private void setModifiedDate(MinimalAd minimalAd, Resources resources) {
    if (minimalAd.getModified() != null) {
      Date modified = new Date(minimalAd.getModified());
      String timeSinceUpdate = AptoideUtils.DateTimeU.getInstance(itemView.getContext())
          .getTimeDiffAll(itemView.getContext(), modified.getTime(), resources);
      if (timeSinceUpdate != null && !timeSinceUpdate.equals("")) {
        timeTextView.setText(timeSinceUpdate);
      }
    }
  }
}
