package cm.aptoide.pt.home.apps;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import rx.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 3/15/18.
 */

class ErrorUpdateAppViewHolder extends AppsViewHolder {

  private TextView appName;
  private ImageView appIcon;
  private ImageView retryButton;
  private PublishSubject<AppClick> retryUpdate;

  private boolean isAppcUpgrade;

  public ErrorUpdateAppViewHolder(View itemView, PublishSubject<AppClick> retryUpdate) {
    this(itemView, retryUpdate, false);
  }

  public ErrorUpdateAppViewHolder(View itemView, PublishSubject<AppClick> retryUpdate,
      boolean isAppcUpgrade) {
    super(itemView);

    appName = (TextView) itemView.findViewById(R.id.apps_updates_app_name);
    appIcon = (ImageView) itemView.findViewById(R.id.apps_updates_app_icon);
    retryButton = (ImageView) itemView.findViewById(R.id.apps_updates_retry_button);
    if (isAppcUpgrade) {
      retryButton.setImageDrawable(
          ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_refresh_appc));
    }
    this.retryUpdate = retryUpdate;
    this.isAppcUpgrade = isAppcUpgrade;
  }

  @Override public void setApp(App app) {
    ImageLoader.with(itemView.getContext())
        .load(((UpdateApp) app).getIcon(), appIcon);
    appName.setText(((UpdateApp) app).getName());
    retryButton.setOnClickListener(install -> retryUpdate.onNext(new AppClick(app,
        isAppcUpgrade ? AppClick.ClickType.APPC_UPGRADE_RETRY : AppClick.ClickType.RETRY_UPDATE)));
  }
}
