package cm.aptoide.pt.v8engine.view.timeline.widget;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SocialRecommendationDisplayable;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by jdandrade on 15/12/2016.
 */
public class SocialRecommendationWidget extends SocialCardWidget<SocialRecommendationDisplayable> {

  private final String CARD_TYPE_NAME = "SOCIAL_RECOMMENDATION";
  private TextView storeName;
  private TextView userName;
  private ImageView appIcon;
  private TextView appName;
  private Button getApp;
  private CardView cardView;
  private RatingBar ratingBar;

  public SocialRecommendationWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);
    storeName = (TextView) itemView.findViewById(R.id.card_title);
    userName = (TextView) itemView.findViewById(R.id.card_subtitle);
    appName = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_similar_apps);
    appIcon =
        (ImageView) itemView.findViewById(R.id.displayable_social_timeline_recommendation_icon);
    getApp = (Button) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_get_app_button);
    cardView = (CardView) itemView.findViewById(R.id.card);
    ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
  }

  @Override public void bindView(SocialRecommendationDisplayable displayable) {
    super.bindView(displayable);
    final FragmentActivity context = getContext();
    if (displayable.getStore() != null) {
      storeName.setVisibility(View.VISIBLE);
      storeName.setText(displayable.getStyledTitle(context, displayable.getStore()
          .getName()));
      storeAvatar.setVisibility(View.VISIBLE);
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(displayable.getStore()
              .getAvatar(), storeAvatar);
      if (displayable.getUser() != null) {
        userName.setVisibility(View.VISIBLE);
        userName.setText(displayable.getUser()
            .getName());
        userAvatar.setVisibility(View.VISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(displayable.getUser()
                .getAvatar(), userAvatar);
      } else {
        userName.setVisibility(View.GONE);
        userAvatar.setVisibility(View.GONE);
      }
    } else {
      userName.setVisibility(View.GONE);
      userAvatar.setVisibility(View.GONE);
      if (displayable.getUser() != null) {
        storeName.setVisibility(View.VISIBLE);
        storeName.setText(displayable.getStyledTitle(context, displayable.getUser()
            .getName()));
        storeAvatar.setVisibility(View.VISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(displayable.getUser()
                .getAvatar(), storeAvatar);
      }
    }

    setCardViewMargin(displayable, cardView);

    ImageLoader.with(context)
        .load(displayable.getAppIcon(), appIcon);

    appName.setText(displayable.getAppName());

    ratingBar.setRating(displayable.getAppRating());

    getApp.setVisibility(View.VISIBLE);
    getApp.setText(displayable.getAppText(context));

    RxView.clicks(getApp)
        .subscribe(view -> {
          knockWithSixpackCredentials(displayable.getAbUrl());

          Analytics.AppsTimeline.clickOnCard(CARD_TYPE_NAME, displayable.getPackageName(),
              Analytics.AppsTimeline.BLANK, displayable.getTitle(),
              Analytics.AppsTimeline.OPEN_APP_VIEW);
          getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
              .newAppViewFragment(displayable.getAppId(), displayable.getPackageName()));
        }, throwable -> CrashReport.getInstance()
            .log(throwable));
  }

  @Override String getCardTypeName() {
    return CARD_TYPE_NAME;
  }
}
