/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppBrickDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by neuro on 09-05-2016.
 */
@Displayables({ AppBrickDisplayable.class }) public class AppBrickWidget
    extends Widget<AppBrickDisplayable> {

  private ImageView graphic;

  public AppBrickWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    graphic = (ImageView) itemView.findViewById(R.id.featured_graphic);
  }

  @Override public void bindView(AppBrickDisplayable displayable) {
    final FragmentActivity context = getContext();
    ImageLoader.with(context)
        .load(displayable.getPojo().getGraphic(), R.drawable.placeholder_brick, graphic);

    compositeSubscription.add(RxView.clicks(itemView).subscribe(v -> {
      Analytics.AppViewViewedFrom.addStepToList(displayable.getTag());
      getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
          .newAppViewFragment(displayable.getPojo().getId(),
              displayable.getPojo().getPackageName()));
      Analytics.HomePageEditorsChoice.clickOnEditorsChoiceItem(getAdapterPosition(),
          displayable.getPojo().getPackageName(), true);
    }, throwable -> CrashReport.getInstance().log(throwable)));
  }
}
