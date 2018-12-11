package cm.aptoide.pt.promotions;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import java.text.DecimalFormat;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIM;
import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIMED;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNLOAD;
import static cm.aptoide.pt.promotions.PromotionsAdapter.INSTALL;
import static cm.aptoide.pt.promotions.PromotionsAdapter.UPDATE;

public class PromotionAppViewHolder extends RecyclerView.ViewHolder {

  private final PublishSubject<PromotionAppClick> promotionAppClick;
  private int appState;
  private TextView appName;
  private TextView appDescription;
  private ImageView appIcon;
  private TextView appSize;
  private TextView numberOfDownloads;
  private TextView rating;
  private Button promotionAction;

  public PromotionAppViewHolder(View itemView, int appState,
      PublishSubject<PromotionAppClick> promotionAppClick) {
    super(itemView);
    this.appState = appState;
    appIcon = itemView.findViewById(R.id.app_icon);
    appName = itemView.findViewById(R.id.app_name);
    appDescription = itemView.findViewById(R.id.app_description);
    numberOfDownloads = itemView.findViewById(R.id.number_of_downloads);
    appSize = itemView.findViewById(R.id.app_size);
    rating = itemView.findViewById(R.id.rating);
    promotionAction = itemView.findViewById(R.id.promotion_app_action_button);
    this.promotionAppClick = promotionAppClick;
  }

  public void setApp(PromotionViewApp app, boolean isWalletInstalled) {
    setAppCardHeader(app);
    promotionAction.setText(itemView.getContext()
        .getString(getButtonMessage(appState), app.getAppcValue()));

    if (!isWalletInstalled) {
      lockButton(true);
    } else {

      if (appState == CLAIMED) {
        // TODO: 12/7/18 set button disabled state
      } else {
        lockButton(false);
        promotionAction.setOnClickListener(
            __ -> promotionAppClick.onNext(new PromotionAppClick(app, getClickType(appState))));
      }
    }
  }

  private void lockButton(boolean lock) {
    if (lock) {
      promotionAction.setEnabled(false);
      promotionAction.setBackgroundColor(itemView.getContext()
          .getResources()
          .getColor(R.color.grey_fog_light));
    } else {
      TypedValue resultValue = new TypedValue();
      itemView.getContext()
          .getTheme()
          .resolveAttribute(R.attr.installButtonBackground, resultValue, true);

      promotionAction.setEnabled(true);
      if (resultValue.resourceId != 0) {
        promotionAction.setBackgroundResource(resultValue.resourceId);
      } else {
        promotionAction.setBackgroundColor(itemView.getContext()
            .getResources()
            .getColor(R.color.orange));
      }
    }
  }

  private PromotionAppClick.ClickType getClickType(int appState) {
    PromotionAppClick.ClickType clickType;
    switch (appState) {
      case UPDATE:
        clickType = PromotionAppClick.ClickType.UPDATE;
        break;
      case DOWNLOAD:
        clickType = PromotionAppClick.ClickType.DOWNLOAD;
        break;
      case INSTALL:
        clickType = PromotionAppClick.ClickType.INSTALL_APP;
        break;
      case CLAIM:
        clickType = PromotionAppClick.ClickType.CLAIM;
        break;
      default:
        throw new IllegalArgumentException("Wrong view type of promotion app");
    }
    return clickType;
  }

  private int getButtonMessage(int appState) {
    int message;
    switch (appState) {
      case UPDATE:
        message = R.string.holidayspromotion_button_update;
        break;
      case DOWNLOAD:
      case INSTALL:
        message = R.string.holidayspromotion_button_install;
        break;
      case CLAIM:
        message = R.string.holidayspromotion_button_claim;
        break;
      case CLAIMED:
        message = R.string.holidayspromotion_button_claimed;
        break;
      default:
        throw new IllegalArgumentException("Wrong view type of promotion app");
    }
    return message;
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
