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
import java.text.DecimalFormat;
import rx.subjects.PublishSubject;

public class EskillsAppInBundleViewHolder extends AppViewHolder {

  private final PublishSubject<HomeEvent> appClicks;
  private final ImageView appIcon;
  private final TextView appName;
  private final TextView rating;
  private final DecimalFormat oneDecimalFormatter;

  public EskillsAppInBundleViewHolder(View itemView, PublishSubject<HomeEvent> appClicks,
      DecimalFormat oneDecimalFormatter) {
    super(itemView);
    appIcon = itemView.findViewById(R.id.icon);
    appName = itemView.findViewById(R.id.name);
    rating = itemView.findViewById(R.id.rating_label);
    this.appClicks = appClicks;
    this.oneDecimalFormatter = oneDecimalFormatter;
  }

  @Override public void setApp(Application app, HomeBundle homeBundle, int bundlePosition) {
    ImageLoader.with(itemView.getContext())
        .loadWithRoundCorners(app.getIcon(), 8, appIcon, R.attr.placeholder_square);
    appName.setText(app.getName());
    float rating = app.getRating();
    if (rating == 0) {
      this.rating.setText(R.string.appcardview_title_no_stars);
    } else {
      this.rating.setText(oneDecimalFormatter.format(rating));
    }
    itemView.setOnClickListener(v -> appClicks.onNext(
        new AppHomeEvent(app, getAdapterPosition(), homeBundle, bundlePosition,
            HomeEvent.Type.ESKILLS_APP)));
  }
}
