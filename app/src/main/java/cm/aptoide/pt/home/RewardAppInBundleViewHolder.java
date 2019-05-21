package cm.aptoide.pt.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.app.AppViewHolder;
import cm.aptoide.pt.view.app.Application;
import rx.subjects.PublishSubject;

public class RewardAppInBundleViewHolder extends AppViewHolder {

  private final PublishSubject<HomeEvent> appClicks;
  private final ImageView appIcon;
  private final TextView appName;
  private final TextView appReward;

  public RewardAppInBundleViewHolder(View itemView, PublishSubject<HomeEvent> appClicks) {
    super(itemView);
    appIcon = (ImageView) itemView.findViewById(R.id.icon);
    appName = (TextView) itemView.findViewById(R.id.name);
    appReward = (TextView) itemView.findViewById(R.id.appc_text);
    this.appClicks = appClicks;
  }

  @Override
  public void setApp(Application app, HomeBundle homeBundle, int position, int bundlePosition) {
    ImageLoader.with(itemView.getContext())
        .loadWithRoundCorners(app.getIcon(), 8, appIcon, R.drawable.placeholder_square);
    appName.setText(app.getName());
    appReward.setText(itemView.getResources()
        .getString(R.string.appc_card_short));
    itemView.setOnClickListener(v -> appClicks.onNext(
        new AppHomeEvent(app, position, homeBundle, bundlePosition, HomeEvent.Type.REWARD_APP)));
  }
}
