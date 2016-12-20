/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 08/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.AptoideAnalytics;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RecommendationDisplayable;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by marcelobenites on 7/8/16.
 */
public class RecommendationWidget extends CardWidget<RecommendationDisplayable> {

  private final String cardType = "Recommendation";
  private TextView title;
  private TextView subtitle;
  private ImageView image;
  private ImageView appIcon;
  private TextView appName;
  private TextView similarApps;
  private TextView getApp;
  private CardView cardView;
  private RelativeLayout cardContent;
  private LinearLayout share;

  public RecommendationWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
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
        (CardView) itemView.findViewById(R.id.displayable_social_timeline_recommendation_card);
    cardContent = (RelativeLayout) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_card_content);
    share = (LinearLayout) itemView.findViewById(R.id.social_share);
  }

  @Override public void bindView(RecommendationDisplayable displayable) {

    title.setText(displayable.getStyledTitle(getContext()));
    subtitle.setText(displayable.getTimeSinceRecommendation(getContext()));

    setCardviewMargin(displayable, cardView);

    ImageLoader.loadWithShadowCircleTransform(displayable.getAvatarResource(), image);

    ImageLoader.load(displayable.getAppIcon(), appIcon);

    appName.setText(displayable.getAppName());

    similarApps.setText(displayable.getSimilarAppsText(getContext()));

    getApp.setVisibility(View.VISIBLE);
    getApp.setText(displayable.getAppText(getContext()));
    cardContent.setOnClickListener(view -> {
      knockWithSixpackCredentials(displayable.getAbUrl());

      Analytics.AppsTimeline.clickOnCard(cardType, displayable.getPackageName(),
          Analytics.AppsTimeline.BLANK, displayable.getTitle(),
          Analytics.AppsTimeline.OPEN_APP_VIEW);
      displayable.sendClickEvent(SendEventRequest.Body.Data.builder()
          .cardType(cardType)
          .source(AptoideAnalytics.SOURCE_APTOIDE)
          .specific(SendEventRequest.Body.Specific.builder()
              .app(displayable.getPackageName())
              .based_on(displayable.getSimilarAppPackageName())
              .build())
          .build(), AptoideAnalytics.OPEN_APP);
      ((FragmentShower) getContext()).pushFragmentV4(
          V8Engine.getFragmentProvider().newAppViewFragment(displayable.getAppId()));
    });
    compositeSubscription.add(RxView.clicks(share).subscribe(click -> {
      shareCard(displayable);
    }, throwable -> throwable.printStackTrace()));
  }

  @Override public void unbindView() {

  }
}
