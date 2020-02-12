/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.store.view.featured;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import java.text.DecimalFormat;

/**
 * Created by neuro on 09-05-2016.
 */
public class AppBrickListWidget extends Widget<AppBrickListDisplayable> {

  private TextView name;
  private ImageView appIcon;
  private ImageView graphic;
  private TextView rating;
  private DecimalFormat oneDecimalFormatter;

  public AppBrickListWidget(View itemView) {
    super(itemView);
    oneDecimalFormatter = new DecimalFormat("0.0");
  }

  @Override protected void assignViews(View itemView) {
    appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
    name = (TextView) itemView.findViewById(R.id.app_name);
    graphic = (ImageView) itemView.findViewById(R.id.featured_graphic);
    rating = (TextView) itemView.findViewById(R.id.rating_label);
  }

  @Override public void bindView(AppBrickListDisplayable displayable, int position) {
    App app = displayable.getPojo();

    ImageLoader.with(getContext())
        .load(app.getIcon(), R.attr.placeholder_square, appIcon);

    ImageLoader.with(getContext())
        .load(app.getGraphic(), R.attr.placeholder_brick, graphic);
    name.setText(app.getName());

    float rating = app.getStats()
        .getRating()
        .getAvg();
    if (rating == 0) {
      this.rating.setText(R.string.appcardview_title_no_stars);
    } else {
      this.rating.setText(oneDecimalFormatter.format(rating));
    }

    compositeSubscription.add(RxView.clicks(itemView)
        .subscribe(v -> {
          getFragmentNavigator().navigateTo(AptoideApplication.getFragmentProvider()
              .newAppViewFragment(app.getId(), app.getPackageName(), app.getStore()
                  .getAppearance()
                  .getTheme(), app.getStore()
                  .getName(), displayable.getTag(), String.valueOf(getAdapterPosition())), true);
        }, throwable -> CrashReport.getInstance()
            .log(throwable)));
  }
}
