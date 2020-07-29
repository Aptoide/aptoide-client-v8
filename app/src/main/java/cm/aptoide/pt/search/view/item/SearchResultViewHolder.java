package cm.aptoide.pt.search.view.item;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.aptoideviews.downloadprogressview.DownloadProgressView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.download.view.DownloadClick;
import cm.aptoide.pt.download.view.DownloadEvent;
import cm.aptoide.pt.download.view.DownloadStatusModel;
import cm.aptoide.pt.download.view.DownloadViewStatusHelper;
import cm.aptoide.pt.home.AppSecondaryInfoViewHolder;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.search.model.SearchAppResult;
import cm.aptoide.pt.search.model.SearchAppResultWrapper;
import cm.aptoide.pt.utils.AptoideUtils;
import java.text.DecimalFormat;
import rx.subjects.PublishSubject;

public class SearchResultViewHolder extends SearchResultItemView<SearchAppResult> {

  public static final int LAYOUT = R.layout.search_app_row;

  private final PublishSubject<SearchAppResultWrapper> onItemViewClick;
  private final PublishSubject<DownloadClick> downloadClickPublishSubject;
  private final AppSecondaryInfoViewHolder appInfoViewHolder;

  private final String query;
  private TextView nameTextView;
  private ImageView iconImageView;
  private TextView downloadsTextView;
  private TextView ratingBar;
  private TextView storeTextView;
  private SearchAppResult searchApp;
  private Button installButton;
  private DownloadProgressView downloadProgressView;

  private DownloadViewStatusHelper downloadViewStatusHelper;

  public SearchResultViewHolder(View itemView,
      PublishSubject<SearchAppResultWrapper> onItemViewClick,
      PublishSubject<DownloadClick> downloadClickPublishSubject, String query) {
    super(itemView);
    this.onItemViewClick = onItemViewClick;
    this.query = query;
    this.downloadClickPublishSubject = downloadClickPublishSubject;
    this.downloadViewStatusHelper = new DownloadViewStatusHelper(itemView.getContext());
    appInfoViewHolder = new AppSecondaryInfoViewHolder(itemView, new DecimalFormat("0.0"));
    bindViews(itemView);
  }

  @Override public void setup(SearchAppResult result) {
    this.searchApp = result;
    appInfoViewHolder.setInfo(result.hasBilling() || result.hasAdvertising(),
        result.getAverageRating(), false, false);
    setAppName();
    setDownloadCount();
    setAverageValue();
    setStoreName();
    setIconView();
    setDownloadStatus(searchApp);
  }

  public void setDownloadStatus(SearchAppResult app) {
    DownloadStatusModel downloadModel = app.getDownloadModel();
    if (app.isHighlightedResult() && downloadModel != null) {
      downloadViewStatusHelper.setDownloadStatus(app, installButton, downloadProgressView);
    } else {
      installButton.setVisibility(View.GONE);
      downloadProgressView.setVisibility(View.GONE);
    }
  }

  private void setIconView() {
    ImageLoader.with(iconImageView.getContext())
        .load(searchApp.getIcon(), iconImageView);
  }

  private void setStoreName() {
    storeTextView.setText(searchApp.getStoreName());
  }

  private void setAverageValue() {
    float avg = searchApp.getAverageRating();
    if (avg <= 0) {
      ratingBar.setText(R.string.appcardview_title_no_stars);
    } else {
      ratingBar.setVisibility(View.VISIBLE);
      ratingBar.setText(new DecimalFormat("0.0").format(avg));
    }
  }

  private void setDownloadCount() {
    downloadsTextView.setText(AptoideUtils.StringU.withSuffix(searchApp.getTotalDownloads()));
  }

  private void setAppName() {
    nameTextView.setText(searchApp.getAppName());
  }

  private void bindViews(View itemView) {
    nameTextView = itemView.findViewById(R.id.app_name);
    iconImageView = itemView.findViewById(R.id.app_icon);
    downloadsTextView = itemView.findViewById(R.id.downloads);
    ratingBar = itemView.findViewById(R.id.rating);
    storeTextView = itemView.findViewById(R.id.store_name);
    installButton = itemView.findViewById(R.id.install_button);
    downloadProgressView = itemView.findViewById(R.id.download_progress_view);

    itemView.setOnClickListener(v -> {
      onItemViewClick.onNext(new SearchAppResultWrapper(query, searchApp, getAdapterPosition()));
    });
    installButton.setOnClickListener(v -> {
      downloadClickPublishSubject.onNext(new DownloadClick(searchApp, DownloadEvent.INSTALL));
    });
    downloadProgressView.setEventListener(action -> {
      switch (action.getType()) {
        case CANCEL:
          downloadClickPublishSubject.onNext(new DownloadClick(searchApp, DownloadEvent.CANCEL));
          break;
        case RESUME:
          downloadClickPublishSubject.onNext(new DownloadClick(searchApp, DownloadEvent.RESUME));
          break;
        case PAUSE:
          downloadClickPublishSubject.onNext(new DownloadClick(searchApp, DownloadEvent.PAUSE));
          break;
      }
    });
  }
}
