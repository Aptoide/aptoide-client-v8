package cm.aptoide.pt.firstinstall.widget;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.firstinstall.displayable.FirstInstallAdDisplayable;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by diogoloureiro on 09/10/2017.
 */

public class FirstInstallAdWidget extends Widget<FirstInstallAdDisplayable> {

  private TextView name;
  private ImageView icon;

  public FirstInstallAdWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    name = (TextView) itemView.findViewById(R.id.app_name);
    icon = (ImageView) itemView.findViewById(R.id.app_icon);
  }

  @Override public void bindView(FirstInstallAdDisplayable displayable) {
    MinimalAd pojo = displayable.getPojo();
    name.setText(pojo.getName());

    final FragmentActivity context = getContext();
    ImageLoader.with(context)
        .load(pojo.getIconPath(), icon);

    compositeSubscription.add(RxView.clicks(itemView)
        .subscribe(v -> {
          // TODO: 09/10/2017 on click
        }, throwable -> CrashReport.getInstance()
            .log(throwable)));
  }
}
