/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.v8engine.view.search;

import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by neuro on 05-11-2015.
 */
public class SearchAdWidget extends Widget<SearchAdDisplayable> {

  private TextView name;
  private ImageView icon;
  private TextView sponsored;
  private TextView description;

  public SearchAdWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    name = (TextView) itemView.findViewById(R.id.name);
    icon = (ImageView) itemView.findViewById(R.id.icon);
    description = (TextView) itemView.findViewById(R.id.description);
    sponsored = (TextView) itemView.findViewById(R.id.sponsored_label);
  }

  @Override public void bindView(SearchAdDisplayable displayable) {
    MinimalAd minimalAd = displayable.getPojo();

    name.setText(minimalAd.getName());
    description.setText(Html.fromHtml(minimalAd.getDescription()));
    sponsored.setTypeface(null, Typeface.BOLD);
    final FragmentActivity context = getContext();
    sponsored.setText((context.getResources()
        .getText(R.string.sponsored) + "").toUpperCase());
    ImageLoader.with(context)
        .load(minimalAd.getIconPath(), icon);

    itemView.setOnClickListener(view -> {
      //	        AptoideUtils.FlurryAppviewOrigin.addAppviewOrigin("Suggested_Search Result");
      getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
          .newAppViewFragment(minimalAd));
    });
  }
}
