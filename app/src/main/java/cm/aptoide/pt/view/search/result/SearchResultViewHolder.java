package cm.aptoide.pt.view.search.result;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.abtesting.ABTest;
import cm.aptoide.pt.abtesting.SearchTabOptions;
import cm.aptoide.pt.dataprovider.model.v7.ListSearchApps;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.utils.AptoideUtils;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Date;
import rx.subjects.PublishSubject;

public class SearchResultViewHolder extends RecyclerView.ViewHolder {

  public static final int LAYOUT = R.layout.search_app_row;
  private final PublishSubject<ListSearchApps.SearchAppsApp> openPopupMenu;
  private final PublishSubject<Pair<ListSearchApps.SearchAppsApp, String>> openAppView;

  private TextView nameTextView;
  private ImageView iconImageView;
  private TextView downloadsTextView;
  private RatingBar ratingBar;
  private ImageView overflowImageView;
  private TextView timeTextView;
  private TextView storeTextView;
  private ImageView icTrustedImageView;
  private View bottomView;
  private ListSearchApps.SearchAppsApp searchAppsApp;
  private String query;

  public SearchResultViewHolder(View itemView,
      PublishSubject<ListSearchApps.SearchAppsApp> openPopupMenu,
      PublishSubject<Pair<ListSearchApps.SearchAppsApp, String>> openAppView) {
    super(itemView);
    this.openPopupMenu = openPopupMenu;
    this.openAppView = openAppView;
    bindViews(itemView);
  }

  public void setupWith(ListSearchApps.SearchAppsApp searchAppsApp, String query) {
    this.searchAppsApp = searchAppsApp;
    this.query = query;

    nameTextView.setText(searchAppsApp.getName());
    String downloadNumber = AptoideUtils.StringU.withSuffix(searchAppsApp.getStats()
        .getPdownloads()) + " " + bottomView.getContext()
        .getString(R.string.downloads);
    downloadsTextView.setText(downloadNumber);

    float avg = searchAppsApp.getStats()
        .getRating()
        .getAvg();
    if (avg <= 0) {
      ratingBar.setVisibility(View.GONE);
    } else {
      ratingBar.setVisibility(View.VISIBLE);
      ratingBar.setRating(avg);
    }

    Date modified = searchAppsApp.getModified();
    if (modified != null) {
      String timeSinceUpdate = AptoideUtils.DateTimeU.getInstance(itemView.getContext())
          .getTimeDiffAll(itemView.getContext(), modified.getTime(), itemView.getResources());
      if (timeSinceUpdate != null && !timeSinceUpdate.equals("")) {
        timeTextView.setText(timeSinceUpdate);
      }
    }

    final StoreTheme theme = StoreTheme.get(searchAppsApp.getStore()
        .getAppearance()
        .getTheme());

    Drawable background = bottomView.getBackground();
    if (background instanceof ShapeDrawable) {
      ((ShapeDrawable) background).getPaint()
          .setColor(itemView.getContext()
              .getResources()
              .getColor(theme.getPrimaryColor()));
    } else if (background instanceof GradientDrawable) {
      ((GradientDrawable) background).setColor(itemView.getContext()
          .getResources()
          .getColor(theme.getPrimaryColor()));
    }

    background = storeTextView.getBackground();
    if (background instanceof ShapeDrawable) {
      ((ShapeDrawable) background).getPaint()
          .setColor(itemView.getContext()
              .getResources()
              .getColor(theme.getPrimaryColor()));
    } else if (background instanceof GradientDrawable) {
      ((GradientDrawable) background).setColor(itemView.getContext()
          .getResources()
          .getColor(theme.getPrimaryColor()));
    }

    storeTextView.setText(searchAppsApp.getStore()
        .getName());
    ImageLoader.with(itemView.getContext())
        .load(searchAppsApp.getIcon(), iconImageView);

    if (Malware.Rank.TRUSTED.equals(searchAppsApp.getFile()
        .getMalware()
        .getRank())) {
      icTrustedImageView.setVisibility(View.VISIBLE);
    } else {
      icTrustedImageView.setVisibility(View.GONE);
    }
  }

  private boolean isConvert(ABTest<SearchTabOptions> searchAbTest, boolean addSubscribedStores,
      boolean hasMultipleFragments) {
    return hasMultipleFragments && (addSubscribedStores == (searchAbTest.alternative()
        == SearchTabOptions.FOLLOWED_STORES));
  }

  private void bindViews(View itemView) {
    nameTextView = (TextView) itemView.findViewById(R.id.name);
    iconImageView = (ImageView) itemView.findViewById(R.id.icon);
    downloadsTextView = (TextView) itemView.findViewById(R.id.downloads);
    ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
    overflowImageView = (ImageView) itemView.findViewById(R.id.overflow);
    timeTextView = (TextView) itemView.findViewById(R.id.search_time);
    storeTextView = (TextView) itemView.findViewById(R.id.search_store);
    icTrustedImageView = (ImageView) itemView.findViewById(R.id.ic_trusted_search);
    bottomView = itemView.findViewById(R.id.bottom_view);

    RxView.clicks(overflowImageView)
        .doOnError(err -> openPopupMenu.onError(err))
        .subscribe(__ -> openPopupMenu.onNext(searchAppsApp));

    RxView.clicks(itemView)
        .doOnError(err -> openAppView.onError(err))
        .subscribe(v -> openAppView.onNext(new Pair<>(searchAppsApp, query)));
  }
}
