package cm.aptoide.pt.firstinstall.widget;

import android.graphics.PorterDuff;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.firstinstall.displayable.FirstInstallAppDisplayable;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by diogoloureiro on 09/10/2017.
 */

public class FirstInstallAppWidget extends Widget<FirstInstallAppDisplayable> {

  public TextView name;
  public ImageView icon;
  private ImageView installCheck;
  private RelativeLayout storeWidget;

  public FirstInstallAppWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    name = (TextView) itemView.findViewById(R.id.app_name);
    icon = (ImageView) itemView.findViewById(R.id.app_icon);
    installCheck = (ImageView) itemView.findViewById(R.id.app_install_check);
    storeWidget = (RelativeLayout) itemView.findViewById(R.id.store_widget);
  }

  @Override public void bindView(FirstInstallAppDisplayable displayable) {
    App app = displayable.getPojo();
    name.setText(app.getName());

    final FragmentActivity context = getContext();
    compositeSubscription.add(RxView.clicks(itemView)
        .subscribe(v -> {
          if (displayable.isSelected()) {
            displayable.setSelected(false);
            installCheck.setVisibility(View.GONE);
            storeWidget.getBackground()
                .setColorFilter(ContextCompat.getColor(getContext(), R.color.white),
                    PorterDuff.Mode.SRC_ATOP);
          } else {
            displayable.setSelected(true);
            installCheck.setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
              storeWidget.setBackgroundDrawable(ContextCompat.getDrawable(context,
                  R.drawable.first_install_displayable_background));
            } else {
              storeWidget.setBackground(ContextCompat.getDrawable(context,
                  R.drawable.first_install_displayable_background));
            }
          }
        }, throwable -> CrashReport.getInstance()
            .log(throwable)));

    ImageLoader.with(context)
        .load(app.getIcon(), icon);
  }
}
