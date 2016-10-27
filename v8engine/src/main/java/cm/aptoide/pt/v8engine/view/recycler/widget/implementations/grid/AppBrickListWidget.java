/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppBrickListDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by neuro on 09-05-2016.
 */
public class AppBrickListWidget extends Widget<AppBrickListDisplayable> {

  private TextView name;
  private ImageView graphic;
  private RatingBar ratingBar;

  public AppBrickListWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    name = (TextView) itemView.findViewById(R.id.app_name);
    graphic = (ImageView) itemView.findViewById(R.id.featured_graphic);
    ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
  }

  @Override public void bindView(AppBrickListDisplayable displayable) {
    App app = displayable.getPojo();

    ImageLoader.load(app.getGraphic(), R.drawable.placeholder_705x345, graphic);
    name.setText(app.getName());
    ratingBar.setRating(app.getStats().getRating().getAvg());
    itemView.setOnClickListener(v -> {
      Analytics.AppViewViewedFrom.addStepToList(displayable.getTag());
      ((FragmentShower) v.getContext()).pushFragmentV4(
          V8Engine.getFragmentProvider().newAppViewFragment(app.getId()));
      Analytics.HomePageEditorsChoice.clickOnEditorsChoiceItem(getAdapterPosition(),
          app.getPackageName(), false);
    });
  }

  @Override public void onViewDetached() {

  }
}
