package cm.aptoide.pt.search.view.item;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.utils.AptoideUtils;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.Date;

public class SearchResultViewHolder extends SearchResultItemView<SearchApp> {

  public static final int LAYOUT = R.layout.search_app_row;
  private final PublishRelay<SearchApp> onItemViewClick;
  private final PublishRelay<Pair<SearchApp, android.view.View>> onOpenPopupMenuClick;

  private TextView nameTextView;
  private ImageView iconImageView;
  private TextView downloadsTextView;
  private RatingBar ratingBar;
  private TextView timeTextView;
  private TextView storeTextView;
  private ImageView icTrustedImageView;
  private ImageView overflowImageView;
  private View bottomView;
  private SearchApp searchApp;

  public SearchResultViewHolder(View itemView, PublishRelay<SearchApp> onItemViewClick,
      PublishRelay<Pair<SearchApp, android.view.View>> onOpenPopupMenuClick) {
    super(itemView);
    this.onItemViewClick = onItemViewClick;
    this.onOpenPopupMenuClick = onOpenPopupMenuClick;
    bindViews(itemView);
  }

  @Override public void setup(SearchApp searchApp) {
    this.searchApp = searchApp;
    setAppName();
    setDownloadCount();
    setAverageValue();
    setDateModified();
    setBackground();
    setStoreName();
    setIconView();
    setTrustedBadge();
  }

  private void setTrustedBadge() {
    if (Malware.Rank.TRUSTED.equals(searchApp.getFile()
        .getMalware()
        .getRank())) {
      icTrustedImageView.setVisibility(View.VISIBLE);
    } else {
      icTrustedImageView.setVisibility(View.GONE);
    }
  }

  private void setIconView() {
    ImageLoader.with(iconImageView.getContext())
        .load(searchApp.getIcon(), iconImageView);
  }

  private void setStoreName() {
    storeTextView.setText(searchApp.getStore()
        .getName());
  }

  private void setBackground() {
    final Resources resources = itemView.getResources();
    final StoreTheme theme = StoreTheme.get(searchApp.getStore()
        .getAppearance()
        .getTheme());
    Drawable background = bottomView.getBackground();

    if (background instanceof ShapeDrawable) {
      ((ShapeDrawable) background).getPaint()
          .setColor(resources.getColor(theme.getPrimaryColor()));
    } else if (background instanceof GradientDrawable) {
      ((GradientDrawable) background).setColor(resources.getColor(theme.getPrimaryColor()));
    }

    background = storeTextView.getBackground();
    if (background instanceof ShapeDrawable) {
      ((ShapeDrawable) background).getPaint()
          .setColor(resources.getColor(theme.getPrimaryColor()));
    } else if (background instanceof GradientDrawable) {
      ((GradientDrawable) background).setColor(resources.getColor(theme.getPrimaryColor()));
    }
  }

  private void setDateModified() {
    Date modified = searchApp.getModified();
    if (modified != null) {
      final Resources resources = itemView.getResources();
      final Context context = itemView.getContext();
      String timeSinceUpdate = AptoideUtils.DateTimeU.getInstance(context)
          .getTimeDiffAll(context, modified.getTime(), resources);
      if (timeSinceUpdate != null && !timeSinceUpdate.equals("")) {
        timeTextView.setText(timeSinceUpdate);
      }
    }
  }

  private void setAverageValue() {
    float avg = searchApp.getStats()
        .getRating()
        .getAvg();
    if (avg <= 0) {
      ratingBar.setVisibility(View.GONE);
    } else {
      ratingBar.setVisibility(View.VISIBLE);
      ratingBar.setRating(avg);
    }
  }

  private void setDownloadCount() {
    String downloadNumber = AptoideUtils.StringU.withSuffix(searchApp.getStats()
        .getPdownloads()) + " " + bottomView.getContext()
        .getString(R.string.downloads);
    downloadsTextView.setText(downloadNumber);
  }

  private void setAppName() {
    nameTextView.setText(searchApp.getName());
  }

  private void bindViews(View itemView) {
    nameTextView = (TextView) itemView.findViewById(R.id.name);
    iconImageView = (ImageView) itemView.findViewById(R.id.icon);
    downloadsTextView = (TextView) itemView.findViewById(R.id.downloads);
    ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
    timeTextView = (TextView) itemView.findViewById(R.id.search_time);
    storeTextView = (TextView) itemView.findViewById(R.id.search_store);
    icTrustedImageView = (ImageView) itemView.findViewById(R.id.ic_trusted_search);
    bottomView = itemView.findViewById(R.id.bottom_view);
    overflowImageView = (ImageView) itemView.findViewById(R.id.overflow);

    RxView.clicks(itemView)
        .map(__ -> searchApp)
        .subscribe(data -> onItemViewClick.call(data));

    RxView.clicks(overflowImageView)
        .map(__ -> new Pair<>(searchApp, (View) overflowImageView))
        .subscribe(data -> onOpenPopupMenuClick.call(data));
  }
}
