package cm.aptoide.pt.home.apps;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import rx.subjects.PublishSubject;

public class AppcAppViewHolder extends AppsViewHolder {

  private PublishSubject<AppClick> updateApp;

  private TextView appName;
  private ImageView appIcon;
  private ImageView updateAppButton;

  public AppcAppViewHolder(View itemView, PublishSubject<AppClick> updateApp) {
    super(itemView);
    this.updateApp = updateApp;
    appName = itemView.findViewById(R.id.apps_updates_app_name);
    appIcon = itemView.findViewById(R.id.apps_updates_app_icon);
    updateAppButton = itemView.findViewById(R.id.apps_updates_update_button);
  }

  @Override public void setApp(App app) {
    appName.setText(((UpdateApp) app).getName());
    ImageLoader.with(itemView.getContext())
        .load(((UpdateApp) app).getIcon(), appIcon);
    updateAppButton.setOnClickListener(
        install -> updateApp.onNext(new AppClick(app, AppClick.ClickType.APPC_UPGRADE_APP)));

    itemView.setOnClickListener(
        __ -> updateApp.onNext(new AppClick(app, AppClick.ClickType.UPDATE_CARD_CLICK)));

    itemView.setOnLongClickListener(__ -> {
      updateApp.onNext(new AppClick(app, AppClick.ClickType.UPDATE_CARD_LONG_CLICK));
      return true;
    });
  }
}

