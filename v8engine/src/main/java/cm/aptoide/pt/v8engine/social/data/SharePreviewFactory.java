package cm.aptoide.pt.v8engine.social.data;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;

/**
 * Created by jdandrade on 06/07/2017.
 */

public class SharePreviewFactory {

  private AptoideAccountManager accountManager;

  public SharePreviewFactory(AptoideAccountManager accountManager) {
    this.accountManager = accountManager;
  }

  public View getSharePreviewView(Post post, Context context) {
    LayoutInflater factory = LayoutInflater.from(context);
    View view = null;
    CardView cardView;
    TextView sharedBy;
    LinearLayout like;
    LikeButtonView likeButtonView;
    TextView comments;
    LinearLayout socialInfoBar;
    LinearLayout socialCommentBar;

    if (post instanceof Media) {
      view = factory.inflate(R.layout.timeline_media_preview, null);

      TextView mediaTitle =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_title);
      ImageView thumbnail = (ImageView) view.findViewById(R.id.featured_graphic);
      TextView relatedTo = (TextView) view.findViewById(R.id.app_name);
      ImageView playIcon = (ImageView) view.findViewById(R.id.play_button);
      if (post.getType()
          .equals(CardType.ARTICLE) || post.getType()
          .equals(CardType.SOCIAL_ARTICLE) || post.getType()
          .equals(CardType.AGGREGATED_SOCIAL_ARTICLE)) {
        playIcon.setVisibility(View.GONE);
      } else if (post.getType()
          .equals(CardType.VIDEO) || post.getType()
          .equals(CardType.SOCIAL_VIDEO) || post.getType()
          .equals(CardType.AGGREGATED_SOCIAL_VIDEO)) {
        playIcon.setVisibility(View.VISIBLE);
      }
      mediaTitle.setMaxLines(1);
      mediaTitle.setText(((Media) post).getMediaTitle());
      relatedTo.setVisibility(View.GONE);
      ImageLoader.with(context)
          .load(((Media) post).getMediaThumbnailUrl(), thumbnail);
    } else if (post instanceof StoreLatestApps) {
      view = factory.inflate(R.layout.timeline_store_preview, null);

      TextView sharedStoreTitleName = (TextView) view.findViewById(R.id.social_shared_store_name);
      TextView sharedStoreName = (TextView) view.findViewById(R.id.store_name);
      ImageView sharedStoreAvatar = (ImageView) view.findViewById(R.id.social_shared_store_avatar);
      LinearLayout latestAppsContainer = (LinearLayout) view.findViewById(
          R.id.displayable_social_timeline_store_latest_apps_container);
      RelativeLayout followStoreBar = (RelativeLayout) view.findViewById(R.id.follow_store_bar);

      followStoreBar.setVisibility(View.GONE);
      sharedStoreTitleName.setText(((StoreLatestApps) post).getStoreName());
      sharedStoreName.setText(((StoreLatestApps) post).getStoreName());
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(((StoreLatestApps) post).getStoreAvatar(),
              sharedStoreAvatar);
      View latestAppView;
      ImageView latestAppIcon;
      TextView latestAppName;
      for (App latestApp : ((StoreLatestApps) post).getApps()) {
        latestAppView =
            factory.inflate(R.layout.social_timeline_latest_app, latestAppsContainer, false);
        latestAppIcon =
            (ImageView) latestAppView.findViewById(R.id.social_timeline_latest_app_icon);
        latestAppName = (TextView) latestAppView.findViewById(R.id.social_timeline_latest_app_name);
        ImageLoader.with(context)
            .load(latestApp.getIcon(), latestAppIcon);
        latestAppName.setMaxLines(1);
        latestAppName.setText(latestApp.getName());
        latestAppsContainer.addView(latestAppView);
      }
    } else if (post instanceof Recommendation) {
      view = factory.inflate(R.layout.timeline_recommendation_preview, null);
      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView getApp = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_get_app_button);
      RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);

      ImageLoader.with(context)
          .load(((Recommendation) post).getAppIcon(), appIcon);
      appName.setText(((Recommendation) post).getAppName());
      ratingBar.setRating(((Recommendation) post).getAppAverageRating());
      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));
    } else if (post instanceof AppUpdate) {
      view = factory.inflate(R.layout.timeline_recommendation_preview, null);
      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView getApp = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_get_app_button);
      RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);

      ImageLoader.with(context)
          .load(((AppUpdate) post).getAppUpdateIcon(), appIcon);
      appName.setText(((AppUpdate) post).getAppUpdateName());
      ratingBar.setRating(((AppUpdate) post).getAppUpdateAverageRating());

      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));
    } else if (post instanceof RatedRecommendation) {
      view = factory.inflate(R.layout.timeline_recommendation_preview, null);
      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);

      TextView getApp = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_get_app_button);
      ImageLoader.with(context)
          .load(((RatedRecommendation) post).getAppIcon(), appIcon);
      appName.setText(((RatedRecommendation) post).getAppName());

      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));
    } else if (post instanceof PopularApp) {
      view = factory.inflate(R.layout.timeline_recommendation_preview, null);
      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView getApp = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_get_app_button);
      RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);

      ImageLoader.with(context)
          .load(((PopularApp) post).getAppIcon(), appIcon);
      appName.setText(((PopularApp) post).getAppName());
      ratingBar.setRating(((PopularApp) post).getAppAverageRating());

      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));
    } else if (post instanceof AggregatedRecommendation) {
      view = factory.inflate(R.layout.timeline_recommendation_preview, null);

      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView getApp = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_get_app_button);
      RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
      ImageLoader.with(context)
          .load(((AggregatedRecommendation) post).getAppIcon(), appIcon);
      appName.setText(((AggregatedRecommendation) post).getAppName());
      ratingBar.setRating(((AggregatedRecommendation) post).getAppAverageRating());
      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));
    } else {
      // TODO: 07/07/2017 return an error preview
      return null;
    }

    TextView storeName = (TextView) view.findViewById(R.id.card_title);
    TextView userName = (TextView) view.findViewById(R.id.card_subtitle);
    ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
    ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
    CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
    LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
    TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);
    cardView = (CardView) view.findViewById(R.id.card);
    like = (LinearLayout) view.findViewById(R.id.social_like);
    likeButtonView = (LikeButtonView) view.findViewById(R.id.social_like_button);
    comments = (TextView) view.findViewById(R.id.social_comment);
    TextView numberOfComments = (TextView) view.findViewById(R.id.social_number_of_comments);
    socialInfoBar = (LinearLayout) view.findViewById(R.id.social_info_bar);
    socialCommentBar = (LinearLayout) view.findViewById(R.id.social_latest_comment_bar);

    setPreviewHeader(storeName, storeAvatar, userAvatar, userName, context);

    cardView.setRadius(8);
    cardView.setCardElevation(10);
    like.setOnClickListener(null);
    like.setOnTouchListener(null);
    like.setVisibility(View.VISIBLE);
    likeButtonView.setOnClickListener(null);
    likeButtonView.setOnTouchListener(null);
    likeButtonView.setVisibility(View.VISIBLE);

    comments.setVisibility(View.VISIBLE);
    socialInfoBar.setVisibility(View.GONE);
    socialCommentBar.setVisibility(View.GONE);

    return view;
  }

  private void setPreviewHeader(TextView storeName, ImageView storeAvatar, ImageView userAvatar,
      TextView userName, Context context) {
    if (accountManager.getAccount()
        .getStore()
        .getName() != null) {
      storeName.setTextColor(ContextCompat.getColor(context, R.color.black_87_alpha));
      if (Account.Access.PUBLIC.equals(accountManager.getAccountAccess())) {
        storeAvatar.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.VISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(accountManager.getAccount()
                .getStore()
                .getAvatar(), storeAvatar);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(accountManager.getAccount()
                .getAvatar(), userAvatar);
        storeName.setText(accountManager.getAccount()
            .getStore()
            .getName());
        userName.setText(accountManager.getAccount()
            .getNickname());
      } else {
        storeAvatar.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.INVISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(accountManager.getAccount()
                .getStore()
                .getAvatar(), storeAvatar);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(accountManager.getAccount()
                .getAvatar(), userAvatar);
        storeName.setText(accountManager.getAccount()
            .getStore()
            .getName());
        userName.setText(accountManager.getAccount()
            .getNickname());
        userName.setVisibility(View.GONE);
      }
    }
  }
}
