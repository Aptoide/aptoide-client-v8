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

public class AppInBundleViewHolder extends AppViewHolder {

  private final TextView nameTextView;
  private final ImageView iconView;
  private final TextView rating;
  private final PublishSubject<HomeClick> appClicks;
  private final DecimalFormat oneDecimalFormatter;

  public AppInBundleViewHolder(View itemView, PublishSubject<HomeClick> appClicks,
      DecimalFormat oneDecimalFormatter) {
    super(itemView);
    nameTextView = ((TextView) itemView.findViewById(R.id.name));
    iconView = ((ImageView) itemView.findViewById(R.id.icon));
    rating = (TextView) itemView.findViewById(R.id.rating_label);
    this.appClicks = appClicks;
    this.oneDecimalFormatter = oneDecimalFormatter;
  }

  public void setApp(Application app, HomeBundle homeBundle, int bundlePosition, int position) {
    nameTextView.setText(app.getName());
    ImageLoader.with(itemView.getContext())
        .loadWithRoundCorners(app.getIcon(), 8, iconView, R.drawable.placeholder_square);
    float rating = app.getRating();
    if (rating == 0) {
      this.rating.setText(R.string.appcardview_title_no_stars);
    } else {
      this.rating.setText(oneDecimalFormatter.format(rating));
    }
    itemView.setOnClickListener(v -> appClicks.onNext(
        new AppHomeClick(app, position, homeBundle, bundlePosition, HomeClick.Type.APP)));
  }
}
