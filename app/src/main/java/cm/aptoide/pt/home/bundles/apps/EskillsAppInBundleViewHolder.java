package cm.aptoide.pt.home.bundles.apps;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.home.bundles.base.AppHomeEvent;
import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.app.AppViewHolder;
import cm.aptoide.pt.view.app.Application;
import rx.subjects.PublishSubject;

public class EskillsAppInBundleViewHolder extends AppViewHolder {

  private final PublishSubject<HomeEvent> appClicks;
  private final ExperimentClicked experimentClickedEvent;
  private final ImageView appIcon;
  private final TextView appName;

  public EskillsAppInBundleViewHolder(View itemView, PublishSubject<HomeEvent> appClicks, ExperimentClicked experimentClickedEvent) {
    super(itemView);
    appIcon = (ImageView) itemView.findViewById(R.id.icon);
    appName = (TextView) itemView.findViewById(R.id.name);
    this.appClicks = appClicks;
    this.experimentClickedEvent = experimentClickedEvent;
  }

  @Override public void setApp(Application app, HomeBundle homeBundle, int bundlePosition) {
    ImageLoader.with(itemView.getContext())
        .loadWithRoundCorners(app.getIcon(), 8, appIcon, R.attr.placeholder_square);
    appName.setText(app.getName());
    itemView.setOnClickListener(v -> {
      appClicks.onNext(
              new AppHomeEvent(app, getAdapterPosition(), homeBundle, bundlePosition,
                      HomeEvent.Type.ESKILLS));
      experimentClickedEvent.onClicked();
    });
  }
}
