/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 08/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.TimelineClickEvent;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SimilarDisplayable;

/**
 * Created by marcelobenites on 7/8/16.
 */
public class SimilarWidget extends CardWidget<SimilarDisplayable> {

  private final String CARD_TYPE_NAME = "SIMILAR";
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

  @Override String getCardTypeName() {
    return CARD_TYPE_NAME;
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
    cardView =
        (CardView) itemView.findViewById(R.id.card);
    cardContent = (RelativeLayout) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_card_content);
  }

  @Override public void bindView(SimilarDisplayable displayable) {
super.bindView(displayable);
    title.setText(displayable.getStyledTitle(getContext()));
    subtitle.setText(displayable.getTimeSinceRecommendation(getContext()));

    setCardViewMargin(displayable, cardView);

    ImageLoader.loadWithShadowCircleTransform(displayable.getAvatarResource(), image);

    ImageLoader.load(displayable.getAppIcon(), appIcon);

    appName.setText(displayable.getAppName());

    similarApps.setText(displayable.getSimilarAppsText(getContext()));

    getApp.setVisibility(View.VISIBLE);
    getApp.setText(displayable.getAppText(getContext()));
    cardContent.setOnClickListener(view -> {
      knockWithSixpackCredentials(displayable.getAbUrl());

      Analytics.AppsTimeline.clickOnCard("Similar", displayable.getPackageName(),
          Analytics.AppsTimeline.BLANK, displayable.getTitle(),
          Analytics.AppsTimeline.OPEN_APP_VIEW);
      displayable.sendClickEvent(SendEventRequest.Body.Data.builder()
          .cardType(CARD_TYPE_NAME)
          .source(TimelineClickEvent.SOURCE_APTOIDE)
          .specific(SendEventRequest.Body.Specific.builder()
              .app(displayable.getPackageName())
              .similar_to(displayable.getSimilarToAppPackageName())
              .build())
          .build(), TimelineClickEvent.OPEN_APP);
      ((FragmentShower) getContext()).pushFragmentV4(V8Engine.getFragmentProvider()
          .newAppViewFragment(displayable.getAppId(), displayable.getPackageName()));
    });
  }
}
