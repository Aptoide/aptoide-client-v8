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
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialCardDisplayable;
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
    LayoutInflater factory = LayoutInflater.from(context);
    View view = null;
    CardView cardView;
    TextView sharedBy;
    LinearLayout like;
    TextView comments;

    if (displayable instanceof ArticleDisplayable) {
      view = factory.inflate(R.layout.displayable_social_timeline_social_article_preview, null);
      TextView articleTitle =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_title);
      ImageView thumbnail =
          (ImageView) view.findViewById(R.id.partial_social_timeline_thumbnail_image);
      TextView relatedTo =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_related_to);

      articleTitle.setText(((ArticleDisplayable) displayable).getArticleTitle());
      relatedTo.setVisibility(View.GONE);
      ImageLoader.load(((ArticleDisplayable) displayable).getThumbnailUrl(), thumbnail);
    } else if (displayable instanceof VideoDisplayable) {
      view = factory.inflate(R.layout.displayable_social_timeline_social_video_preview, null);
      TextView articleTitle =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_title);
      ImageView thumbnail =
          (ImageView) view.findViewById(R.id.partial_social_timeline_thumbnail_image);
      TextView relatedTo =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_related_to);
      articleTitle.setText(((VideoDisplayable) displayable).getVideoTitle());

      relatedTo.setVisibility(View.GONE);
      ImageLoader.load(((VideoDisplayable) displayable).getThumbnailUrl(), thumbnail);
    } else if (displayable instanceof StoreLatestAppsDisplayable) {
      view = factory.inflate(R.layout.displayable_social_timeline_social_store_latest_apps_preview,
          null);

      TextView sharedStoreName = (TextView) view.findViewById(R.id.store_name);
      ImageView sharedStoreAvatar = (ImageView) view.findViewById(R.id.social_shared_store_avatar);
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
    } else if (displayable instanceof RecommendationDisplayable) {
      view =
          factory.inflate(R.layout.displayable_social_timeline_social_recommendation_preview, null);
      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView appSubTitle =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_name);
      TextView getApp = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_get_app_button);
      ImageLoader.loadWithShadowCircleTransform(
          ((RecommendationDisplayable) displayable).getAppIcon(), appIcon);
      appName.setText(((RecommendationDisplayable) displayable).getAppName());
      appSubTitle.setText(AptoideUtils.StringU.getFormattedString(
          R.string.displayable_social_timeline_recommendation_atptoide_team_recommends, ""));

      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));
    } else if (displayable instanceof AppUpdateDisplayable) {
      view =
          factory.inflate(R.layout.displayable_social_timeline_social_recommendation_preview, null);
      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView appSubTitle =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_name);
      TextView getApp = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_get_app_button);
      ImageLoader.loadWithShadowCircleTransform(
          ((AppUpdateDisplayable) displayable).getAppIconUrl(), appIcon);
      appName.setText(((AppUpdateDisplayable) displayable).getAppName());
      appSubTitle.setText(AptoideUtils.StringU.getFormattedString(
          R.string.displayable_social_timeline_recommendation_atptoide_team_recommends, ""));

      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));
    } else if (displayable instanceof SimilarDisplayable) {
      view =
          factory.inflate(R.layout.displayable_social_timeline_social_recommendation_preview, null);
      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView appSubTitle =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_name);
      TextView getApp = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_get_app_button);
      ImageLoader.loadWithShadowCircleTransform(((SimilarDisplayable) displayable).getAppIcon(),
          appIcon);
      appName.setText(((SimilarDisplayable) displayable).getAppName());
      appSubTitle.setText(AptoideUtils.StringU.getFormattedString(
          R.string.displayable_social_timeline_recommendation_atptoide_team_recommends, ""));

      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));
    } else if (displayable instanceof AppViewInstallDisplayable) {
      view = factory.inflate(R.layout.displayable_social_timeline_social_install_preview, null);
      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView appSubTitle =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_name);
      TextView getApp = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_get_app_button);
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
    } else if (displayable instanceof SocialArticleDisplayable) {
      view = factory.inflate(R.layout.displayable_social_timeline_social_article_preview, null);
      TextView articleTitle =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_title);
      ImageView thumbnail =
          (ImageView) view.findViewById(R.id.partial_social_timeline_thumbnail_image);
      TextView relatedTo =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_related_to);
      articleTitle.setText(((SocialArticleDisplayable) displayable).getArticleTitle());
      relatedTo.setVisibility(View.GONE);

      ImageLoader.load(((SocialArticleDisplayable) displayable).getThumbnailUrl(), thumbnail);
    } else if (displayable instanceof SocialVideoDisplayable) {
      view = factory.inflate(R.layout.displayable_social_timeline_social_video_preview, null);
      TextView articleTitle =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_title);
      ImageView thumbnail =
          (ImageView) view.findViewById(R.id.partial_social_timeline_thumbnail_image);
      TextView relatedTo =
          (TextView) view.findViewById(R.id.partial_social_timeline_thumbnail_related_to);
      articleTitle.setText(((SocialVideoDisplayable) displayable).getVideoTitle());

      relatedTo.setVisibility(View.GONE);

      ImageLoader.load(((SocialVideoDisplayable) displayable).getThumbnailUrl(), thumbnail);
    } else if (displayable instanceof SocialRecommendationDisplayable) {
      view =
          factory.inflate(R.layout.displayable_social_timeline_social_recommendation_preview, null);
      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView appSubTitle =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_name);

      TextView getApp = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_get_app_button);
      ImageLoader.loadWithShadowCircleTransform(
          ((SocialRecommendationDisplayable) displayable).getAppIcon(), appIcon);
      appName.setText(((SocialRecommendationDisplayable) displayable).getAppName());
      appSubTitle.setText(AptoideUtils.StringU.getFormattedString(
          R.string.displayable_social_timeline_recommendation_atptoide_team_recommends, ""));

      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));
    } else if (displayable instanceof SocialStoreLatestAppsDisplayable) {
      view = factory.inflate(R.layout.displayable_social_timeline_social_store_latest_apps_preview,
          null);
      TextView sharedStoreName = (TextView) view.findViewById(R.id.store_name);
      ImageView sharedStoreAvatar = (ImageView) view.findViewById(R.id.social_shared_store_avatar);
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
    } else if (displayable instanceof SocialInstallDisplayable) {
      view = factory.inflate(R.layout.displayable_social_timeline_social_install_preview, null);

      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      TextView appSubTitle =
          (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_name);
      TextView getApp = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_get_app_button);
      ImageLoader.loadWithShadowCircleTransform(
          ((SocialInstallDisplayable) displayable).getAppIcon(), appIcon);
      appName.setText(((SocialInstallDisplayable) displayable).getAppName());
      appSubTitle.setText(AptoideUtils.StringU.getFormattedString(
          R.string.displayable_social_timeline_recommendation_atptoide_team_recommends, ""));

      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));
    }

    if (view != null) {
      TextView storeName = (TextView) view.findViewById(R.id.card_title);
      TextView userName = (TextView) view.findViewById(R.id.card_subtitle);
      ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
      ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
      CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
      LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
      TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);
      cardView = (CardView) view.findViewById(R.id.card);
      like = (LinearLayout) view.findViewById(R.id.social_like);
      comments = (TextView) view.findViewById(R.id.social_comment);

      cardView.setRadius(8);
      cardView.setCardElevation(10);
      like.setClickable(false);
      like.setOnClickListener(null);
      like.setVisibility(View.VISIBLE);
      comments.setVisibility(View.VISIBLE);
      alertadd.setView(view).setCancelable(false);
      alertadd.setTitle(R.string.social_timeline_you_will_share);

      if (!(displayable instanceof SocialCardDisplayable)) {
        storeName.setText(AptoideAccountManager.getUserData().getUserRepo());
        setCardHeader(storeName, userName, storeAvatar, userAvatar);
      } else {
        sharedBy = (TextView) view.findViewById(R.id.social_shared_by);
        setSharedByText(context, sharedBy);
        setSocialCardHeader(storeName, userName, storeAvatar, userAvatar);
      }
      if (!ManagerPreferences.getUserAccessConfirmed()) {
        privacyText.setOnClickListener(click -> checkBox.toggle());
        checkBox.setClickable(true);
        storeAvatar.setVisibility(View.VISIBLE);
        storeName.setVisibility(View.VISIBLE);
        handlePrivacyCheckBoxChanges(userName, userAvatar, checkBox, socialTerms);
      }
    }
    return alertadd;
  }

  private void setSharedByText(Context context, TextView sharedBy) {
    sharedBy.setVisibility(View.VISIBLE);

    if (BaseActivity.UserAccessState.PUBLIC.toString().equals(ManagerPreferences.getUserAccess())) {
      sharedBy.setText(String.format(context.getString(R.string.social_timeline_shared_by),
          AptoideAccountManager.getUserData().getUserName()));
    } else {
      sharedBy.setText(String.format(context.getString(R.string.social_timeline_shared_by),
          AptoideAccountManager.getUserData().getUserRepo()));
    }
  }

  private void setSocialCardHeader1(TextView storeName, TextView userName, ImageView storeAvatar,
      ImageView userAvatar) {
    if (((SocialCardDisplayable) displayable).getStore() != null) {
      storeName.setVisibility(View.VISIBLE);
      storeAvatar.setVisibility(View.VISIBLE);
      if (((SocialCardDisplayable) displayable).getStore().getName() != null) {
        storeName.setText(((SocialCardDisplayable) displayable).getStore().getName());
      }

      if (((SocialCardDisplayable) displayable).getStore().getAvatar() != null) {
        ImageLoader.loadWithShadowCircleTransform(
            ((SocialCardDisplayable) displayable).getStore().getAvatar(), storeAvatar);
      }
    } else {
      storeName.setVisibility(View.GONE);
      storeAvatar.setVisibility(View.GONE);
    }

    if (((SocialCardDisplayable) displayable).getUser() != null) {
      userName.setVisibility(View.VISIBLE);
      userAvatar.setVisibility(View.VISIBLE);
      if (((SocialCardDisplayable) displayable).getUser().getName() != null) {
        userName.setText(((SocialCardDisplayable) displayable).getUser().getName());
      }

      if (((SocialCardDisplayable) displayable).getUser().getAvatar() != null) {
        ImageLoader.loadWithShadowCircleTransform(
            ((SocialCardDisplayable) displayable).getUser().getAvatar(), userAvatar);
      }
    } else {
      userName.setVisibility(View.GONE);
      userAvatar.setVisibility(View.GONE);
    }
  }

  private void setSocialCardHeader(TextView storeName, TextView userName, ImageView storeAvatar,
      ImageView userAvatar) {
    if (((SocialCardDisplayable) displayable).getStore() != null) {
      storeName.setVisibility(View.VISIBLE);
      storeAvatar.setVisibility(View.VISIBLE);
      if (((SocialCardDisplayable) displayable).getStore().getName() != null) {
        storeName.setText(((SocialCardDisplayable) displayable).getStore().getName());
      }
      if (((SocialCardDisplayable) displayable).getStore().getAvatar() != null) {
        ImageLoader.loadWithShadowCircleTransform(
            ((SocialCardDisplayable) displayable).getStore().getAvatar(), storeAvatar);
      }

      if (((SocialCardDisplayable) displayable).getUser() != null) {
        userName.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.VISIBLE);
        if (((SocialCardDisplayable) displayable).getUser().getName() != null) {
          userName.setText(((SocialCardDisplayable) displayable).getUser().getName());
        }

        if (((SocialCardDisplayable) displayable).getUser().getAvatar() != null) {
          ImageLoader.loadWithShadowCircleTransform(
              ((SocialCardDisplayable) displayable).getUser().getAvatar(), userAvatar);
        }
      } else {
        userName.setVisibility(View.GONE);
        userAvatar.setVisibility(View.GONE);
      }
    } else {
      userAvatar.setVisibility(View.INVISIBLE);
      userName.setVisibility(View.GONE);

      if (((SocialCardDisplayable) displayable).getUser() != null) {
        storeName.setVisibility(View.VISIBLE);
        storeAvatar.setVisibility(View.VISIBLE);
        if (((SocialCardDisplayable) displayable).getUser().getName() != null) {
          storeName.setText(((SocialCardDisplayable) displayable).getUser().getName());
        }

        if (((SocialCardDisplayable) displayable).getUser().getAvatar() != null) {
          ImageLoader.loadWithShadowCircleTransform(
              ((SocialCardDisplayable) displayable).getUser().getAvatar(), storeAvatar);
        }
      } else {
        storeName.setVisibility(View.GONE);
        storeAvatar.setVisibility(View.GONE);
      }
    }
  }

  private void setCardHeader(TextView storeName, TextView userName, ImageView storeAvatar,
      ImageView userAvatar) {
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
