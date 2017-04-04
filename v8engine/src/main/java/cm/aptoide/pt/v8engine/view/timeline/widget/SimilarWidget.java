/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 08/07/2016.
 */

package cm.aptoide.pt.v8engine.view.timeline.widget;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SimilarDisplayable;

/**
 * Created by marcelobenites on 7/8/16.
 */
public class SimilarWidget extends CardWidget<SimilarDisplayable> {

  private TextView title;
  private TextView subtitle;
  private ImageView image;
  private ImageView appIcon;
  private TextView appName;
  private TextView similarApps;
  private TextView getApp;
  private CardView cardView;
  private RelativeLayout cardContent;

  public SimilarWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);
    title = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_card_title);
    subtitle = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_card_subtitle);
    image = (ImageView) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_card_icon);
    appIcon =
        (ImageView) itemView.findViewById(R.id.displayable_social_timeline_recommendation_icon);
    appName =
        (TextView) itemView.findViewById(R.id.displayable_social_timeline_recommendation_name);
    similarApps = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_similar_apps);
    getApp = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_get_app_button);
    cardView = (CardView) itemView.findViewById(R.id.card);
    cardContent = (RelativeLayout) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_card_content);
  }

  @Override public void bindView(SimilarDisplayable displayable) {
    super.bindView(displayable);
    final FragmentActivity context = getContext();
    title.setText(displayable.getStyledTitle(context));
    subtitle.setText(displayable.getTimeSinceRecommendation(context));

    setCardViewMargin(displayable, cardView);

    ImageLoader.with(context).loadWithShadowCircleTransform(displayable.getAvatarResource(), image);

    ImageLoader.with(context).load(displayable.getAppIcon(), appIcon);

    appName.setText(displayable.getAppName());

    similarApps.setText(displayable.getSimilarAppsText(context));

    getApp.setVisibility(View.VISIBLE);
    getApp.setText(displayable.getAppText(context));
    cardContent.setOnClickListener(view -> {
      knockWithSixpackCredentials(displayable.getAbUrl());

      Analytics.AppsTimeline.clickOnCard("Similar", displayable.getPackageName(),
          Analytics.AppsTimeline.BLANK, displayable.getTitle(),
          Analytics.AppsTimeline.OPEN_APP_VIEW);
      displayable.sendSimilarOpenAppEvent();
      getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
          .newAppViewFragment(displayable.getAppId(), displayable.getPackageName()));
    });
  }

  @Override String getCardTypeName() {
    return SimilarDisplayable.CARD_TYPE_NAME;
  }
}
