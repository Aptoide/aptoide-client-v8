package cm.aptoide.pt.store.view.top;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.home.AppSecondaryInfoViewHolder;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import java.text.DecimalFormat;

public class TopAppListWidget extends Widget<TopAppListDisplayable> {

  private DecimalFormat oneDecimalFormatter;

  private AppSecondaryInfoViewHolder appInfoViewHolder;
  private TextView topNumber;
  private TextView name;
  private TextView downloadNumber;
  private ImageView appIcon;

  public TopAppListWidget(@NonNull View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    topNumber = itemView.findViewById(R.id.top_number);
    appIcon = itemView.findViewById(R.id.icon);
    name = itemView.findViewById(R.id.name_label);
    downloadNumber = itemView.findViewById(R.id.download_number_label);
    oneDecimalFormatter = new DecimalFormat("0.0");
    appInfoViewHolder = new AppSecondaryInfoViewHolder(itemView, oneDecimalFormatter);
    TextView rating = itemView.findViewById(R.id.rating_label);
    rating.setTextAppearance(itemView.getContext(), R.style.Aptoide_TextView_Medium_XXS_Black);
  }

  @Override public void bindView(TopAppListDisplayable displayable, int position) {
    App app = displayable.getPojo();
    ImageLoader.with(itemView.getContext())
        .loadWithRoundCorners(app.getIcon(), 8, appIcon, R.attr.placeholder_square);
    name.setText(app.getName());
    topNumber.setText(String.valueOf(position + 1));
    appInfoViewHolder.setInfo(app.getAppcoins()
        .hasBilling(), app.getStats()
        .getRating()
        .getAvg(), true, true);
    downloadNumber.setText(String.format("%s %s", AptoideUtils.StringU.withSuffix(app.getStats()
        .getDownloads()), itemView.getContext()
        .getResources()
        .getString(R.string.downloads)));

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
