package cm.aptoide.pt.app.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.AppViewSimilarApp;
import cm.aptoide.pt.networking.image.ImageLoader;
import java.text.DecimalFormat;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 11/05/18.
 */

public class AppViewSimilarAppViewHolder extends RecyclerView.ViewHolder {

  private final TextView nameTextView;
  private final ImageView iconView;
  private final TextView rating;
  private final TextView adLabel;

  private DecimalFormat oneDecimalFormatter;
  private PublishSubject<SimilarAppClickEvent> appClicked;

  public AppViewSimilarAppViewHolder(View itemView, DecimalFormat oneDecimalFormatter,
      PublishSubject<SimilarAppClickEvent> appClicked) {
    super(itemView);
    this.oneDecimalFormatter = oneDecimalFormatter;
    this.appClicked = appClicked;

    nameTextView = ((TextView) itemView.findViewById(R.id.name));
    iconView = ((ImageView) itemView.findViewById(R.id.icon));
    rating = (TextView) itemView.findViewById(R.id.rating_label);
    adLabel = (TextView) itemView.findViewById(R.id.ad_label);
  }

  public void setSimilarApp(AppViewSimilarApp app, int position) {
    if (app.isAd()) {
      adLabel.setVisibility(View.VISIBLE);
      nameTextView.setText(app.getAd()
          .getName());
      ImageLoader.with(itemView.getContext())
          .loadWithRoundCorners(app.getAd()
              .getIconPath(), 8, iconView, R.drawable.placeholder_square);
      float rating = app.getAd()
          .getStars();
      if (rating == 0) {
        this.rating.setText(R.string.appcardview_title_no_stars);
      } else {
        this.rating.setText(oneDecimalFormatter.format(rating));
      }
      itemView.setOnClickListener(
          view -> appClicked.onNext(new SimilarAppClickEvent(app, position)));
    } else {
      adLabel.setVisibility(View.GONE);
      nameTextView.setText(app.getApp()
          .getName());
      ImageLoader.with(itemView.getContext())
          .loadWithRoundCorners(app.getApp()
              .getIcon(), 8, iconView, R.drawable.placeholder_square);
      float rating = app.getApp()
          .getRating();
      if (rating == 0) {
        this.rating.setText(R.string.appcardview_title_no_stars);
      } else {
        this.rating.setText(oneDecimalFormatter.format(rating));
      }
      itemView.setOnClickListener(
          view -> appClicked.onNext(new SimilarAppClickEvent(app, position)));
    }
  }
}
