package cm.aptoide.pt.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.app.AppViewHolder;
import cm.aptoide.pt.view.app.Application;
import java.text.DecimalFormat;
import rx.subjects.PublishSubject;

public class TopAppViewHolder extends AppViewHolder {
  private final PublishSubject<HomeEvent> appClicks;

  private final AppSecondaryInfoViewHolder appInfoViewHolder;
  private TextView topNumber;
  private TextView name;
  private TextView downloadNumber;
  private ImageView appIcon;

  public TopAppViewHolder(View itemView, PublishSubject<HomeEvent> appClicks,
      DecimalFormat oneDecimalFormatter) {
    super(itemView);
    this.appClicks = appClicks;

    topNumber = itemView.findViewById(R.id.top_number);
    appIcon = itemView.findViewById(R.id.icon);
    name = itemView.findViewById(R.id.name_label);
    downloadNumber = itemView.findViewById(R.id.download_number_label);
    appInfoViewHolder = new AppSecondaryInfoViewHolder(itemView, oneDecimalFormatter);
    TextView rating = itemView.findViewById(R.id.rating_label);
    rating.setTextAppearance(itemView.getContext(), R.style.Aptoide_TextView_Medium_XXS_Black);
  }

  @Override
  public void setApp(Application app, HomeBundle homeBundle, int position, int bundlePosition) {
    ImageLoader.with(itemView.getContext())
        .loadWithRoundCorners(app.getIcon(), 8, appIcon, R.drawable.placeholder_square);
    name.setText(app.getName());
    topNumber.setText(String.valueOf(bundlePosition + 1));
    appInfoViewHolder.setInfo(app.hasAppcBilling(), app.getRating(), true, true);
    downloadNumber.setText(
        String.format("%s %s", AptoideUtils.StringU.withSuffix(app.getDownloads()),
            itemView.getContext()
                .getResources()
                .getString(R.string.downloads)));
    itemView.setOnClickListener(v -> appClicks.onNext(
        new AppHomeEvent(app, position, homeBundle, bundlePosition, HomeEvent.Type.APP)));
  }
}
