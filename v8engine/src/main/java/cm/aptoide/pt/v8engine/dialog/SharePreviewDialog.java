package cm.aptoide.pt.v8engine.dialog;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.BaseActivity;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewInstallDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.AppUpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.ArticleDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.RecommendationDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SimilarDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialArticleDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialInstallDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialRecommendationDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialStoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialVideoDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.StoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.VideoDisplayable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jdandrade on 09/12/2016.
 */

public class SharePreviewDialog {
  private Displayable displayable;
  private boolean privacyResult;

  public SharePreviewDialog(Displayable cardDisplayable) {
    this.displayable = cardDisplayable;
  }

  public AlertDialog.Builder showPreviewDialog(Context context) {
    AlertDialog.Builder alertadd = new AlertDialog.Builder(context);
    if (displayable instanceof ArticleDisplayable) {
      LayoutInflater factory = LayoutInflater.from(context);
      final View view =
          factory.inflate(R.layout.displayable_social_timeline_social_article_preview, null);

      TextView storeName = (TextView) view.findViewById(R.id.card_title);
      TextView userName = (TextView) view.findViewById(R.id.card_subtitle);
      ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
      ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
      TextView articleTitle =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_title);
      ImageView thumbnail =
          (ImageView) view.findViewById(R.id.partial_social_timeline_thumbnail_image);
      CardView cardView = (CardView) view.findViewById(R.id.card);
      LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
      TextView comments = (TextView) view.findViewById(R.id.social_comment);
      TextView relatedTo =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_related_to);
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
      LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
      TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);

      storeName.setText(AptoideAccountManager.getUserData().getUserRepo());
      articleTitle.setText(((ArticleDisplayable) displayable).getArticleTitle());
      cardView.setRadius(8);
      cardView.setCardElevation(10);
      like.setClickable(false);
      like.setOnClickListener(null);
      like.setVisibility(View.VISIBLE);
      comments.setVisibility(View.VISIBLE);

      showCardHeaderWithPrivacyOptions(storeName, userName, storeAvatar, userAvatar);

      relatedTo.setVisibility(View.GONE);
      ImageLoader.load(((ArticleDisplayable) displayable).getThumbnailUrl(), thumbnail);

      alertadd.setView(view).setCancelable(false);

      alertadd.setTitle(R.string.social_timeline_you_will_share);

      if (!ManagerPreferences.getUserAccessConfirmed()) {
        privacyText.setOnClickListener(click -> checkBox.toggle());
        checkBox.setClickable(true);
        storeAvatar.setVisibility(View.VISIBLE);
        storeName.setVisibility(View.VISIBLE);
        handlePrivacyCheckBoxChanges(userName, userAvatar, checkBox, socialTerms);
      }
    } else if (displayable instanceof VideoDisplayable) {
      LayoutInflater factory = LayoutInflater.from(context);
      final View view =
          factory.inflate(R.layout.displayable_social_timeline_social_video_preview, null);

      TextView storeName = (TextView) view.findViewById(R.id.card_title);
      TextView userName = (TextView) view.findViewById(R.id.card_subtitle);
      ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
      ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
      TextView articleTitle =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_title);
      ImageView thumbnail =
          (ImageView) view.findViewById(R.id.partial_social_timeline_thumbnail_image);
      CardView cardView = (CardView) view.findViewById(R.id.card);
      LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
      TextView comments = (TextView) view.findViewById(R.id.social_comment);
      TextView relatedTo =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_related_to);
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
      LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
      TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);

      storeName.setText(AptoideAccountManager.getUserData().getUserRepo());
      articleTitle.setText(((VideoDisplayable) displayable).getVideoTitle());
      cardView.setRadius(8);
      cardView.setCardElevation(10);
      like.setClickable(false);
      like.setOnClickListener(null);
      like.setVisibility(View.VISIBLE);
      comments.setVisibility(View.VISIBLE);

      showCardHeaderWithPrivacyOptions(storeName, userName, storeAvatar, userAvatar);

      relatedTo.setVisibility(View.GONE);
      ImageLoader.load(((VideoDisplayable) displayable).getThumbnailUrl(), thumbnail);
      alertadd.setView(view).setCancelable(false);

      alertadd.setTitle(R.string.social_timeline_you_will_share);
      if (!ManagerPreferences.getUserAccessConfirmed()) {
        privacyText.setOnClickListener(click -> checkBox.toggle());
        checkBox.setClickable(true);
        handlePrivacyCheckBoxChanges(userName, userAvatar, checkBox, socialTerms);
      }
    } else if (displayable instanceof StoreLatestAppsDisplayable) {
      LayoutInflater factory = LayoutInflater.from(context);
      final View view =
          factory.inflate(R.layout.displayable_social_timeline_social_store_latest_apps_preview,
              null);

      TextView storeName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_store_latest_apps_card_title);
      TextView userName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_store_latest_apps_card_subtitle);
      TextView sharedStoreName = (TextView) view.findViewById(R.id.store_name);
      ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
      ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
      ImageView sharedStoreAvatar = (ImageView) view.findViewById(R.id.social_shared_store_avatar);
      CardView cardView =
          (CardView) view.findViewById(R.id.displayable_social_timeline_store_latest_apps_card);
      LinearLayout latestAppsContainer = (LinearLayout) view.findViewById(
          R.id.displayable_social_timeline_store_latest_apps_container);

      Map<View, Long> apps = new HashMap<>();
      Map<Long, String> appsPackages = new HashMap<>();

      sharedStoreName.setText(((StoreLatestAppsDisplayable) displayable).getStoreName());
      ImageLoader.loadWithShadowCircleTransform(
          ((StoreLatestAppsDisplayable) displayable).getAvatarUrl(), sharedStoreAvatar);
      View latestAppView;
      ImageView latestAppIcon;
      for (StoreLatestAppsDisplayable.LatestApp latestApp : ((StoreLatestAppsDisplayable) displayable)
          .getLatestApps()) {
        latestAppView =
            factory.inflate(R.layout.social_timeline_latest_app, latestAppsContainer, false);
        latestAppIcon = (ImageView) latestAppView.findViewById(R.id.social_timeline_latest_app);
        ImageLoader.load(latestApp.getIconUrl(), latestAppIcon);
        latestAppsContainer.addView(latestAppView);
        apps.put(latestAppView, latestApp.getAppId());
        appsPackages.put(latestApp.getAppId(), latestApp.getPackageName());
      }

      LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
      TextView comments = (TextView) view.findViewById(R.id.social_comment);
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
      LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
      TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);

      storeName.setText(AptoideAccountManager.getUserData().getUserRepo());
      cardView.setRadius(0);
      like.setClickable(false);
      like.setOnClickListener(null);
      like.setVisibility(View.VISIBLE);
      comments.setVisibility(View.VISIBLE);

      showCardHeaderWithPrivacyOptions(storeName, userName, storeAvatar, userAvatar);

      alertadd.setView(view).setCancelable(false);

      alertadd.setTitle(R.string.social_timeline_you_will_share);
      if (!ManagerPreferences.getUserAccessConfirmed()) {
        privacyText.setOnClickListener(click -> checkBox.toggle());
        checkBox.setClickable(true);
        handlePrivacyCheckBoxChanges(userName, userAvatar, checkBox, socialTerms);
      }
    } else if (displayable instanceof RecommendationDisplayable) {
      LayoutInflater factory = LayoutInflater.from(context);
      final View view =
          factory.inflate(R.layout.displayable_social_timeline_social_recommendation_preview, null);

      TextView storeName =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_card_title);
      TextView userName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_card_subtitle);
      ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
      ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView appSubTitle =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_name);
      CardView cardView =
          (CardView) view.findViewById(R.id.displayable_social_timeline_recommendation_card);
      LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
      TextView comments = (TextView) view.findViewById(R.id.social_comment);
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
      LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
      TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);
      TextView getApp = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_get_app_button);
      storeName.setText(AptoideAccountManager.getUserData().getUserRepo());
      ImageLoader.loadWithShadowCircleTransform(
          ((RecommendationDisplayable) displayable).getAppIcon(), appIcon);
      appName.setText(((RecommendationDisplayable) displayable).getAppName());
      appSubTitle.setText(AptoideUtils.StringU.getFormattedString(
          R.string.displayable_social_timeline_recommendation_atptoide_team_recommends, ""));

      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));

      cardView.setRadius(8);
      cardView.setCardElevation(10);
      like.setClickable(false);
      like.setOnClickListener(null);
      like.setVisibility(View.VISIBLE);
      comments.setVisibility(View.VISIBLE);
      showCardHeaderWithPrivacyOptions(storeName, userName, storeAvatar, userAvatar);
      alertadd.setView(view).setCancelable(false);

      alertadd.setTitle(R.string.social_timeline_you_will_share);

      if (!ManagerPreferences.getUserAccessConfirmed()) {
        privacyText.setOnClickListener(click -> checkBox.toggle());
        checkBox.setClickable(true);
        handlePrivacyCheckBoxChanges(userName, userAvatar, checkBox, socialTerms);
      }
    } else if (displayable instanceof AppUpdateDisplayable) {
      LayoutInflater factory = LayoutInflater.from(context);
      final View view =
          factory.inflate(R.layout.displayable_social_timeline_social_recommendation_preview, null);

      TextView storeName =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_card_title);
      TextView userName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_card_subtitle);
      ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
      ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView appSubTitle =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_name);
      CardView cardView =
          (CardView) view.findViewById(R.id.displayable_social_timeline_recommendation_card);
      LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
      TextView comments = (TextView) view.findViewById(R.id.social_comment);
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
      LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
      TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);
      TextView getApp = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_get_app_button);
      storeName.setText(AptoideAccountManager.getUserData().getUserRepo());
      ImageLoader.loadWithShadowCircleTransform(
          ((AppUpdateDisplayable) displayable).getAppIconUrl(), appIcon);
      appName.setText(((AppUpdateDisplayable) displayable).getAppName());
      appSubTitle.setText(AptoideUtils.StringU.getFormattedString(
          R.string.displayable_social_timeline_recommendation_atptoide_team_recommends, ""));

      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));

      cardView.setRadius(8);
      cardView.setCardElevation(10);
      like.setClickable(false);
      like.setOnClickListener(null);
      like.setVisibility(View.VISIBLE);
      comments.setVisibility(View.VISIBLE);
      showCardHeaderWithPrivacyOptions(storeName, userName, storeAvatar, userAvatar);
      alertadd.setView(view).setCancelable(false);

      alertadd.setTitle(R.string.social_timeline_you_will_share);

      if (!ManagerPreferences.getUserAccessConfirmed()) {
        privacyText.setOnClickListener(click -> checkBox.toggle());
        checkBox.setClickable(true);
        handlePrivacyCheckBoxChanges(userName, userAvatar, checkBox, socialTerms);
      }
    } else if (displayable instanceof SimilarDisplayable) {
      LayoutInflater factory = LayoutInflater.from(context);
      final View view =
          factory.inflate(R.layout.displayable_social_timeline_social_recommendation_preview, null);

      TextView storeName =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_card_title);
      TextView userName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_card_subtitle);
      ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
      ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView appSubTitle =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_name);
      CardView cardView =
          (CardView) view.findViewById(R.id.displayable_social_timeline_recommendation_card);
      LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
      TextView comments = (TextView) view.findViewById(R.id.social_comment);
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
      LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
      TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);
      TextView getApp = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_get_app_button);
      storeName.setText(AptoideAccountManager.getUserData().getUserRepo());
      ImageLoader.loadWithShadowCircleTransform(((SimilarDisplayable) displayable).getAppIcon(),
          appIcon);
      appName.setText(((SimilarDisplayable) displayable).getAppName());
      appSubTitle.setText(AptoideUtils.StringU.getFormattedString(
          R.string.displayable_social_timeline_recommendation_atptoide_team_recommends, ""));

      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));

      cardView.setRadius(8);
      cardView.setCardElevation(10);
      like.setClickable(false);
      like.setOnClickListener(null);
      like.setVisibility(View.VISIBLE);
      comments.setVisibility(View.VISIBLE);
      showCardHeaderWithPrivacyOptions(storeName, userName, storeAvatar, userAvatar);
      alertadd.setView(view).setCancelable(false);

      alertadd.setTitle(R.string.social_timeline_you_will_share);

      if (!ManagerPreferences.getUserAccessConfirmed()) {
        privacyText.setOnClickListener(click -> checkBox.toggle());
        checkBox.setClickable(true);
        handlePrivacyCheckBoxChanges(userName, userAvatar, checkBox, socialTerms);
      }
    } else if (displayable instanceof AppViewInstallDisplayable) {
      LayoutInflater factory = LayoutInflater.from(context);
      final View view =
          factory.inflate(R.layout.displayable_social_timeline_social_install_preview, null);

      TextView storeName =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_card_title);
      TextView userName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_card_subtitle);
      ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
      ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView appSubTitle =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_name);
      CardView cardView =
          (CardView) view.findViewById(R.id.displayable_social_timeline_recommendation_card);
      LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
      TextView comments = (TextView) view.findViewById(R.id.social_comment);
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
      LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
      TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);
      TextView getApp = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_get_app_button);
      storeName.setText(AptoideAccountManager.getUserData().getUserRepo());
      ImageLoader.loadWithShadowCircleTransform(((AppViewInstallDisplayable) displayable).getPojo()
          .getNodes()
          .getMeta()
          .getData()
          .getIcon(), appIcon);
      appName.setText(((AppViewInstallDisplayable) displayable).getPojo()
          .getNodes()
          .getMeta()
          .getData()
          .getName());
      appSubTitle.setText(R.string.social_timeline_share_dialog_installed_and_recommended);

      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));

      cardView.setRadius(8);
      cardView.setCardElevation(10);
      like.setClickable(false);
      like.setOnClickListener(null);
      like.setVisibility(View.VISIBLE);
      comments.setVisibility(View.VISIBLE);
      showCardHeaderWithPrivacyOptions(storeName, userName, storeAvatar, userAvatar);
      alertadd.setView(view).setCancelable(false);

      alertadd.setTitle(R.string.social_timeline_you_will_share);

      if (!ManagerPreferences.getUserAccessConfirmed()) {
        privacyText.setOnClickListener(click -> checkBox.toggle());
        checkBox.setClickable(true);
        handlePrivacyCheckBoxChanges(userName, userAvatar, checkBox, socialTerms);
      }
    } else if (displayable instanceof SocialArticleDisplayable) {
      LayoutInflater factory = LayoutInflater.from(context);
      final View view =
          factory.inflate(R.layout.displayable_social_timeline_social_article_preview, null);

      TextView storeName = (TextView) view.findViewById(R.id.card_title);
      TextView userName = (TextView) view.findViewById(R.id.card_subtitle);
      ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
      ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
      TextView articleTitle =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_title);
      ImageView thumbnail =
          (ImageView) view.findViewById(R.id.partial_social_timeline_thumbnail_image);
      CardView cardView = (CardView) view.findViewById(R.id.card);
      LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
      TextView comments = (TextView) view.findViewById(R.id.social_comment);
      TextView relatedTo =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_related_to);
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
      LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
      TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);
      TextView sharedBy = (TextView) view.findViewById(R.id.social_shared_by);

      if (((SocialArticleDisplayable) displayable).getStore() != null) {
        storeName.setVisibility(View.VISIBLE);
        storeAvatar.setVisibility(View.VISIBLE);
        if (((SocialArticleDisplayable) displayable).getStore().getName() != null) {
          storeName.setText(((SocialArticleDisplayable) displayable).getStore().getName());
        }

        if (((SocialArticleDisplayable) displayable).getStore().getAvatar() != null) {
          ImageLoader.loadWithShadowCircleTransform(
              ((SocialArticleDisplayable) displayable).getStore().getAvatar(), storeAvatar);
        }
      } else {
        storeName.setVisibility(View.GONE);
        storeAvatar.setVisibility(View.GONE);
      }

      if (((SocialArticleDisplayable) displayable).getUser() != null) {
        userName.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.VISIBLE);
        if (((SocialArticleDisplayable) displayable).getUser().getName() != null) {
          userName.setText(((SocialArticleDisplayable) displayable).getUser().getName());
        }

        if (((SocialArticleDisplayable) displayable).getUser().getAvatar() != null) {
          ImageLoader.loadWithShadowCircleTransform(
              ((SocialArticleDisplayable) displayable).getUser().getAvatar(), userAvatar);
        }
      } else {
        userName.setVisibility(View.GONE);
        userAvatar.setVisibility(View.GONE);
      }

      articleTitle.setText(((SocialArticleDisplayable) displayable).getArticleTitle());
      cardView.setRadius(8);
      cardView.setCardElevation(10);
      like.setClickable(false);
      like.setOnClickListener(null);
      like.setVisibility(View.VISIBLE);
      comments.setVisibility(View.VISIBLE);

      relatedTo.setVisibility(View.GONE);

      sharedBy.setVisibility(View.VISIBLE);

      if (BaseActivity.UserAccessState.PUBLIC.toString()
          .equals(ManagerPreferences.getUserAccess())) {
        sharedBy.setText(String.format(context.getString(R.string.social_timeline_shared_by),
            AptoideAccountManager.getUserData().getUserName()));
      } else {
        sharedBy.setText(String.format(context.getString(R.string.social_timeline_shared_by),
            AptoideAccountManager.getUserData().getUserRepo()));
      }

      ImageLoader.load(((SocialArticleDisplayable) displayable).getThumbnailUrl(), thumbnail);

      alertadd.setView(view).setCancelable(false);

      alertadd.setTitle(R.string.social_timeline_you_will_share);

      if (!ManagerPreferences.getUserAccessConfirmed()) {
        privacyText.setOnClickListener(click -> checkBox.toggle());
        checkBox.setClickable(true);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
          if (isChecked) {
            if (BaseActivity.UserAccessState.PUBLIC.toString()
                .equals(ManagerPreferences.getUserAccess())) {
              relatedTo.setText(AptoideAccountManager.getUserData().getUserRepo());
            }
            this.privacyResult = true;
          } else {
            if (BaseActivity.UserAccessState.PUBLIC.toString()
                .equals(ManagerPreferences.getUserAccess())) {
              relatedTo.setText(AptoideAccountManager.getUserData().getUserName());
            }
            this.privacyResult = false;
          }
        });
        socialTerms.setVisibility(View.VISIBLE);
      }
    } else if (displayable instanceof SocialVideoDisplayable) {
      LayoutInflater factory = LayoutInflater.from(context);
      final View view =
          factory.inflate(R.layout.displayable_social_timeline_social_video_preview, null);

      TextView storeName = (TextView) view.findViewById(R.id.card_title);
      TextView userName = (TextView) view.findViewById(R.id.card_subtitle);
      ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
      ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
      TextView articleTitle =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_title);
      ImageView thumbnail =
          (ImageView) view.findViewById(R.id.partial_social_timeline_thumbnail_image);
      CardView cardView = (CardView) view.findViewById(R.id.card);
      LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
      TextView comments = (TextView) view.findViewById(R.id.social_comment);
      TextView relatedTo =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_related_to);
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
      LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
      TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);
      TextView sharedBy = (TextView) view.findViewById(R.id.social_shared_by);

      storeName.setText(AptoideAccountManager.getUserData().getUserRepo());
      articleTitle.setText(((SocialVideoDisplayable) displayable).getVideoTitle());
      cardView.setRadius(8);
      cardView.setCardElevation(10);
      like.setClickable(false);
      like.setOnClickListener(null);
      like.setVisibility(View.VISIBLE);
      comments.setVisibility(View.VISIBLE);

      if (((SocialVideoDisplayable) displayable).getStore() != null) {
        storeName.setVisibility(View.VISIBLE);
        storeAvatar.setVisibility(View.VISIBLE);
        if (((SocialVideoDisplayable) displayable).getStore().getName() != null) {
          storeName.setText(((SocialVideoDisplayable) displayable).getStore().getName());
        }

        if (((SocialVideoDisplayable) displayable).getStore().getAvatar() != null) {
          ImageLoader.loadWithShadowCircleTransform(
              ((SocialVideoDisplayable) displayable).getStore().getAvatar(), storeAvatar);
        }
      } else {
        storeName.setVisibility(View.GONE);
        storeAvatar.setVisibility(View.GONE);
      }

      if (((SocialVideoDisplayable) displayable).getUser() != null) {
        userName.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.VISIBLE);
        if (((SocialVideoDisplayable) displayable).getUser().getName() != null) {
          userName.setText(((SocialVideoDisplayable) displayable).getUser().getName());
        }

        if (((SocialVideoDisplayable) displayable).getUser().getAvatar() != null) {
          ImageLoader.loadWithShadowCircleTransform(
              ((SocialVideoDisplayable) displayable).getUser().getAvatar(), userAvatar);
        }
      } else {
        userName.setVisibility(View.GONE);
        userAvatar.setVisibility(View.GONE);
      }

      relatedTo.setVisibility(View.GONE);

      sharedBy.setVisibility(View.VISIBLE);

      if (BaseActivity.UserAccessState.PUBLIC.toString()
          .equals(ManagerPreferences.getUserAccess())) {
        sharedBy.setText(String.format(context.getString(R.string.social_timeline_shared_by),
            AptoideAccountManager.getUserData().getUserName()));
      } else {
        sharedBy.setText(String.format(context.getString(R.string.social_timeline_shared_by),
            AptoideAccountManager.getUserData().getUserRepo()));
      }

      ImageLoader.load(((SocialVideoDisplayable) displayable).getThumbnailUrl(), thumbnail);
      alertadd.setView(view).setCancelable(false);

      alertadd.setTitle(R.string.social_timeline_you_will_share);
      if (!ManagerPreferences.getUserAccessConfirmed()) {
        privacyText.setOnClickListener(click -> checkBox.toggle());
        checkBox.setClickable(true);
        handlePrivacyCheckBoxChanges(userName, userAvatar, checkBox, socialTerms);
      }
    } else if (displayable instanceof SocialRecommendationDisplayable) {
      LayoutInflater factory = LayoutInflater.from(context);
      final View view =
          factory.inflate(R.layout.displayable_social_timeline_social_recommendation_preview, null);

      TextView storeName = (TextView) view.findViewById(R.id.card_title);
      TextView userName = (TextView) view.findViewById(R.id.card_subtitle);
      ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
      ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView appSubTitle =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_name);
      CardView cardView =
          (CardView) view.findViewById(R.id.displayable_social_timeline_recommendation_card);
      LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
      TextView comments = (TextView) view.findViewById(R.id.social_comment);
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
      LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
      TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);
      TextView sharedBy = (TextView) view.findViewById(R.id.social_shared_by);

      TextView getApp = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_get_app_button);
      storeName.setText(AptoideAccountManager.getUserData().getUserRepo());
      ImageLoader.loadWithShadowCircleTransform(
          ((SocialRecommendationDisplayable) displayable).getAppIcon(), appIcon);
      appName.setText(((SocialRecommendationDisplayable) displayable).getAppName());
      appSubTitle.setText(AptoideUtils.StringU.getFormattedString(
          R.string.displayable_social_timeline_recommendation_atptoide_team_recommends, ""));

      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));

      cardView.setRadius(8);
      cardView.setCardElevation(10);
      like.setClickable(false);
      like.setOnClickListener(null);
      like.setVisibility(View.VISIBLE);
      comments.setVisibility(View.VISIBLE);
      if (((SocialRecommendationDisplayable) displayable).getStore() != null) {
        storeName.setVisibility(View.VISIBLE);
        storeAvatar.setVisibility(View.VISIBLE);
        if (((SocialRecommendationDisplayable) displayable).getStore().getName() != null) {
          storeName.setText(((SocialRecommendationDisplayable) displayable).getStore().getName());
        }

        if (((SocialRecommendationDisplayable) displayable).getStore().getAvatar() != null) {
          ImageLoader.loadWithShadowCircleTransform(
              ((SocialRecommendationDisplayable) displayable).getStore().getAvatar(), storeAvatar);
        }
      } else {
        storeName.setVisibility(View.GONE);
        storeAvatar.setVisibility(View.GONE);
      }

      if (((SocialRecommendationDisplayable) displayable).getUser() != null) {
        userName.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.VISIBLE);
        if (((SocialRecommendationDisplayable) displayable).getUser().getName() != null) {
          userName.setText(((SocialRecommendationDisplayable) displayable).getUser().getName());
        }

        if (((SocialRecommendationDisplayable) displayable).getUser().getAvatar() != null) {
          ImageLoader.loadWithShadowCircleTransform(
              ((SocialRecommendationDisplayable) displayable).getUser().getAvatar(), userAvatar);
        }
      } else {
        userName.setVisibility(View.GONE);
        userAvatar.setVisibility(View.GONE);
      }

      sharedBy.setVisibility(View.VISIBLE);

      if (BaseActivity.UserAccessState.PUBLIC.toString()
          .equals(ManagerPreferences.getUserAccess())) {
        sharedBy.setText(String.format(context.getString(R.string.social_timeline_shared_by),
            AptoideAccountManager.getUserData().getUserName()));
      } else {
        sharedBy.setText(String.format(context.getString(R.string.social_timeline_shared_by),
            AptoideAccountManager.getUserData().getUserRepo()));
      }

      alertadd.setView(view).setCancelable(false);

      alertadd.setTitle(R.string.social_timeline_you_will_share);

      if (!ManagerPreferences.getUserAccessConfirmed()) {
        privacyText.setOnClickListener(click -> checkBox.toggle());
        checkBox.setClickable(true);
        handlePrivacyCheckBoxChanges(userName, userAvatar, checkBox, socialTerms);
      }
    } else if (displayable instanceof SocialStoreLatestAppsDisplayable) {
      LayoutInflater factory = LayoutInflater.from(context);
      final View view =
          factory.inflate(R.layout.displayable_social_timeline_social_store_latest_apps_preview,
              null);
      TextView storeName = (TextView) view.findViewById(R.id.card_title);
      TextView userName = (TextView) view.findViewById(R.id.card_subtitle);
      ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
      ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
      TextView sharedStoreName = (TextView) view.findViewById(R.id.store_name);
      ImageView sharedStoreAvatar = (ImageView) view.findViewById(R.id.social_shared_store_avatar);
      CardView cardView =
          (CardView) view.findViewById(R.id.displayable_social_timeline_store_latest_apps_card);
      LinearLayout latestAppsContainer = (LinearLayout) view.findViewById(
          R.id.displayable_social_timeline_store_latest_apps_container);

      Map<View, Long> apps = new HashMap<>();
      Map<Long, String> appsPackages = new HashMap<>();

      sharedStoreName.setText(
          ((SocialStoreLatestAppsDisplayable) displayable).getSharedStore().getName());
      ImageLoader.loadWithShadowCircleTransform(
          ((SocialStoreLatestAppsDisplayable) displayable).getSharedStore().getAvatar(),
          sharedStoreAvatar);
      View latestAppView;
      ImageView latestAppIcon;
      for (SocialStoreLatestAppsDisplayable.LatestApp latestApp : ((SocialStoreLatestAppsDisplayable) displayable)
          .getLatestApps()) {
        latestAppView =
            factory.inflate(R.layout.social_timeline_latest_app, latestAppsContainer, false);
        latestAppIcon = (ImageView) latestAppView.findViewById(R.id.social_timeline_latest_app);
        ImageLoader.load(latestApp.getIconUrl(), latestAppIcon);
        latestAppsContainer.addView(latestAppView);
        apps.put(latestAppView, latestApp.getAppId());
        appsPackages.put(latestApp.getAppId(), latestApp.getPackageName());
      }

      LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
      TextView comments = (TextView) view.findViewById(R.id.social_comment);
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
      LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
      TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);
      TextView sharedBy = (TextView) view.findViewById(R.id.social_shared_by);

      storeName.setText(AptoideAccountManager.getUserData().getUserRepo());
      cardView.setRadius(0);
      like.setClickable(false);
      like.setOnClickListener(null);
      like.setVisibility(View.VISIBLE);
      comments.setVisibility(View.VISIBLE);

      if (((SocialStoreLatestAppsDisplayable) displayable).getStore() != null) {
        storeName.setVisibility(View.VISIBLE);
        storeAvatar.setVisibility(View.VISIBLE);
        if (((SocialStoreLatestAppsDisplayable) displayable).getStore().getName() != null) {
          storeName.setText(((SocialStoreLatestAppsDisplayable) displayable).getStore().getName());
        }

        if (((SocialStoreLatestAppsDisplayable) displayable).getStore().getAvatar() != null) {
          ImageLoader.loadWithShadowCircleTransform(
              ((SocialStoreLatestAppsDisplayable) displayable).getStore().getAvatar(), storeAvatar);
        }
      } else {
        storeName.setVisibility(View.INVISIBLE);
        storeAvatar.setVisibility(View.GONE);
      }

      if (((SocialStoreLatestAppsDisplayable) displayable).getUser() != null) {
        userName.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.VISIBLE);
        if (((SocialStoreLatestAppsDisplayable) displayable).getUser().getName() != null) {
          userName.setText(((SocialStoreLatestAppsDisplayable) displayable).getUser().getName());
        }

        if (((SocialStoreLatestAppsDisplayable) displayable).getUser().getAvatar() != null) {
          ImageLoader.loadWithShadowCircleTransform(
              ((SocialStoreLatestAppsDisplayable) displayable).getUser().getAvatar(), userAvatar);
        }
      } else {
        userName.setVisibility(View.INVISIBLE);
        userAvatar.setVisibility(View.GONE);
      }

      sharedBy.setVisibility(View.VISIBLE);

      if (BaseActivity.UserAccessState.PUBLIC.toString()
          .equals(ManagerPreferences.getUserAccess())) {
        sharedBy.setText(String.format(context.getString(R.string.social_timeline_shared_by),
            AptoideAccountManager.getUserData().getUserName()));
      } else {
        sharedBy.setText(String.format(context.getString(R.string.social_timeline_shared_by),
            AptoideAccountManager.getUserData().getUserRepo()));
      }

      alertadd.setView(view).setCancelable(false);

      alertadd.setTitle(R.string.social_timeline_you_will_share);
      if (!ManagerPreferences.getUserAccessConfirmed()) {
        privacyText.setOnClickListener(click -> checkBox.toggle());
        checkBox.setClickable(true);
        handlePrivacyCheckBoxChanges(userName, userAvatar, checkBox, socialTerms);
      }
    } else if (displayable instanceof SocialInstallDisplayable) {

      LayoutInflater factory = LayoutInflater.from(context);
      final View view =
          factory.inflate(R.layout.displayable_social_timeline_social_install_preview, null);

      TextView storeName =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_card_title);
      TextView userName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_card_subtitle);
      ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
      ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView appSubTitle =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_name);
      CardView cardView =
          (CardView) view.findViewById(R.id.displayable_social_timeline_recommendation_card);
      LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
      TextView comments = (TextView) view.findViewById(R.id.social_comment);
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
      LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
      TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);
      TextView getApp = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_get_app_button);
      storeName.setText(AptoideAccountManager.getUserData().getUserRepo());
      ImageLoader.loadWithShadowCircleTransform(
          ((SocialInstallDisplayable) displayable).getAppIcon(), appIcon);
      appName.setText(((SocialInstallDisplayable) displayable).getAppName());
      appSubTitle.setText(AptoideUtils.StringU.getFormattedString(
          R.string.displayable_social_timeline_recommendation_atptoide_team_recommends, ""));

      //// TODO: 27/12/2016 shared by text

      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));

      cardView.setRadius(8);
      cardView.setCardElevation(10);
      like.setClickable(false);
      like.setOnClickListener(null);
      like.setVisibility(View.VISIBLE);
      comments.setVisibility(View.VISIBLE);

      if (((SocialInstallDisplayable) displayable).getStore() != null) {
        storeName.setVisibility(View.VISIBLE);
        storeAvatar.setVisibility(View.VISIBLE);
        if (((SocialInstallDisplayable) displayable).getStore().getName() != null) {
          storeName.setText(((SocialInstallDisplayable) displayable).getStore().getName());
        }

        if (((SocialInstallDisplayable) displayable).getStore().getAvatar() != null) {
          ImageLoader.loadWithShadowCircleTransform(
              ((SocialInstallDisplayable) displayable).getStore().getAvatar(), storeAvatar);
        }
      } else {
        storeName.setVisibility(View.GONE);
        storeAvatar.setVisibility(View.GONE);
      }

      if (((SocialInstallDisplayable) displayable).getUser() != null) {
        userName.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.VISIBLE);
        if (((SocialInstallDisplayable) displayable).getUser().getName() != null) {
          userName.setText(((SocialInstallDisplayable) displayable).getUser().getName());
        }

        if (((SocialInstallDisplayable) displayable).getUser().getAvatar() != null) {
          ImageLoader.loadWithShadowCircleTransform(
              ((SocialInstallDisplayable) displayable).getUser().getAvatar(), userAvatar);
        }
      } else {
        userName.setVisibility(View.GONE);
        userAvatar.setVisibility(View.GONE);
      }

      alertadd.setView(view).setCancelable(false);

      alertadd.setTitle(R.string.social_timeline_you_will_share);

      if (!ManagerPreferences.getUserAccessConfirmed()) {
        privacyText.setOnClickListener(click -> checkBox.toggle());
        checkBox.setClickable(true);
        handlePrivacyCheckBoxChanges(userName, userAvatar, checkBox, socialTerms);
      }

    }
    return alertadd;
  }

  private void showCardHeaderWithPrivacyOptions(TextView storeName, TextView userName,
      ImageView storeAvatar, ImageView userAvatar) {
    if (AptoideAccountManager.getUserData().getUserRepo() != null) {
      if (BaseActivity.UserAccessState.PUBLIC.toString()
          .equals(ManagerPreferences.getUserAccess())) {
        storeAvatar.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.VISIBLE);
        ImageLoader.loadWithShadowCircleTransform(
            AptoideAccountManager.getUserData().getUserAvatarRepo(), storeAvatar);
        ImageLoader.loadWithShadowCircleTransform(
            AptoideAccountManager.getUserData().getUserAvatar(), userAvatar);
        storeName.setText(AptoideAccountManager.getUserData().getUserRepo());
        userName.setText(AptoideAccountManager.getUserData().getUserName());
      } else {
        storeAvatar.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.INVISIBLE);
        ImageLoader.loadWithShadowCircleTransform(
            AptoideAccountManager.getUserData().getUserAvatarRepo(), storeAvatar);
        ImageLoader.loadWithShadowCircleTransform(
            AptoideAccountManager.getUserData().getUserAvatar(), userAvatar);
        storeName.setText(AptoideAccountManager.getUserData().getUserRepo());
        userName.setText(AptoideAccountManager.getUserData().getUserName());
        userName.setVisibility(View.GONE);
      }
    } else {
      if ((BaseActivity.UserAccessState.PUBLIC.toString()).equals(
          ManagerPreferences.getUserAccess())) {
        storeAvatar.setVisibility(View.VISIBLE);
        ImageLoader.loadWithShadowCircleTransform(
            AptoideAccountManager.getUserData().getUserAvatar(), storeAvatar);
        userAvatar.setVisibility(View.INVISIBLE);
        storeName.setText(AptoideAccountManager.getUserData().getUserName());
        userName.setVisibility(View.GONE);
      }
    }
  }

  private void handlePrivacyCheckBoxChanges(TextView subtitle, ImageView userAvatar,
      CheckBox checkBox, LinearLayout socialTerms) {
    subtitle.setVisibility(View.VISIBLE);
    userAvatar.setVisibility(View.VISIBLE);
    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (isChecked) {
        userAvatar.setVisibility(View.GONE);
        subtitle.setVisibility(View.GONE);
        this.privacyResult = true;
      } else {
        userAvatar.setVisibility(View.VISIBLE);
        subtitle.setVisibility(View.VISIBLE);
        this.privacyResult = false;
      }
    });
    socialTerms.setVisibility(View.VISIBLE);
  }

  public boolean getPrivacyResult() {
    return this.privacyResult;
  }
}
