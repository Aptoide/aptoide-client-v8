/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.ImageView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppBrickDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

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

  @Override public void unbindView() {

  }

  @Override public void bindView(AppBrickDisplayable displayable) {
    ImageLoader.load(displayable.getPojo().getGraphic(), R.drawable.placeholder_705x345, graphic);

    itemView.setOnClickListener(v -> {
      Analytics.AppViewViewedFrom.addStepToList(displayable.getTag());
      ((FragmentShower) v.getContext()).pushFragment(V8Engine.getFragmentProvider()
          .newAppViewFragment(displayable.getPojo().getId(),
              displayable.getPojo().getPackageName()));
      Analytics.HomePageEditorsChoice.clickOnEditorsChoiceItem(getAdapterPosition(),
          displayable.getPojo().getPackageName(), true);
    });
  }
}
