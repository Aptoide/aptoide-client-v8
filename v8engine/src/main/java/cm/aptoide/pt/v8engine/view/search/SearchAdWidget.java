/*
 * Copyright (c) 2016.
 * Modified on 24/06/2016.
 */

package cm.aptoide.pt.v8engine.view.search;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import java.util.Date;

/**
 * Created by neuro on 05-11-2015.
 */
public class SearchAdWidget extends Widget<SearchAdDisplayable> {

  private TextView name;
  private ImageView icon;
  private TextView sponsored;
  private TextView downloadsTextView;
  private RatingBar ratingBar;
  private TextView timeTextView;

  public SearchAdWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    name = (TextView) itemView.findViewById(R.id.name);
    icon = (ImageView) itemView.findViewById(R.id.icon);
    sponsored = (TextView) itemView.findViewById(R.id.sponsored_label);
    downloadsTextView = (TextView) itemView.findViewById(R.id.downloads);
    ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
    timeTextView = (TextView) itemView.findViewById(R.id.search_time);
  }

  @Override public void bindView(SearchAdDisplayable displayable) {
    MinimalAd minimalAd = displayable.getPojo();

    name.setText(minimalAd.getName());
    final FragmentActivity context = getContext();
    sponsored.setText((context.getResources()
        .getText(R.string.all_title_ad) + ""));
    ImageLoader.with(context)
        .load(minimalAd.getIconPath(), icon);

    itemView.setOnClickListener(view -> {
      //	        AptoideUtils.FlurryAppviewOrigin.addAppviewOrigin("Suggested_Search Result");
      getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
          .newAppViewFragment(minimalAd));
    });

    String downloadNumber =
        AptoideUtils.StringU.withSuffix(minimalAd.getDownloads()) + " " + getContext().getString(
            R.string.downloads);
    downloadsTextView.setText(downloadNumber);

    ratingBar.setRating(minimalAd.getStars());

    if (minimalAd.getModified() != null) {
      Date modified = new Date(minimalAd.getModified());
      String timeSinceUpdate = AptoideUtils.DateTimeU.getInstance(itemView.getContext())
          .getTimeDiffAll(itemView.getContext(), modified.getTime(), getContext().getResources());
      if (timeSinceUpdate != null && !timeSinceUpdate.equals("")) {
        timeTextView.setText(timeSinceUpdate);
      }
    }
  }
}
