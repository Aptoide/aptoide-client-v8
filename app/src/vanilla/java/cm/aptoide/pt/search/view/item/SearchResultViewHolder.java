package cm.aptoide.pt.search.view.item;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.search.model.SearchAppResultWrapper;
import cm.aptoide.pt.utils.AptoideUtils;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import rx.subscriptions.CompositeSubscription;

public class SearchResultViewHolder extends SearchResultItemView<SearchAppResultWrapper> {

  public static final int LAYOUT = R.layout.search_app_row;
  private final PublishRelay<SearchAppResultWrapper> onItemViewClick;

  private TextView nameTextView;
  private ImageView iconImageView;
  private TextView downloadsTextView;
  private TextView ratingBar;
  private TextView storeTextView;
  private View bottomView;
  private SearchAppResultWrapper searchApp;
  private CompositeSubscription subscriptions;

  public SearchResultViewHolder(View itemView,
      PublishRelay<SearchAppResultWrapper> onItemViewClick) {
    super(itemView);
    subscriptions = new CompositeSubscription();
    this.onItemViewClick = onItemViewClick;
    bindViews(itemView);
  }

  @Override public void setup(SearchAppResultWrapper wrapper) {
    this.searchApp = wrapper;
    setAppName();
    setDownloadCount();
    setAverageValue();
    setStoreName();
    setIconView();
  }

  public void prepareToRecycle() {
    if (subscriptions.hasSubscriptions() && !subscriptions.isUnsubscribed()) {
      subscriptions.unsubscribe();
    }
  }

  private void setIconView() {
    ImageLoader.with(iconImageView.getContext())
        .load(searchApp.getSearchAppResult()
            .getIcon(), iconImageView);
  }

  private void setStoreName() {
    storeTextView.setText(searchApp.getSearchAppResult()
        .getStoreName());
  }

  private void setAverageValue() {
    float avg = searchApp.getSearchAppResult()
        .getAverageRating();
    if (avg <= 0) {
      ratingBar.setText(R.string.appcardview_title_no_stars);
    } else {
      ratingBar.setVisibility(View.VISIBLE);
      ratingBar.setText(Float.toString(avg));
    }
  }

  private void setDownloadCount() {
    String downloadNumber = String.format("%s %s", AptoideUtils.StringU.withSuffix(
        searchApp.getSearchAppResult()
            .getTotalDownloads()), bottomView.getContext()
        .getString(R.string.downloads));
    downloadsTextView.setText(downloadNumber);
  }

  private void setAppName() {
    nameTextView.setText(searchApp.getSearchAppResult()
        .getAppName());
  }

  private void bindViews(View itemView) {
    nameTextView = (TextView) itemView.findViewById(R.id.app_name);
    iconImageView = (ImageView) itemView.findViewById(R.id.app_icon);
    downloadsTextView = (TextView) itemView.findViewById(R.id.downloads);
    ratingBar = (TextView) itemView.findViewById(R.id.rating);
    storeTextView = (TextView) itemView.findViewById(R.id.store_name);
    bottomView = itemView;

    subscriptions.add(RxView.clicks(itemView)
        .map(__ -> searchApp)
        .subscribe(data -> onItemViewClick.call(data)));
  }
}
