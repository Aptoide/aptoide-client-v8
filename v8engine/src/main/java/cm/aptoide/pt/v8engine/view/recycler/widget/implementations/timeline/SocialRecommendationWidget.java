package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialRecommendationDisplayable;

/**
 * Created by jdandrade on 15/12/2016.
 */
public class SocialRecommendationWidget extends SocialCardWidget<SocialRecommendationDisplayable> {

  private final String CARD_TYPE_NAME = "Social Recommendation";
  private TextView storeName;
  private TextView userName;
  private ImageView storeAvatar;
  private ImageView userAvatar;
  private ImageView appIcon;
  private TextView appName;
  private TextView getApp;
  private CardView cardView;
  private RelativeLayout cardContent;

  public SocialRecommendationWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);
    storeName = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_card_title);
    userName = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_card_subtitle);
    storeAvatar = (ImageView) itemView.findViewById(R.id.card_image);
    userAvatar = (ImageView) itemView.findViewById(R.id.card_user_avatar);
    appName = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_similar_apps);
    appIcon =
        (ImageView) itemView.findViewById(R.id.displayable_social_timeline_recommendation_icon);
    getApp = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_get_app_button);
    cardView =
        (CardView) itemView.findViewById(R.id.displayable_social_timeline_recommendation_card);
    cardContent = (RelativeLayout) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_card_content);
  }

  @Override public void bindView(SocialRecommendationDisplayable displayable) {
    super.bindView(displayable);
    if (displayable.getStore() != null) {
      storeName.setVisibility(View.VISIBLE);
      storeName.setText(displayable.getStore().getName());
      storeAvatar.setVisibility(View.VISIBLE);
      ImageLoader.loadWithShadowCircleTransform(displayable.getStore().getAvatar(), storeAvatar);
      if (displayable.getUser() != null) {
        userName.setVisibility(View.VISIBLE);
        userName.setText(displayable.getUser().getName());
        userAvatar.setVisibility(View.VISIBLE);
        ImageLoader.loadWithShadowCircleTransform(displayable.getUser().getAvatar(), userAvatar);
      } else {
        userName.setVisibility(View.GONE);
        userAvatar.setVisibility(View.GONE);
      }
    } else {
      userName.setVisibility(View.GONE);
      userAvatar.setVisibility(View.GONE);
      if (displayable.getUser() != null) {
        storeName.setVisibility(View.VISIBLE);
        storeName.setText(displayable.getUser().getName());
        storeAvatar.setVisibility(View.VISIBLE);
        ImageLoader.loadWithShadowCircleTransform(displayable.getUser().getAvatar(), storeAvatar);
      }
    }
    //setCardviewMargin(displayable, cardView);

    ImageLoader.load(displayable.getAppIcon(), appIcon);

    appName.setText(displayable.getAppName());

    getApp.setVisibility(View.VISIBLE);
    getApp.setText(displayable.getAppText(getContext()));
    cardContent.setOnClickListener(view -> {
      knockWithSixpackCredentials(displayable.getAbUrl());

      Analytics.AppsTimeline.clickOnCard(CARD_TYPE_NAME, displayable.getPackageName(),
          Analytics.AppsTimeline.BLANK, displayable.getTitle(),
          Analytics.AppsTimeline.OPEN_APP_VIEW);
      //displayable.sendClickEvent(SendEventRequest.Body.Data.builder()
      //    .cardType(CARD_TYPE_NAME)
      //    .source(AptoideAnalytics.SOURCE_APTOIDE)
      //    .specific(SendEventRequest.Body.Specific.builder()
      //        .app(displayable.getPackageName())
      //        .based_on(displayable.getSimilarAppPackageName())
      //        .build())
      //    .build(), AptoideAnalytics.OPEN_APP);
      ((FragmentShower) getContext()).pushFragmentV4(
          V8Engine.getFragmentProvider().newAppViewFragment(displayable.getAppId()));
    });
  }

  @Override String getCardTypeName() {
    return CARD_TYPE_NAME;
  }
}
