/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.app.view;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.home.AppSecondaryInfoViewHolder;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import java.text.DecimalFormat;
import rx.functions.Action1;

public class GridAppWidget<T extends GridAppDisplayable> extends Widget<T> {

  private TextView name;
  private ImageView icon;
  private String storeName;
  private AppSecondaryInfoViewHolder appInfoViewHolder;

  public GridAppWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(@NonNull View view) {
    name = (TextView) itemView.findViewById(R.id.name);
    icon = (ImageView) itemView.findViewById(R.id.icon);
    appInfoViewHolder = new AppSecondaryInfoViewHolder(itemView, new DecimalFormat("0.0"));
  }

  @Override public void bindView(T displayable, int position) {
    final App pojo = displayable.getPojo();
    final long appId = pojo.getId();
    final FragmentActivity context = getContext();

    ImageLoader.with(context)
        .load(pojo.getIcon(), icon);

    name.setText(pojo.getName());
    appInfoViewHolder.setInfo(pojo.hasBilling(), pojo.getStats()
        .getRating()
        .getAvg(), true, false);
    storeName = pojo.getStore()
        .getName();
    compositeSubscription.add(RxView.clicks(itemView)
        .subscribe(newOnClickListener(displayable, pojo, appId),
            throwable -> CrashReport.getInstance()
                .log(throwable)));
  }

  @NonNull protected Action1<Void> newOnClickListener(T displayable, App pojo, long appId) {
    return v -> {
      // FIXME
      getFragmentNavigator().navigateTo(AptoideApplication.getFragmentProvider()
          .newAppViewFragment(appId, pojo.getPackageName(), pojo.getStore()
              .getAppearance()
              .getTheme(), storeName, displayable.getTag()), true);
    };
  }
}
