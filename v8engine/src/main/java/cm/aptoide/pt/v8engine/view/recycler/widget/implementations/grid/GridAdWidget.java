/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAdDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by neuro on 20-06-2016.
 */
public class GridAdWidget extends Widget<GridAdDisplayable> {

  private TextView name;
  private ImageView icon;

  public GridAdWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    name = (TextView) itemView.findViewById(R.id.name);
    icon = (ImageView) itemView.findViewById(R.id.icon);
  }

  @Override public void bindView(GridAdDisplayable displayable) {

    GetAdsResponse.Ad pojo = displayable.getPojo();

    name.setText(pojo.getData().getName());
    ImageLoader.load(pojo.getData().getIcon(), icon);

    itemView.setOnClickListener(v -> {
      Analytics.AppViewViewedFrom.addStepToList(displayable.getTag());
      ((FragmentShower) v.getContext()).pushFragmentV4(
          V8Engine.getFragmentProvider().newAppViewFragment(pojo));
    });
  }

  @Override public void onViewDetached() {

  }
}
