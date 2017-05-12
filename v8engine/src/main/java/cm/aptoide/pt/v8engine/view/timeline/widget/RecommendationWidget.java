/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 08/07/2016.
 */

package cm.aptoide.pt.v8engine.view.timeline.widget;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.view.timeline.displayable.RecommendationDisplayable;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by marcelobenites on 7/8/16.
 */
public class RecommendationWidget extends CardWidget<RecommendationDisplayable> {

  private TextView title;
  private TextView subtitle;
  private ImageView image;
  private ImageView appIcon;
  private TextView appName;
  private TextView relatedToText;
  private Button getApp;
  private CardView cardView;
  private TextView relatedToApp;

  public RecommendationWidget(View itemView) {
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
    relatedToText = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_similar_apps);
    relatedToApp =
        (TextView) itemView.findViewById(R.id.social_timeline_recommendation_card_related_to_app);
    getApp = (Button) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_get_app_button);
    cardView = (CardView) itemView.findViewById(R.id.card);
  }

  @Override public void bindView(RecommendationDisplayable displayable) {
    super.bindView(displayable);
    final FragmentActivity context = getContext();
    title.setText(displayable.getStyledTitle(context));
    subtitle.setText(displayable.getTimeSinceRecommendation(context));

    setCardViewMargin(displayable, cardView);

    ImageLoader.with(context)
        .loadWithShadowCircleTransform(displayable.getAvatarResource(), image);

    ImageLoader.with(context)
        .load(displayable.getAppIcon(), appIcon);

    appName.setText(displayable.getAppName());

    relatedToText.setText(context.getString(R.string.related_to)
        .toLowerCase());
    relatedToApp.setText(displayable.getSimilarAppName());
    getApp.setVisibility(View.VISIBLE);

    compositeSubscription.add(RxView.clicks(getApp)
        .subscribe(a -> {
          knockWithSixpackCredentials(displayable.getAbUrl());

          Analytics.AppsTimeline.clickOnCard(RecommendationDisplayable.CARD_TYPE_NAME,
              displayable.getPackageName(), Analytics.AppsTimeline.BLANK, displayable.getTitle(),
              Analytics.AppsTimeline.OPEN_APP_VIEW);
          displayable.sendRecommendedOpenAppEvent();
          getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
              .newAppViewFragment(displayable.getAppId(), displayable.getPackageName()));
        }));
  }

  @Override String getCardTypeName() {
    return RecommendationDisplayable.CARD_TYPE_NAME;
  }
}
