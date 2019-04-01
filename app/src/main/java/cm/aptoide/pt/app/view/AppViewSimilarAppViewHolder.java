package cm.aptoide.pt.app.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.AppViewSimilarApp;
import cm.aptoide.pt.app.view.similar.SimilarAppClickEvent;
import cm.aptoide.pt.networking.image.ImageLoader;
import java.text.DecimalFormat;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 11/05/18.
 */

public class AppViewSimilarAppViewHolder extends RecyclerView.ViewHolder {

  private final LinearLayout appInfoLayout;
  private final LinearLayout appcInfoLayout;
  private final TextView nameTextView;
  private final ImageView iconView;
  private final TextView rating;
  private final TextView adLabel;

  private final View itemView;
  private DecimalFormat oneDecimalFormatter;
  private PublishSubject<SimilarAppClickEvent> appClicked;

  public AppViewSimilarAppViewHolder(View itemView, DecimalFormat oneDecimalFormatter,
      PublishSubject<SimilarAppClickEvent> appClicked) {
    super(itemView);
    this.itemView = itemView;
    this.oneDecimalFormatter = oneDecimalFormatter;
    this.appClicked = appClicked;

    appInfoLayout = itemView.findViewById(R.id.app_info_layout);
    appcInfoLayout = itemView.findViewById(R.id.appc_info_layout);
    nameTextView = itemView.findViewById(R.id.name);
    iconView = itemView.findViewById(R.id.icon);
    rating = itemView.findViewById(R.id.rating_label);
    adLabel = itemView.findViewById(R.id.ad_label);
  }

  public void setSimilarApp(AppViewSimilarApp app, AppViewSimilarAppsAdapter.SimilarAppType type) {
    if (app.isAd()) {
      adLabel.setVisibility(View.VISIBLE);
      nameTextView.setText(app.getAd()
          .getAdTitle());
      ImageLoader.with(itemView.getContext())
          .loadWithRoundCorners(app.getAd()
              .getIconUrl(), 8, iconView, R.drawable.placeholder_square);
      app.getAd()
          .registerClickableView(itemView);
      float rating = app.getAd()
          .getStars();
      if (rating == 0) {
        this.rating.setText(R.string.appcardview_title_no_stars);
      } else {
        this.rating.setText(oneDecimalFormatter.format(rating));
      }
      itemView.setOnClickListener(
          view -> appClicked.onNext(new SimilarAppClickEvent(app, type, getLayoutPosition())));
    } else if (app.getApp() != null) {
      adLabel.setVisibility(View.GONE);
      nameTextView.setText(app.getApp()
          .getName());
      ImageLoader.with(itemView.getContext())
          .loadWithRoundCorners(app.getApp()
              .getIcon(), 8, iconView, R.drawable.placeholder_square);
      if (app.getApp()
          .hasAppcBilling()) {
        appcInfoLayout.setVisibility(View.VISIBLE);
        appInfoLayout.setVisibility(View.GONE);
      } else {
        appcInfoLayout.setVisibility(View.GONE);
        appInfoLayout.setVisibility(View.VISIBLE);
        float rating = app.getApp()
            .getRating();
        if (rating == 0) {
          this.rating.setText(R.string.appcardview_title_no_stars);
        } else {
          this.rating.setText(oneDecimalFormatter.format(rating));
        }
      }

      itemView.setOnClickListener(
          view -> appClicked.onNext(new SimilarAppClickEvent(app, type, getLayoutPosition())));
    }
  }
}
