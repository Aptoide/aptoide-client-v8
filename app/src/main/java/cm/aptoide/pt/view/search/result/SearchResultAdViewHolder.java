package cm.aptoide.pt.view.search.result;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.ItemView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.Date;

public class SearchResultAdViewHolder extends RecyclerView.ViewHolder implements
    ItemView<MinimalAd> {

  public static final int LAYOUT = R.layout.search_ad;
  private final PublishRelay<Void> onItemViewClickRelay;

  private TextView name;
  private ImageView icon;
  private TextView downloadsTextView;
  private RatingBar ratingBar;
  private TextView timeTextView;

  public SearchResultAdViewHolder(View itemView, PublishRelay<Void> onItemViewClickRelay) {
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
        .subscribe(__ -> onItemViewClickRelay.call(__));
    /*
    itemView.setOnClickListener(view -> {
      getFragmentNavigator().navigateTo(AptoideApplication.getFragmentProvider()
          .newAppViewFragment(minimalAd), true);
    });
     */
  }

  @Override public void setup(MinimalAd minimalAd) {
    final Context context = itemView.getContext();
    final Resources resources = itemView.getResources();
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
