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
  private final PublishSubject<HomeEvent> appClicks;
  private AppSecondaryInfoViewHolder appInfoViewHolder;

  public AppInBundleViewHolder(View itemView, PublishSubject<HomeEvent> appClicks,
      DecimalFormat oneDecimalFormatter) {
    super(itemView);
    appInfoViewHolder = new AppSecondaryInfoViewHolder(itemView, oneDecimalFormatter);
    nameTextView = ((TextView) itemView.findViewById(R.id.name));
    iconView = ((ImageView) itemView.findViewById(R.id.icon));
    this.appClicks = appClicks;
  }

  public void setApp(Application app, HomeBundle homeBundle, int bundlePosition, int position) {
    nameTextView.setText(app.getName());
    ImageLoader.with(itemView.getContext())
        .loadWithRoundCorners(app.getIcon(), 8, iconView, R.drawable.placeholder_square);

    appInfoViewHolder.setInfo(app.hasAppcBilling(), app.getRating(), true);

    itemView.setOnClickListener(v -> appClicks.onNext(
        new AppHomeEvent(app, position, homeBundle, bundlePosition, HomeEvent.Type.APP)));
  }
}
