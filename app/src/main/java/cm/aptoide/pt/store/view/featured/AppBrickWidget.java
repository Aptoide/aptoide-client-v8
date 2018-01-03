/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.store.view.featured;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.view.BaseActivity;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;

/**
 * Created by neuro on 09-05-2016.
 */
public class AppBrickWidget extends Widget<AppBrickDisplayable> {

  @Inject FragmentNavigator fragmentNavigator;
  private ImageView graphic;

  public AppBrickWidget(View itemView) {
    super(itemView);
    ((BaseActivity) getContext()).getActivityComponent()
        .inject(this);
  }

  @Override protected void assignViews(View itemView) {
    graphic = (ImageView) itemView.findViewById(R.id.featured_graphic);
  }

  @Override public void bindView(AppBrickDisplayable displayable) {
    final FragmentActivity context = getContext();
    ImageLoader.with(context)
        .load(displayable.getPojo()
            .getGraphic(), R.drawable.placeholder_brick, graphic);

    compositeSubscription.add(RxView.clicks(itemView)
        .subscribe(v -> {
          fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
              .newAppViewFragment(displayable.getPojo()
                  .getId(), displayable.getPojo()
                  .getPackageName(), displayable.getPojo()
                  .getStore()
                  .getAppearance()
                  .getTheme(), displayable.getPojo()
                  .getStore()
                  .getName(), displayable.getTag(), String.valueOf(getAdapterPosition())), true);
        }, throwable -> CrashReport.getInstance()
            .log(throwable)));
  }
}
