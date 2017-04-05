package cm.aptoide.pt.v8engine.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.timeline.LikeButtonView;
import cm.aptoide.pt.v8engine.repository.SocialRepository;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewInstallDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.AppUpdateDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.ArticleDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.RecommendationDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SimilarDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SocialArticleDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SocialCardDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SocialInstallDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SocialRecommendationDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SocialStoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SocialVideoDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.StoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.VideoDisplayable;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 09/12/2016.
 */
// FIXME: 27/2/2017 convert into a class that extends "BaseDialog"
public class SharePreviewDialog {

  private final AptoideAccountManager accountManager;
  private final boolean dontShowMeAgainOption;
  private final SharePreviewOpenMode openMode;
  @Nullable private Displayable displayable;
  private boolean privacyResult;

  public SharePreviewDialog(Displayable cardDisplayable, AptoideAccountManager accountManager,
      boolean dontShowMeAgainOption, SharePreviewOpenMode openMode) {
    this.displayable = cardDisplayable;
    this.accountManager = accountManager;
    this.dontShowMeAgainOption = dontShowMeAgainOption;
    this.openMode = openMode;
  }

  public SharePreviewDialog(AptoideAccountManager accountManager, boolean dontShowMeAgainOption,
      SharePreviewOpenMode openMode) {
    this.accountManager = accountManager;
    this.dontShowMeAgainOption = dontShowMeAgainOption;
    this.openMode = openMode;
  }

  public AlertDialog.Builder getPreviewDialogBuilder(Context context) {
    AlertDialog.Builder alertadd = new AlertDialog.Builder(context);
    LayoutInflater factory = LayoutInflater.from(context);
    View view = null;
    CardView cardView;
    TextView sharedBy;
    LinearLayout like;
    LikeButtonView likeButtonView;
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
      ImageLoader.with(context)
          .load(((ArticleDisplayable) displayable).getThumbnailUrl(), thumbnail);
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
      ImageLoader.with(context).load(((VideoDisplayable) displayable).getThumbnailUrl(), thumbnail);
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
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(((StoreLatestAppsDisplayable) displayable).getAvatarUrl(),
              sharedStoreAvatar);
      View latestAppView;
      ImageView latestAppIcon;
      for (StoreLatestAppsDisplayable.LatestApp latestApp : ((StoreLatestAppsDisplayable) displayable)
          .getLatestApps()) {
        latestAppView =
            factory.inflate(R.layout.social_timeline_latest_app, latestAppsContainer, false);
        latestAppIcon = (ImageView) latestAppView.findViewById(R.id.social_timeline_latest_app);
        ImageLoader.with(context).load(latestApp.getIconUrl(), latestAppIcon);
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
      ImageLoader.with(context)
          .load(((RecommendationDisplayable) displayable).getAppIcon(), appIcon);
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
      ImageLoader.with(context).load(((AppUpdateDisplayable) displayable).getAppIconUrl(), appIcon);
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
      ImageLoader.with(context).load(((SimilarDisplayable) displayable).getAppIcon(), appIcon);
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
      ImageLoader.with(context)
          .load(((AppViewInstallDisplayable) displayable).getPojo()
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

      ImageLoader.with(context)
          .load(((SocialArticleDisplayable) displayable).getThumbnailUrl(), thumbnail);
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

      ImageLoader.with(context)
          .load(((SocialVideoDisplayable) displayable).getThumbnailUrl(), thumbnail);
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
      ImageLoader.with(context)
          .load(((SocialRecommendationDisplayable) displayable).getAppIcon(), appIcon);
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
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(
              ((SocialStoreLatestAppsDisplayable) displayable).getSharedStore().getAvatar(),
              sharedStoreAvatar);
      View latestAppView;
      ImageView latestAppIcon;
      for (SocialStoreLatestAppsDisplayable.LatestApp latestApp : ((SocialStoreLatestAppsDisplayable) displayable)
          .getLatestApps()) {
        latestAppView =
            factory.inflate(R.layout.social_timeline_latest_app, latestAppsContainer, false);
        latestAppIcon = (ImageView) latestAppView.findViewById(R.id.social_timeline_latest_app);
        ImageLoader.with(context).load(latestApp.getIconUrl(), latestAppIcon);
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
      ImageLoader.with(context)
          .load(((SocialInstallDisplayable) displayable).getAppIcon(), appIcon);
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
      likeButtonView = (LikeButtonView) view.findViewById(R.id.social_like_button);
      comments = (TextView) view.findViewById(R.id.social_comment);
      TextView numberOfComments = (TextView) view.findViewById(R.id.social_number_of_comments);

      cardView.setRadius(8);
      cardView.setCardElevation(10);
      like.setOnClickListener(null);
      like.setOnTouchListener(null);
      like.setVisibility(View.VISIBLE);
      likeButtonView.setOnClickListener(null);
      likeButtonView.setOnTouchListener(null);
      likeButtonView.setVisibility(View.VISIBLE);
      if (openMode.equals(SharePreviewOpenMode.LIKE)) {
        likeButtonView.setHeartState(true);
        alertadd.setTitle(R.string.social_timeline_you_will_share_like);
      } else if (openMode.equals(SharePreviewOpenMode.COMMENT)) {
        numberOfComments.setText("1");
        numberOfComments.setVisibility(View.VISIBLE);
        alertadd.setTitle(R.string.social_timeline_you_will_share_comment);
      } else {
        alertadd.setTitle(R.string.social_timeline_you_will_share);
      }
      comments.setVisibility(View.VISIBLE);

      alertadd.setView(view).setCancelable(false);

      if (!(displayable instanceof SocialCardDisplayable)) {
        storeName.setText(accountManager.getAccount().getStoreName());
        setCardHeader(context, storeName, userName, storeAvatar, userAvatar);
      } else {
        sharedBy = (TextView) view.findViewById(R.id.social_shared_by);
        setSharedByText(context, sharedBy);
        setSocialCardHeader(context, storeName, userName, storeAvatar, userAvatar);
      }
      if (!accountManager.isAccountAccessConfirmed()) {
        privacyText.setOnClickListener(click -> checkBox.toggle());
        checkBox.setClickable(true);
        storeAvatar.setVisibility(View.VISIBLE);
        storeName.setVisibility(View.VISIBLE);
        handlePrivacyCheckBoxChanges(userName, userAvatar, checkBox, socialTerms);
      }
    }
    return alertadd;
  }

  private void setCardHeader(Context context, TextView storeName, TextView userName,
      ImageView storeAvatar, ImageView userAvatar) {
    if (accountManager.getAccount().getStoreName() != null) {
      if (Account.Access.PUBLIC.equals(accountManager.getAccountAccess())) {
        storeAvatar.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.VISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(accountManager.getAccount().getStoreAvatar(),
                storeAvatar);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(accountManager.getAccount().getAvatar(), userAvatar);
        storeName.setText(accountManager.getAccount().getStoreName());
        userName.setText(accountManager.getAccount().getNickname());
      } else {
        storeAvatar.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.INVISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(accountManager.getAccount().getStoreAvatar(),
                storeAvatar);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(accountManager.getAccount().getAvatar(), userAvatar);
        storeName.setText(accountManager.getAccount().getStoreName());
        userName.setText(accountManager.getAccount().getNickname());
        userName.setVisibility(View.GONE);
      }
    } else {
      if ((Account.Access.PUBLIC).equals(accountManager.getAccountAccess())) {
        storeAvatar.setVisibility(View.VISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(accountManager.getAccount().getAvatar(), storeAvatar);
        userAvatar.setVisibility(View.INVISIBLE);
        storeName.setText(accountManager.getAccount().getNickname());
        userName.setVisibility(View.GONE);
      }
    }
  }

  private void setSharedByText(Context context, TextView sharedBy) {
    sharedBy.setVisibility(View.VISIBLE);

    if (Account.Access.PUBLIC.equals(accountManager.getAccountAccess())) {
      sharedBy.setText(String.format(context.getString(R.string.social_timeline_shared_by),
          accountManager.getAccount().getNickname()));
    } else {
      sharedBy.setText(String.format(context.getString(R.string.social_timeline_shared_by),
          accountManager.getAccount().getStoreName()));
    }
  }

  private void setSocialCardHeader(Context context, TextView storeName, TextView userName,
      ImageView storeAvatar, ImageView userAvatar) {
    if (((SocialCardDisplayable) displayable).getStore() != null) {
      storeName.setVisibility(View.VISIBLE);
      storeAvatar.setVisibility(View.VISIBLE);
      if (((SocialCardDisplayable) displayable).getStore().getName() != null) {
        storeName.setText(((SocialCardDisplayable) displayable).getStore().getName());
      }
      if (((SocialCardDisplayable) displayable).getStore().getAvatar() != null) {
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(
                ((SocialCardDisplayable) displayable).getStore().getAvatar(), storeAvatar);
      }

      if (((SocialCardDisplayable) displayable).getUser() != null) {
        userName.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.VISIBLE);
        if (((SocialCardDisplayable) displayable).getUser().getName() != null) {
          userName.setText(((SocialCardDisplayable) displayable).getUser().getName());
        }

        if (((SocialCardDisplayable) displayable).getUser().getAvatar() != null) {
          ImageLoader.with(context)
              .loadWithShadowCircleTransform(
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
          ImageLoader.with(context)
              .loadWithShadowCircleTransform(
                  ((SocialCardDisplayable) displayable).getUser().getAvatar(), storeAvatar);
        }
      } else {
        storeName.setVisibility(View.GONE);
        storeAvatar.setVisibility(View.GONE);
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

  public AlertDialog.Builder getCustomRecommendationPreviewDialogBuilder(Context context,
      String appName, String appIconUrl) {
    AlertDialog.Builder alertadd = new AlertDialog.Builder(context);
    LayoutInflater factory = LayoutInflater.from(context);
    View view =
        factory.inflate(R.layout.displayable_social_timeline_social_recommendation_preview, null);
    CardView cardView = (CardView) view.findViewById(R.id.card);
    LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
    LikeButtonView likeButtonView = (LikeButtonView) view.findViewById(R.id.social_like_button);
    TextView comments = (TextView) view.findViewById(R.id.social_comment);

    ImageView appIconV =
        (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
    TextView appNameV =
        (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_similar_apps);
    TextView appSubTitle =
        (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_name);
    TextView getApp = (TextView) view.findViewById(
        R.id.displayable_social_timeline_recommendation_get_app_button);
    ImageLoader.with(context).load(appIconUrl, appIconV);
    appNameV.setText(appName);
    appSubTitle.setText(AptoideUtils.StringU.getFormattedString(
        R.string.displayable_social_timeline_recommendation_atptoide_team_recommends, ""));

    SpannableFactory spannableFactory = new SpannableFactory();

    getApp.setText(spannableFactory.createColorSpan(
        context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
        ContextCompat.getColor(context, R.color.appstimeline_grey), ""));

    TextView storeName = (TextView) view.findViewById(R.id.card_title);
    TextView userName = (TextView) view.findViewById(R.id.card_subtitle);
    ImageView storeAvatar = (ImageView) view.findViewById(R.id.card_image);
    ImageView userAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
    CheckBox checkBox = (CheckBox) view.findViewById(R.id.social_preview_checkbox);
    LinearLayout socialTerms = (LinearLayout) view.findViewById(R.id.social_privacy_terms);
    TextView privacyText = (TextView) view.findViewById(R.id.social_text_privacy);

    cardView.setRadius(8);
    cardView.setCardElevation(10);
    like.setOnClickListener(null);
    like.setOnTouchListener(null);
    like.setVisibility(View.VISIBLE);
    likeButtonView.setOnClickListener(null);
    likeButtonView.setOnTouchListener(null);
    likeButtonView.setVisibility(View.VISIBLE);
    comments.setVisibility(View.VISIBLE);

    alertadd.setView(view).setCancelable(false);
    alertadd.setTitle(R.string.social_timeline_you_will_share);

    storeName.setText(accountManager.getAccount().getStoreName());
    setCardHeader(context, storeName, userName, storeAvatar, userAvatar);

    if (!accountManager.isAccountAccessConfirmed()) {
      privacyText.setOnClickListener(click -> checkBox.toggle());
      checkBox.setClickable(true);
      storeAvatar.setVisibility(View.VISIBLE);
      storeName.setVisibility(View.VISIBLE);
      handlePrivacyCheckBoxChanges(userName, userAvatar, checkBox, socialTerms);
    }

    return alertadd;
  }

  public void showShareCardPreviewDialog(String packageName, String shareType, Context context,
      SharePreviewDialog sharePreviewDialog, AlertDialog.Builder alertDialog,
      SocialRepository socialRepository) {
    Observable.create((Subscriber<? super GenericDialogs.EResponse> subscriber) -> {
      if (!accountManager.isAccountAccessConfirmed()) {
        alertDialog.setPositiveButton(R.string.share, (dialogInterface, i) -> {
          socialRepository.share(packageName, shareType, sharePreviewDialog.getPrivacyResult());
          subscriber.onNext(GenericDialogs.EResponse.YES);
          subscriber.onCompleted();
        }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
          subscriber.onNext(GenericDialogs.EResponse.NO);
          subscriber.onCompleted();
        });
      } else {
        alertDialog.setPositiveButton(R.string.continue_option, (dialogInterface, i) -> {
          socialRepository.share(packageName, shareType);
          subscriber.onNext(GenericDialogs.EResponse.YES);
          subscriber.onCompleted();
        }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
          subscriber.onNext(GenericDialogs.EResponse.NO);
          subscriber.onCompleted();
        });
        if (dontShowMeAgainOption) {
          alertDialog.setNeutralButton(R.string.dont_show_this_again, (dialogInterface, i) -> {
            subscriber.onNext(GenericDialogs.EResponse.CANCEL);
            subscriber.onCompleted();
            ManagerPreferences.setShowPreviewDialog(false);
          });
        }
      }

      alertDialog.show();
    }).subscribeOn(AndroidSchedulers.mainThread()).subscribe(eResponse -> {
      switch (eResponse) {
        case YES:
          ShowMessage.asSnack((Activity) context, R.string.social_timeline_share_dialog_title);
          break;
        case NO:
          break;
        case CANCEL:
          break;
      }
    });
  }

  public boolean getPrivacyResult() {
    return this.privacyResult;
  }

  public enum SharePreviewOpenMode {
    COMMENT, LIKE, SHARE
  }
}
