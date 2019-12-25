package cm.aptoide.pt.promotions;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.networking.image.ImageLoader;
import java.text.DecimalFormat;
import rx.subjects.PublishSubject;

class PromotionAppDownloadingViewHolder extends RecyclerView.ViewHolder {

  private final PublishSubject<PromotionAppClick> promotionAppClick;
  private TextView appName;
  private TextView appDescription;
  private ImageView appIcon;
  private ProgressBar downloadProgressBar;
  private TextView downloadProgressValue;
  private ImageView pauseDownload;
  private ImageView cancelDownload;
  private ImageView resumeDownload;
  private TextView appRewardMessage;
  private LinearLayout downloadControlsLayout;

  public PromotionAppDownloadingViewHolder(View itemView,
      PublishSubject<PromotionAppClick> promotionAppClick) {
    super(itemView);
    this.appIcon = itemView.findViewById(R.id.app_icon);
    this.appName = itemView.findViewById(R.id.app_name);
    this.appDescription = itemView.findViewById(R.id.app_description);
    this.downloadProgressBar = itemView.findViewById(R.id.promotions_download_progress_bar);
    this.downloadProgressValue = itemView.findViewById(R.id.promotions_download_progress_number);
    this.pauseDownload = itemView.findViewById(R.id.promotions_download_pause_download);
    this.cancelDownload = itemView.findViewById(R.id.promotions_download_cancel_button);
    this.resumeDownload = itemView.findViewById(R.id.promotions_download_resume_download);
    this.downloadControlsLayout = itemView.findViewById(R.id.install_controls_layout);
    this.appRewardMessage = itemView.findViewById(R.id.app_reward);
    this.promotionAppClick = promotionAppClick;
  }

  public void setApp(PromotionViewApp app) {
    setAppCardHeader(app);
    setDownloadState(app.getDownloadModel()
        .getProgress(), app);
  }

  private void setAppCardHeader(PromotionViewApp app) {
    ImageLoader.with(itemView.getContext())
        .load(app.getAppIcon(), appIcon);
    appName.setText(app.getName());
    appDescription.setText(app.getDescription());
    appRewardMessage.setText(
        handleRewardMessage(app.getAppcValue(), app.getFiatSymbol(), app.getFiatValue(),
            app.getDownloadModel()
                .getAction()
                .equals(DownloadModel.Action.UPDATE)));
  }

  private void setDownloadState(int progress, PromotionViewApp promotionViewApp) {

    DownloadModel.DownloadState downloadState = promotionViewApp.getDownloadModel()
        .getDownloadState();

    LinearLayout.LayoutParams pauseShowing =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, 4f);
    LinearLayout.LayoutParams pauseHidden =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT, 2f);
    switch (downloadState) {
      case ACTIVE:
        downloadProgressBar.setIndeterminate(false);
        downloadProgressBar.setProgress(progress);
        downloadProgressValue.setText(String.valueOf(progress) + "%");
        pauseDownload.setVisibility(View.VISIBLE);
        pauseDownload.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionAppClick(promotionViewApp, PromotionAppClick.ClickType.PAUSE_DOWNLOAD)));
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        downloadControlsLayout.setLayoutParams(pauseShowing);
        break;
      case INDETERMINATE:
        downloadProgressBar.setIndeterminate(true);
        pauseDownload.setVisibility(View.VISIBLE);
        pauseDownload.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionAppClick(promotionViewApp, PromotionAppClick.ClickType.PAUSE_DOWNLOAD)));
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        downloadControlsLayout.setLayoutParams(pauseShowing);
        break;
      case PAUSE:
        downloadProgressBar.setIndeterminate(false);
        downloadProgressBar.setProgress(progress);
        downloadProgressValue.setText(String.valueOf(progress) + "%");
        pauseDownload.setVisibility(View.GONE);
        cancelDownload.setVisibility(View.VISIBLE);
        cancelDownload.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionAppClick(promotionViewApp, PromotionAppClick.ClickType.CANCEL_DOWNLOAD)));
        resumeDownload.setVisibility(View.VISIBLE);
        resumeDownload.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionAppClick(promotionViewApp, PromotionAppClick.ClickType.RESUME_DOWNLOAD)));
        downloadControlsLayout.setLayoutParams(pauseHidden);
        break;
      case ERROR:
      case NOT_ENOUGH_STORAGE_ERROR:
      case COMPLETE:
        downloadProgressBar.setIndeterminate(true);
        pauseDownload.setVisibility(View.VISIBLE);
        pauseDownload.setOnClickListener(__ -> promotionAppClick.onNext(
            new PromotionAppClick(promotionViewApp, PromotionAppClick.ClickType.PAUSE_DOWNLOAD)));
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        downloadControlsLayout.setLayoutParams(pauseShowing);
        break;
    }
  }

  private SpannableString handleRewardMessage(float appcValue, String fiatSymbol, double fiatValue,
      boolean isUpdate) {
    DecimalFormat fiatDecimalFormat = new DecimalFormat("0.00");
    String message = "";
    String appc = Double.toString(Math.floor(appcValue));
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
