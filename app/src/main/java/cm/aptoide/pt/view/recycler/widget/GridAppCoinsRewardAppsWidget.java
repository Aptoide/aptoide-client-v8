package cm.aptoide.pt.view.recycler.widget;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.home.GridAppCoinsRewardAppsDisplayable;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.app.Application;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by filipegoncalves on 4/28/18.
 */

public class GridAppCoinsRewardAppsWidget extends Widget<GridAppCoinsRewardAppsDisplayable> {

  private ImageView appIcon;
  private TextView appName;
  private TextView appReward;

  public GridAppCoinsRewardAppsWidget(@NonNull View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    appIcon = (ImageView) itemView.findViewById(R.id.icon);
    appName = (TextView) itemView.findViewById(R.id.name);
    appReward = (TextView) itemView.findViewById(R.id.appc_text);
  }

  @Override public void unbindView() {
    super.unbindView();
  }

  @Override public void bindView(GridAppCoinsRewardAppsDisplayable displayable, int position) {
    Application app = displayable.getPojo();
    ImageLoader.with(itemView.getContext())
        .loadWithRoundCorners(app.getIcon(), 8, appIcon, R.drawable.placeholder_square);
    appName.setText(app.getName());
    appReward.setText(itemView.getResources()
        .getString(R.string.appc_short_get_appc));
    compositeSubscription.add(RxView.clicks(itemView)
        .subscribe(v -> displayable.openAppView(), throwable -> CrashReport.getInstance()
            .log(throwable)));
  }
}
