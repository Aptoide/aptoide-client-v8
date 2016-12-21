package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SocialInstallDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import com.like.LikeButton;
import com.like.OnLikeListener;

/**
 * Created by jdandrade on 15/12/2016.
 */
public class SocialInstallWidget extends SocialCardWidget<SocialInstallDisplayable> {

  private final String cardType = "Social Install";
  private TextView storeName;
  private TextView userName;
  private ImageView storeAvatar;
  private ImageView userAvatar;
  private ImageView appIcon;
  private TextView appName;
  private TextView getApp;
  private CardView cardView;
  private RelativeLayout cardContent;
  private LinearLayout like;
  private LinearLayout share;
  private LinearLayout comments;
  private LikeButton likeButton;
  private TextView numberLikes;
  private TextView numberComments;

  public SocialInstallWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
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
    like = (LinearLayout) itemView.findViewById(R.id.social_like);
    share = (LinearLayout) itemView.findViewById(R.id.social_share);
    likeButton = (LikeButton) itemView.findViewById(R.id.social_like_test);
    comments = (LinearLayout) itemView.findViewById(R.id.social_comment);
    numberLikes = (TextView) itemView.findViewById(R.id.social_number_of_likes);
    numberComments = (TextView) itemView.findViewById(R.id.social_number_of_comments);
  }

  @Override public void bindView(SocialInstallDisplayable displayable) {
    if (displayable.getStore() != null) {
      storeName.setText(displayable.getStore().getName());
    }

    if (displayable.getUser() != null) {
      userName.setText(displayable.getUser().getName());
    }
    if (displayable.getStore() != null) {
      ImageLoader.loadWithShadowCircleTransform(displayable.getStore().getAvatar(), storeAvatar);
    }
    if (displayable.getUser() != null) {
      ImageLoader.loadWithShadowCircleTransform(displayable.getUser().getAvatar(), userAvatar);
    }
    setCardviewMargin(displayable, cardView);

    ImageLoader.load(displayable.getAppIcon(), appIcon);

    showFullSocialBar(displayable);

    appName.setText(displayable.getAppName());

    getApp.setVisibility(View.VISIBLE);
    getApp.setText(displayable.getAppText(getContext()));
    cardContent.setOnClickListener(view -> {
      knockWithSixpackCredentials(displayable.getAbUrl());

      Analytics.AppsTimeline.clickOnCard(cardType, displayable.getPackageName(),
          Analytics.AppsTimeline.BLANK, displayable.getTitle(),
          Analytics.AppsTimeline.OPEN_APP_VIEW);
      //displayable.sendClickEvent(SendEventRequest.Body.Data.builder()
      //    .cardType(cardType)
      //    .source(AptoideAnalytics.SOURCE_APTOIDE)
      //    .specific(SendEventRequest.Body.Specific.builder()
      //        .app(displayable.getPackageName())
      //        .based_on(displayable.getSimilarAppPackageName())
      //        .build())
      //    .build(), AptoideAnalytics.OPEN_APP);
      ((FragmentShower) getContext()).pushFragmentV4(
          V8Engine.getFragmentProvider().newAppViewFragment(displayable.getAppId()));
    });

    // TODO: 21/12/2016 jdandradex
    share.setVisibility(View.INVISIBLE);

    compositeSubscription.add(RxView.clicks(share).subscribe(click -> {
      shareCard(displayable);
    }, throwable -> throwable.printStackTrace()));

    compositeSubscription.add(RxView.clicks(like).subscribe(click -> {
    }, (throwable) -> throwable.printStackTrace()));

    likeButton.setOnLikeListener(new OnLikeListener() {
      @Override public void liked(LikeButton likeButton) {
        likeCard(displayable, cardType, 1);
        numberLikes.setText(String.valueOf(displayable.getNumberOfLikes() + 1));
      }

      @Override public void unLiked(LikeButton likeButton) {
        likeButton.setLiked(true);
        //likeCard(displayable, cardType, -1);
        //numberLikes.setText("0");
      }
    });
  }

  private void showFullSocialBar(SocialInstallDisplayable displayable) {
    likeButton.setLiked(false);
    like.setVisibility(View.VISIBLE);
    numberLikes.setVisibility(View.VISIBLE);
    numberLikes.setText(String.valueOf(displayable.getNumberOfLikes()));
    comments.setVisibility(View.VISIBLE);
    numberComments.setVisibility(View.VISIBLE);
    numberComments.setText(String.valueOf(displayable.getNumberOfComments()));
  }
}
