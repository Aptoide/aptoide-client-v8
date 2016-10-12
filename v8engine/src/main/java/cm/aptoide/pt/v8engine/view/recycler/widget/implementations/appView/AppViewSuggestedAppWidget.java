/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.model.MinimalAd;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewSuggestedAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by neuro on 04-08-2016.
 */
public class AppViewSuggestedAppWidget extends Widget<AppViewSuggestedAppDisplayable> {

  private ImageView iconImageView;
  private TextView appNameTextView;
  private TextView descriptionTextView;
  private View layout;

  public AppViewSuggestedAppWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    layout = itemView;
    iconImageView = (ImageView) itemView.findViewById(R.id.icon);
    appNameTextView = (TextView) itemView.findViewById(R.id.app_name);
    descriptionTextView = (TextView) itemView.findViewById(R.id.description);
  }

  @Override public void bindView(AppViewSuggestedAppDisplayable displayable) {
    MinimalAd pojo = displayable.getPojo();
    ImageLoader.load(pojo.getIconPath(), iconImageView);
    appNameTextView.setText(pojo.getName());
    descriptionTextView.setText(AptoideUtils.HtmlU.parse(pojo.getDescription()));

    layout.setOnClickListener(v -> {
      ((FragmentShower) v.getContext()).pushFragmentV4(
          V8Engine.getFragmentProvider().newAppViewFragment(pojo));
    });
  }

  @Override public void onViewAttached() {

  }

  @Override public void onViewDetached() {

  }
}
