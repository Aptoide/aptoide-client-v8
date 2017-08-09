/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.v8engine.view.app;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import rx.functions.Action1;

public class GridAppWidget<T extends GridAppDisplayable> extends Widget<T> {

  private TextView name;
  private ImageView icon;
  private TextView downloads;
  private RatingBar ratingBar;
  private TextView tvStoreName;
  private TextView tvAddedTime;

  public GridAppWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(@NonNull View view) {
    name = (TextView) itemView.findViewById(R.id.name);
    icon = (ImageView) itemView.findViewById(R.id.icon);
    downloads = (TextView) itemView.findViewById(R.id.downloads);
    ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
    tvStoreName = (TextView) itemView.findViewById(R.id.store_name);
    tvAddedTime = (TextView) itemView.findViewById(R.id.added_time);
  }

  @Override public void bindView(T displayable) {
    final App pojo = displayable.getPojo();
    final long appId = pojo.getId();
    final FragmentActivity context = getContext();

    ImageLoader.with(context)
        .load(pojo.getIcon(), icon);

    int downloads = displayable.isTotalDownloads() ? pojo.getStats()
        .getPdownloads() : pojo.getStats()
        .getDownloads();

    name.setText(pojo.getName());
    this.downloads.setText(
        AptoideUtils.StringU.withSuffix(downloads) + context.getString(R.string._downloads));
    ratingBar.setRating(pojo.getStats()
        .getRating()
        .getAvg());
    tvStoreName.setText(pojo.getStore()
        .getName());
    tvAddedTime.setText(AptoideUtils.DateTimeU.getInstance(getContext())
        .getTimeDiffString(context, pojo.getAdded()
            .getTime(), getContext().getResources()));
    compositeSubscription.add(RxView.clicks(itemView)
        .subscribe(newOnClickListener(displayable, pojo, appId),
            throwable -> CrashReport.getInstance()
                .log(throwable)));
  }

  @NonNull protected Action1<Void> newOnClickListener(T displayable, App pojo, long appId) {
    return v -> {
      // FIXME
      Analytics.AppViewViewedFrom.addStepToList(displayable.getTag());
      getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
          .newAppViewFragment(appId, pojo.getPackageName(), pojo.getStore()
              .getAppearance()
              .getTheme(), tvStoreName.getText()
              .toString()));
    };
  }
}
