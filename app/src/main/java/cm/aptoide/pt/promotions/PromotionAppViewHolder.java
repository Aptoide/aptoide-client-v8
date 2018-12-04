package cm.aptoide.pt.promotions;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import java.text.DecimalFormat;

import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIM;
import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIMED;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNLOAD;
import static cm.aptoide.pt.promotions.PromotionsAdapter.INSTALL;
import static cm.aptoide.pt.promotions.PromotionsAdapter.UPDATE;

public class PromotionAppViewHolder extends GeneralPromotionAppsViewHolder {

  private int appState;
  private TextView appName;
  private TextView appDescription;
  private ImageView appIcon;
  private TextView appSize;
  private TextView numberOfDownloads;
  private TextView rating;
  private Button promotionAction;

  public PromotionAppViewHolder(View itemView, int appState) {
    super(itemView);
    this.appState = appState;
    appIcon = itemView.findViewById(R.id.app_icon);
    appName = itemView.findViewById(R.id.app_name);
    appDescription = itemView.findViewById(R.id.app_description);
    numberOfDownloads = itemView.findViewById(R.id.number_of_downloads);
    appSize = itemView.findViewById(R.id.app_size);
    rating = itemView.findViewById(R.id.rating);
    promotionAction = itemView.findViewById(R.id.promotion_app_action_button);
  }

  @Override public void setApp(PromotionApp app) {
    setAppCardHeader(app);
    promotionAction.setText(getButtonMessage(appState));
  }

  private String getButtonMessage(int appState) {
    String message = "";
    switch (appState) {
      case UPDATE:
        message = "Update to get 25 APPC";
        break;
      case DOWNLOAD:
        message = "Install to get 25 APPC";
        break;
      case INSTALL:
        message = "Install to get 25 APPC";
        break;
      case CLAIM:
        message = "Claim your 25 appc";
        break;
      case CLAIMED:
        message = "Claimed";
        break;
      default:
        throw new IllegalArgumentException("Wrong view type of promotion app");
    }
    return message;
  }

  private void setAppCardHeader(PromotionApp app) {
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
