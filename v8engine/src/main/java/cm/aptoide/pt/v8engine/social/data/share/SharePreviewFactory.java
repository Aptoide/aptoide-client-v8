package cm.aptoide.pt.v8engine.social.data.share;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.AggregatedRecommendation;
import cm.aptoide.pt.v8engine.social.data.AppUpdate;
import cm.aptoide.pt.v8engine.social.data.Media;
import cm.aptoide.pt.v8engine.social.data.PopularApp;
import cm.aptoide.pt.v8engine.social.data.Post;
import cm.aptoide.pt.v8engine.social.data.RatedRecommendation;
import cm.aptoide.pt.v8engine.social.data.Recommendation;
import cm.aptoide.pt.v8engine.social.data.StoreLatestApps;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;

/**
 * Created by jdandrade on 06/07/2017.
 */

public class SharePreviewFactory {

  private final PartialShareViewPreparer mediaPartialShareViewPreparer;
  private final StoreLatestAppsPartialShareViewPreparer storeLatestAppsPartialShareViewPreparer;
  private final RecommendationPartialShareViewPreparer recommendationPartialShareViewPreparer;
  private final AppUpdatePartialShareViewPreparer appUpdatePartialShareViewPreparer;
  private final RatedRecommendationPartialShareViewPreparer
      ratedRecommendationPartialShareViewPreparer;
  private final PopularAppPartialShareViewPreparer popularAppPartialShareViewPreparer;
  private final AggregatedRecommendationPartialShareViewPreparer
      aggregatedRecommendationPartialShareViewPreparer;

  public SharePreviewFactory() {
    mediaPartialShareViewPreparer = new MediaPartialShareViewPreparer();
    storeLatestAppsPartialShareViewPreparer = new StoreLatestAppsPartialShareViewPreparer();
    recommendationPartialShareViewPreparer = new RecommendationPartialShareViewPreparer();
    appUpdatePartialShareViewPreparer = new AppUpdatePartialShareViewPreparer();
    ratedRecommendationPartialShareViewPreparer = new RatedRecommendationPartialShareViewPreparer();
    popularAppPartialShareViewPreparer = new PopularAppPartialShareViewPreparer();
    aggregatedRecommendationPartialShareViewPreparer =
        new AggregatedRecommendationPartialShareViewPreparer();
  }

  public View getSharePreviewView(Post post, Context context, Account account) {
    LayoutInflater factory = LayoutInflater.from(context);
    View view;
    CardView cardView;
    TextView sharedBy;
    LinearLayout like;
    LikeButtonView likeButtonView;
    TextView comments;
    LinearLayout socialInfoBar;
    LinearLayout socialCommentBar;

    view = prepareViewForPostType(post, context, factory);

    if (view == null) return null;

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

    setPreviewHeader(storeName, storeAvatar, userAvatar, userName, context, account);
    setPreviewBottom(checkBox, account);

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

  private void setPreviewBottom(CheckBox checkBox, Account account) {
    checkBox.setVisibility(account.isAccessConfirmed() ? View.GONE : View.VISIBLE);
  }

  @Nullable
  private View prepareViewForPostType(Post post, Context context, LayoutInflater factory) {
    if (post instanceof Media) {
      return mediaPartialShareViewPreparer.prepareViewForPostType(post, context, factory);
    } else if (post instanceof StoreLatestApps) {
      return storeLatestAppsPartialShareViewPreparer.prepareViewForPostType(post, context, factory);
    } else if (post instanceof Recommendation) {
      return recommendationPartialShareViewPreparer.prepareViewForPostType(post, context, factory);
    } else if (post instanceof AppUpdate) {
      return appUpdatePartialShareViewPreparer.prepareViewForPostType(post, context, factory);
    } else if (post instanceof RatedRecommendation) {
      return ratedRecommendationPartialShareViewPreparer.prepareViewForPostType(post, context,
          factory);
    } else if (post instanceof PopularApp) {
      return popularAppPartialShareViewPreparer.prepareViewForPostType(post, context, factory);
    } else if (post instanceof AggregatedRecommendation) {
      return aggregatedRecommendationPartialShareViewPreparer.prepareViewForPostType(post, context,
          factory);
    } else {
      // TODO: 07/07/2017 return an error preview
      return null;
    }
  }

  private void setPreviewHeader(TextView storeName, ImageView storeAvatar, ImageView userAvatar,
      TextView userName, Context context, Account account) {
    if (account.getStore()
        .getName() != null) {
      storeName.setTextColor(ContextCompat.getColor(context, R.color.black_87_alpha));
      if (Account.Access.PUBLIC.equals(account.getAccess())) {
        storeAvatar.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.VISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(account.getStore()
                .getAvatar(), storeAvatar);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(account.getAvatar(), userAvatar);
        storeName.setText(account.getStore()
            .getName());
        userName.setText(account.getNickname());
      } else {
        storeAvatar.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.INVISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(account.getStore()
                .getAvatar(), storeAvatar);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(account.getAvatar(), userAvatar);
        storeName.setText(account.getStore()
            .getName());
        userName.setText(account.getNickname());
        userName.setVisibility(View.GONE);
      }
    }
  }
}
