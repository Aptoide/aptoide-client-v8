package cm.aptoide.pt.view.recycler.widget;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.home.GridAppCoinsRewardAppsDisplayable;
import cm.aptoide.pt.home.RewardApp;
import cm.aptoide.pt.networking.image.ImageLoader;
import com.jakewharton.rxbinding.view.RxView;
import java.text.DecimalFormat;

/**
 * Created by filipegoncalves on 4/28/18.
 */

public class GridAppCoinsRewardAppsWidget extends Widget<GridAppCoinsRewardAppsDisplayable> {

  private final DecimalFormat twoDecimalFormat;
  private ImageView appIcon;
  private TextView appName;
  private TextView appReward;

  public GridAppCoinsRewardAppsWidget(@NonNull View itemView) {
    super(itemView);
    this.twoDecimalFormat = new DecimalFormat("#.##");
  }

  @Override protected void assignViews(View itemView) {
    appIcon = (ImageView) itemView.findViewById(R.id.icon);
    appName = (TextView) itemView.findViewById(R.id.name);
    appReward = (TextView) itemView.findViewById(R.id.reward_appc);
  }

  @Override public void bindView(GridAppCoinsRewardAppsDisplayable displayable) {
    RewardApp app = ((RewardApp) displayable.getPojo());
    ImageLoader.with(itemView.getContext())
        .loadWithRoundCorners(app.getIcon(), 8, appIcon, R.drawable.placeholder_square);
    appName.setText(app.getName());
    appReward.setText(itemView.getResources()
        .getString(R.string.bundles_short_reward_app_appc,
            twoDecimalFormat.format(app.getRewardValue())));

    compositeSubscription.add(RxView.clicks(itemView)
        .subscribe(v -> {
          displayable.openAppView();
        }, throwable -> CrashReport.getInstance()
            .log(throwable)));
  }
}
