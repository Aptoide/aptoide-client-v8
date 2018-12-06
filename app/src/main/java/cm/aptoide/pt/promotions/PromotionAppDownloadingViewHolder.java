package cm.aptoide.pt.promotions;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import java.text.DecimalFormat;

class PromotionAppDownloadingViewHolder extends GeneralPromotionAppsViewHolder {

  private TextView appName;
  private TextView appDescription;
  private ImageView appIcon;
  private TextView appSize;
  private TextView numberOfDownloads;
  private TextView rating;

  public PromotionAppDownloadingViewHolder(View itemView) {
    super(itemView);
    appIcon = itemView.findViewById(R.id.app_icon);
    appName = itemView.findViewById(R.id.app_name);
    appDescription = itemView.findViewById(R.id.app_description);
    numberOfDownloads = itemView.findViewById(R.id.number_of_downloads);
    appSize = itemView.findViewById(R.id.app_size);
    rating = itemView.findViewById(R.id.rating);
  }

  @Override public void setApp(PromotionViewApp app) {
    setAppCardHeader(app);
  }

  private void setAppCardHeader(PromotionViewApp app) {
    ImageLoader.with(itemView.getContext())
        .load(app.getAppIcon(), appIcon);
    appName.setText(app.getName());
    appDescription.setText(app.getDescription());
    appSize.setText(AptoideUtils.StringU.formatBytes(app.getSize(), false));
    if (app.getRating() == 0) {
      rating.setText(R.string.appcardview_title_no_stars);
    } else {
      rating.setText(new DecimalFormat("0.0").format(app.getRating()));
    }
    numberOfDownloads.setText(String.valueOf(app.getNumberOfDownloads()));
  }
}
