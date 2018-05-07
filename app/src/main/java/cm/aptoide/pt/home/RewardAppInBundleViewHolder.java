package cm.aptoide.pt.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.app.AppViewHolder;
import cm.aptoide.pt.view.app.Application;
import java.text.DecimalFormat;
import rx.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 4/24/18.
 */

public class RewardAppInBundleViewHolder extends AppViewHolder {

  private final PublishSubject<HomeEvent> appClicks;
  private final DecimalFormat twoDecimalFormat;
  private final ImageView appIcon;
  private final TextView appName;
  private final TextView appReward;

  public RewardAppInBundleViewHolder(View itemView, PublishSubject<HomeEvent> appClicks,
      DecimalFormat twoDecimalFormat) {
    super(itemView);
    appIcon = (ImageView) itemView.findViewById(R.id.icon);
    appName = (TextView) itemView.findViewById(R.id.name);
    appReward = (TextView) itemView.findViewById(R.id.reward_appc);
    this.appClicks = appClicks;
    this.twoDecimalFormat = twoDecimalFormat;
  }

  @Override
  public void setApp(Application app, HomeBundle homeBundle, int position, int bundlePosition) {
    ImageLoader.with(itemView.getContext())
        .loadWithRoundCorners(app.getIcon(), 8, appIcon, R.drawable.placeholder_square);
    appName.setText(((RewardApp) app).getName());
    appReward.setText(itemView.getResources()
        .getString(R.string.bundles_short_reward_app_appc,
            twoDecimalFormat.format(((RewardApp) app).getRewardValue())));
    itemView.setOnClickListener(v -> appClicks.onNext(
        new AppHomeEvent(app, position, homeBundle, bundlePosition, HomeEvent.Type.REWARD_APP)));
  }
}
