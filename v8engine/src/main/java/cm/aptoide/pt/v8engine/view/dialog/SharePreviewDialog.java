package cm.aptoide.pt.v8engine.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewInstallDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
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
  private final TimelineAnalytics timelineAnalytics;
  private final SharedPreferences sharedPreferences;

  @Nullable private Displayable displayable;
  private boolean privacyResult;

  public SharePreviewDialog(Displayable cardDisplayable, AptoideAccountManager accountManager,
      boolean dontShowMeAgainOption, SharePreviewOpenMode openMode,
      TimelineAnalytics timelineAnalytics, SharedPreferences sharedPreferences) {
    this.displayable = cardDisplayable;
    this.accountManager = accountManager;
    this.dontShowMeAgainOption = dontShowMeAgainOption;
    this.openMode = openMode;
    this.timelineAnalytics = timelineAnalytics;
    this.sharedPreferences = sharedPreferences;
  }

  public SharePreviewDialog(AptoideAccountManager accountManager, boolean dontShowMeAgainOption,
      SharePreviewOpenMode openMode, TimelineAnalytics timelineAnalytics,
      SharedPreferences sharedPreferences) {
    this.accountManager = accountManager;
    this.dontShowMeAgainOption = dontShowMeAgainOption;
    this.openMode = openMode;
    this.timelineAnalytics = timelineAnalytics;
    this.sharedPreferences = sharedPreferences;
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
    LinearLayout socialInfoBar;
    LinearLayout socialCommentBar;

    if (displayable instanceof AppViewInstallDisplayable) {
      view = factory.inflate(R.layout.displayable_social_timeline_social_install_preview, null);
      ImageView appIcon =
          (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
      TextView appName = (TextView) view.findViewById(
          R.id.displayable_social_timeline_recommendation_similar_apps);
      RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
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

      ratingBar.setRating(((AppViewInstallDisplayable) displayable).getPojo()
          .getNodes()
          .getMeta()
          .getData()
          .getStats()
          .getRating()
          .getAvg());

      SpannableFactory spannableFactory = new SpannableFactory();

      getApp.setText(spannableFactory.createColorSpan(
          context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
          ContextCompat.getColor(context, R.color.appstimeline_grey), ""));
    } else {
      throw new IllegalStateException(
          "The Displayable " + displayable + " is not being handled " + "in SharePreviewDialog");
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
      socialInfoBar = (LinearLayout) view.findViewById(R.id.social_info_bar);
      socialCommentBar = (LinearLayout) view.findViewById(R.id.social_latest_comment_bar);

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
      socialInfoBar.setVisibility(View.GONE);
      socialCommentBar.setVisibility(View.GONE);

      alertadd.setView(view)
          .setCancelable(false);

      storeName.setText(accountManager.getAccount()
          .getStore()
          .getName());
      setCardHeader(context, storeName, userName, storeAvatar, userAvatar);

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
    if (accountManager.getAccount()
        .hasStore()) {
      storeName.setTextColor(ContextCompat.getColor(context, R.color.black_87_alpha));
      if (accountManager.getAccount()
          .isPublicUser()) {
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
    } else {
      if (accountManager.getAccount()
          .isPublicUser()) {
        storeAvatar.setVisibility(View.VISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(accountManager.getAccount()
                .getAvatar(), storeAvatar);
        userAvatar.setVisibility(View.INVISIBLE);
        storeName.setText(accountManager.getAccount()
            .getNickname());
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

  public AlertDialog.Builder getCustomRecommendationPreviewDialogBuilder(Context context,
      String appName, String appIconUrl, float rating) {
    AlertDialog.Builder alertadd = new AlertDialog.Builder(context);
    LayoutInflater factory = LayoutInflater.from(context);
    View view =
        factory.inflate(R.layout.displayable_social_timeline_social_recommendation_preview, null);
    ImageView appIcon =
        (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
    TextView appNameT =
        (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_similar_apps);
    TextView getApp = (TextView) view.findViewById(
        R.id.displayable_social_timeline_recommendation_get_app_button);
    RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);

    if (appIconUrl != null) {
      ImageLoader.with(context)
          .load(appIconUrl, appIcon);
    }
    appNameT.setText(appName);
    ratingBar.setRating(rating);
    SpannableFactory spannableFactory = new SpannableFactory();

    getApp.setText(spannableFactory.createColorSpan(
        context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
        ContextCompat.getColor(context, R.color.appstimeline_grey), ""));

    //
    CardView cardView = (CardView) view.findViewById(R.id.card);
    LinearLayout like = (LinearLayout) view.findViewById(R.id.social_like);
    LikeButtonView likeButtonView = (LikeButtonView) view.findViewById(R.id.social_like_button);
    TextView comments = (TextView) view.findViewById(R.id.social_comment);
    LinearLayout socialInfoBar;
    LinearLayout socialCommentBar;

    socialInfoBar = (LinearLayout) view.findViewById(R.id.social_info_bar);
    socialCommentBar = (LinearLayout) view.findViewById(R.id.social_latest_comment_bar);

    socialInfoBar.setVisibility(View.GONE);
    socialCommentBar.setVisibility(View.GONE);

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

    alertadd.setView(view)
        .setCancelable(false);
    alertadd.setTitle(R.string.social_timeline_you_will_share);

    storeName.setText(accountManager.getAccount()
        .getStore()
        .getName());
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

  public void showShareCardPreviewDialog(String packageName, Long storeId, String shareType,
      Context context, SharePreviewDialog sharePreviewDialog, AlertDialog.Builder alertDialog,
      SocialRepository socialRepository) {
    Observable.create((Subscriber<? super GenericDialogs.EResponse> subscriber) -> {
      if (!accountManager.isAccountAccessConfirmed()) {
        alertDialog.setPositiveButton(R.string.share, (dialogInterface, i) -> {
          socialRepository.share(packageName, storeId, shareType,
              sharePreviewDialog.getPrivacyResult());
          subscriber.onNext(GenericDialogs.EResponse.YES);
          subscriber.onCompleted();
        })
            .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
              subscriber.onNext(GenericDialogs.EResponse.NO);
              subscriber.onCompleted();
            });
      } else {
        alertDialog.setPositiveButton(R.string.continue_option, (dialogInterface, i) -> {
          socialRepository.share(packageName, storeId, shareType);
          subscriber.onNext(GenericDialogs.EResponse.YES);
          subscriber.onCompleted();
        })
            .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
              subscriber.onNext(GenericDialogs.EResponse.NO);
              subscriber.onCompleted();
            });
        if (dontShowMeAgainOption) {
          alertDialog.setNeutralButton(R.string.dont_show_this_again, (dialogInterface, i) -> {
            subscriber.onNext(GenericDialogs.EResponse.CANCEL);
            subscriber.onCompleted();
            ManagerPreferences.setShowPreviewDialog(false, sharedPreferences);
          });
        }
      }

      alertDialog.show();
    })
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(eResponse -> {
          switch (eResponse) {
            case YES:
              ShowMessage.asSnack((Activity) context, R.string.social_timeline_share_dialog_title);
              timelineAnalytics.sendSocialCardPreviewActionEvent(
                  TimelineAnalytics.SOCIAL_CARD_ACTION_SHARE_CONTINUE);
              break;
            case NO:
              break;
            case CANCEL:
              timelineAnalytics.sendSocialCardPreviewActionEvent(
                  TimelineAnalytics.SOCIAL_CARD_ACTION_SHARE_CANCEL);
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
