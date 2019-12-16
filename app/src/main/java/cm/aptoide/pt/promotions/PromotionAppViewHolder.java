package cm.aptoide.pt.promotions;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import java.text.DecimalFormat;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIM;
import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIMED;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNGRADE;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNLOAD;
import static cm.aptoide.pt.promotions.PromotionsAdapter.INSTALL;
import static cm.aptoide.pt.promotions.PromotionsAdapter.UPDATE;

public class PromotionAppViewHolder extends RecyclerView.ViewHolder {

  private final PublishSubject<PromotionAppClick> promotionAppClick;
  private int appState;
  private TextView appName;
  private TextView appDescription;
  private ImageView appIcon;
  private TextView appRewardMessage;
  private Button promotionAction;

  public PromotionAppViewHolder(View itemView, int appState,
      PublishSubject<PromotionAppClick> promotionAppClick) {
    super(itemView);
    this.appState = appState;
    appIcon = itemView.findViewById(R.id.app_icon);
    appName = itemView.findViewById(R.id.app_name);
    appDescription = itemView.findViewById(R.id.app_description);
    appRewardMessage = itemView.findViewById(R.id.app_reward);
    promotionAction = itemView.findViewById(R.id.promotion_app_action_button);
    this.promotionAppClick = promotionAppClick;
  }

  public void setApp(PromotionViewApp app, boolean isWalletInstalled) {
    setAppCardHeader(app);
    promotionAction.setText(itemView.getContext()
        .getString(getButtonMessage(appState), app.getAppcValue()));

    if (!isWalletInstalled) {
      lockInstallButton(true);
    } else {

      if (appState == CLAIMED) {
        lockInstallButton(true);
        promotionAction.setBackgroundColor(itemView.getResources()
            .getColor(R.color.grey_fog_light));
        promotionAction.setTextColor(itemView.getResources()
            .getColor(R.color.black));

        promotionAction.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_promotion_claimed_check, 0, 0, 0);
        promotionAction.setPadding(330, 0, 360, 0);
      } else if (appState == CLAIM) {
        promotionAction.setEnabled(true);
        promotionAction.setBackgroundDrawable(itemView.getContext()
            .getResources()
            .getDrawable(R.drawable.card_border_rounded_green));
        promotionAction.setOnClickListener(
            __ -> promotionAppClick.onNext(new PromotionAppClick(app, getClickType(appState))));
      } else {
        lockInstallButton(false);
        promotionAction.setOnClickListener(
            __ -> promotionAppClick.onNext(new PromotionAppClick(app, getClickType(appState))));
      }
    }
  }

  private void lockInstallButton(boolean lock) {
    if (lock) {
      promotionAction.setEnabled(false);
      promotionAction.setBackgroundDrawable(itemView.getContext()
          .getResources()
          .getDrawable(R.drawable.card_border_fog_grey_normal));
      promotionAction.setTextColor(itemView.getContext()
          .getResources()
          .getColor(R.color.grey_fog_light));
    } else {
      TypedValue resultValue = new TypedValue();
      itemView.getContext()
          .getTheme()
          .resolveAttribute(R.attr.installButtonBackground, resultValue, true);

      promotionAction.setTextColor(Color.WHITE);

      promotionAction.setEnabled(true);
      if (resultValue.resourceId != 0) {
        promotionAction.setBackgroundResource(resultValue.resourceId);
      } else {
        promotionAction.setBackgroundDrawable(itemView.getContext()
            .getResources()
            .getDrawable(R.drawable.card_border_rounded_orange));
      }
    }
  }

  private PromotionAppClick.ClickType getClickType(int appState) {
    PromotionAppClick.ClickType clickType;
    switch (appState) {
      case DOWNGRADE:
        clickType = PromotionAppClick.ClickType.DOWNGRADE;
        break;
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
        message = R.string.appview_button_update;
        break;
      case DOWNLOAD:
      case INSTALL:
      case DOWNGRADE:
        message = R.string.install;
        break;
      case CLAIM:
        message = R.string.promotion_claim_button;
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
    appRewardMessage.setText(
        handleRewardMessage(app.getAppcValue(), app.getFiatSymbol(), app.getFiatValue(),
            appState == UPDATE));
  }

  private SpannableString handleRewardMessage(float appcValue, String fiatSymbol, double fiatValue,
      boolean isUpdate) {
    DecimalFormat fiatDecimalFormat = new DecimalFormat("0.00");
    String message = "";
    String appc = Integer.toString(Math.round(appcValue));
    if (isUpdate) {
      message = itemView.getContext()
          .getString(R.string.FIATpromotion_update_to_get_short, appc,
              fiatSymbol + fiatDecimalFormat.format(fiatValue));
    } else {
      message = itemView.getContext()
          .getString(R.string.FIATpromotion_install_to_get_short, appc,
              fiatSymbol + fiatDecimalFormat.format(fiatValue));
    }

    SpannableString spannable = new SpannableString(message);
    spannable.setSpan(new ForegroundColorSpan(itemView.getContext()
            .getResources()
            .getColor(R.color.promotions_reward)), message.indexOf(appc), message.length(),
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    return spannable;
  }
}
