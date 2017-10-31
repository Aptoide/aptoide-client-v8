package cm.aptoide.pt.store.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;

public class BadgeDialogFactory {
  public static final float MEDAL_SCALE = 1.25f;
  private final Context context;
  private int normalMedalSize;
  private int selectedMedalSize;

  public BadgeDialogFactory(Context context) {
    this.context = context;
  }

  public Dialog create(GridStoreMetaWidget.HomeMeta.Badge badge, boolean storeOwner) {
    View view = LayoutInflater.from(context)
        .inflate(R.layout.store_badge_dialog, null);
    ImageView bronzeMedal = ((ImageView) view.findViewById(R.id.bronze_medal));
    normalMedalSize = bronzeMedal.getLayoutParams().width;
    selectedMedalSize = (int) (normalMedalSize * MEDAL_SCALE);
    Resources resources = view.getContext()
        .getResources();
    createViewModel(badge, resources, badge, view, storeOwner);
    Dialog dialog = new AlertDialog.Builder(context).setView(view)
        .create();
    View okButton = view.findViewById(R.id.ok_button);
    okButton.setOnClickListener(v -> dialog.dismiss());
    return dialog;
  }

  private void createViewModel(GridStoreMetaWidget.HomeMeta.Badge storeBadge, Resources resources,
      GridStoreMetaWidget.HomeMeta.Badge selectedBadge, View view, boolean storeOwner) {

    @ColorRes int mainColor;
    @ColorRes int secondaryColor;
    @DrawableRes int storeMedal;
    String medalText;
    String congratulationsMessage;
    String uploadedAppsMessage;
    String downloadsMessage;
    String followersMessage;
    String reviewsMessage;
    boolean tinBadgeSelected = false;
    boolean bronzeBadgeSelected = false;
    boolean silverBadgeSelected = false;
    boolean goldBadgeSelected = false;
    boolean platinumBadgeSelected = false;
    @ColorRes int tinBadgeColor;
    @ColorRes int bronzeBadgeColor;
    @ColorRes int silverBadgeColor;
    @ColorRes int goldBadgeColor;
    @ColorRes int platinumBadgeColor;
    @ColorRes int progress1;
    @ColorRes int progress2;
    @ColorRes int progress3;
    @ColorRes int progress4;

    switch (selectedBadge) {
      case NONE:
      default:
        mainColor = R.color.tin_medal;
        secondaryColor = R.color.tin_medal_secodary;
        storeMedal = R.drawable.tin;
        medalText = resources.getString(R.string.badgedialog_title_tin);
        congratulationsMessage = resources.getString(R.string.badgedialog_message_tin);
        uploadedAppsMessage = resources.getString(R.string.badgedialog_message_tin_1);
        downloadsMessage = "";
        followersMessage = "";
        reviewsMessage = "";
        tinBadgeSelected = true;
        break;
      case BRONZE:
        if (isRankLocked(storeBadge, selectedBadge)) {
          storeMedal = R.drawable.lock;
          mainColor = R.color.grey_fog_dark;
          secondaryColor = R.color.grey_fog_light;
          congratulationsMessage = resources.getString(R.string.badgedialog_message_bronze_lock);
        } else {
          storeMedal = R.drawable.bronze;
          mainColor = R.color.bronze_medal;
          secondaryColor = R.color.bronze_medal_secodary;
          congratulationsMessage = resources.getString(R.string.badgedialog_message_bronze);
        }
        medalText = resources.getString(R.string.badgedialog_title_bronze);
        uploadedAppsMessage = resources.getString(R.string.badgedialog_message_bronze_1);
        downloadsMessage = resources.getString(R.string.badgedialog_message_bronze_2);
        followersMessage = resources.getString(R.string.badgedialog_message_bronze_3);
        reviewsMessage = resources.getString(R.string.badgedialog_message_bronze_4);
        bronzeBadgeSelected = true;
        break;
      case SILVER:
        if (isRankLocked(storeBadge, selectedBadge)) {
          storeMedal = R.drawable.lock;
          mainColor = R.color.grey_fog_dark;
          secondaryColor = R.color.grey_fog_light;
          congratulationsMessage = resources.getString(R.string.badgedialog_message_silver_lock);
        } else {
          storeMedal = R.drawable.silver;
          mainColor = R.color.silver_medal;
          secondaryColor = R.color.silver_medal_secodary;
          congratulationsMessage = resources.getString(R.string.badgedialog_message_silver);
        }
        medalText = resources.getString(R.string.badgedialog_title_silver);
        uploadedAppsMessage = resources.getString(R.string.badgedialog_message_silver_1);
        downloadsMessage = resources.getString(R.string.badgedialog_message_silver_2);
        followersMessage = resources.getString(R.string.badgedialog_message_silver_3);
        reviewsMessage = resources.getString(R.string.badgedialog_message_silver_4);
        silverBadgeSelected = true;
        break;
      case GOLD:
        if (isRankLocked(storeBadge, selectedBadge)) {
          storeMedal = R.drawable.lock;
          mainColor = R.color.grey_fog_dark;
          secondaryColor = R.color.grey_fog_light;
          congratulationsMessage = resources.getString(R.string.badgedialog_message_gold_lock);
        } else {
          storeMedal = R.drawable.gold;
          mainColor = R.color.gold_medal;
          secondaryColor = R.color.gold_medal_secodary;
          congratulationsMessage = resources.getString(R.string.badgedialog_message_gold);
        }
        medalText = resources.getString(R.string.badgedialog_title_gold);
        uploadedAppsMessage = resources.getString(R.string.badgedialog_message_gold_1);
        downloadsMessage = resources.getString(R.string.badgedialog_message_gold_2);
        followersMessage = resources.getString(R.string.badgedialog_message_gold_3);
        reviewsMessage = resources.getString(R.string.badgedialog_message_gold_4);
        goldBadgeSelected = true;
        break;
      case PLATINUM:
        if (isRankLocked(storeBadge, selectedBadge)) {
          storeMedal = R.drawable.lock;
          mainColor = R.color.grey_fog_dark;
          secondaryColor = R.color.grey_fog_light;
          congratulationsMessage = resources.getString(R.string.badgedialog_message_platinum_lock);
        } else {
          storeMedal = R.drawable.platinum;
          mainColor = R.color.platinum_medal;
          secondaryColor = R.color.platinum_medal_secodary;
          congratulationsMessage = resources.getString(R.string.badgedialog_message_platinum);
        }
        medalText = resources.getString(R.string.badgedialog_title_platinum);
        uploadedAppsMessage = resources.getString(R.string.badgedialog_message_platinum_1);
        downloadsMessage = resources.getString(R.string.badgedialog_message_platinum_2);
        followersMessage = resources.getString(R.string.badgedialog_message_platinum_3);
        reviewsMessage = resources.getString(R.string.badgedialog_message_platinum_4);
        platinumBadgeSelected = true;
        break;
    }

    tinBadgeColor = getProgressColor(mainColor, secondaryColor, storeBadge, selectedBadge,
        GridStoreMetaWidget.HomeMeta.Badge.NONE);

    bronzeBadgeColor = getProgressColor(mainColor, secondaryColor, storeBadge, selectedBadge,
        GridStoreMetaWidget.HomeMeta.Badge.BRONZE);
    progress1 = bronzeBadgeColor;

    silverBadgeColor = getProgressColor(mainColor, secondaryColor, storeBadge, selectedBadge,
        GridStoreMetaWidget.HomeMeta.Badge.SILVER);
    progress2 = silverBadgeColor;

    goldBadgeColor = getProgressColor(mainColor, secondaryColor, storeBadge, selectedBadge,
        GridStoreMetaWidget.HomeMeta.Badge.GOLD);
    progress3 = goldBadgeColor;

    platinumBadgeColor = getProgressColor(mainColor, secondaryColor, storeBadge, selectedBadge,
        GridStoreMetaWidget.HomeMeta.Badge.PLATINUM);
    progress4 = platinumBadgeColor;

    StoreMedalPopupViewModel storeMedalPopupViewModel =
        new StoreMedalPopupViewModel(storeBadge, mainColor, storeMedal, medalText,
            congratulationsMessage, uploadedAppsMessage, downloadsMessage, followersMessage,
            reviewsMessage, tinBadgeSelected, bronzeBadgeSelected, silverBadgeSelected,
            goldBadgeSelected, platinumBadgeSelected, tinBadgeColor, bronzeBadgeColor,
            silverBadgeColor, goldBadgeColor, platinumBadgeColor, progress1, progress2, progress3,
            progress4, !isRankLocked(storeBadge, selectedBadge), storeOwner);

    setupView(view, storeMedalPopupViewModel, resources);
  }

  private boolean isRankLocked(GridStoreMetaWidget.HomeMeta.Badge storeBadge,
      GridStoreMetaWidget.HomeMeta.Badge selectedBadge) {
    return storeBadge.ordinal() < selectedBadge.ordinal();
  }

  private int getProgressColor(int mainColor, int secondaryColor,
      GridStoreMetaWidget.HomeMeta.Badge storeBadge,
      GridStoreMetaWidget.HomeMeta.Badge selectedBadge,
      GridStoreMetaWidget.HomeMeta.Badge currentSetup) {
    int tinBadgeColor = R.color.progress_bar_color;
    if (currentSetup.ordinal() <= storeBadge.ordinal()
        && currentSetup.ordinal() <= selectedBadge.ordinal()) {
      tinBadgeColor = mainColor;
    } else if (currentSetup.ordinal() <= storeBadge.ordinal()) {
      tinBadgeColor = secondaryColor;
    }
    return tinBadgeColor;
  }

  private void setupView(View view, StoreMedalPopupViewModel viewModel, Resources resources) {
    ImageView headerBackground = ((ImageView) view.findViewById(R.id.header_background));
    View followersBackground = view.findViewById(R.id.followers_background);
    ImageView medalIcon = ((ImageView) view.findViewById(R.id.medal_icon));
    ImageView tinMedal = ((ImageView) view.findViewById(R.id.tin_medal));
    ImageView bronzeMedal = ((ImageView) view.findViewById(R.id.bronze_medal));
    ImageView silverMedal = ((ImageView) view.findViewById(R.id.silver_medal));
    ImageView goldMedal = ((ImageView) view.findViewById(R.id.gold_medal));
    ImageView platinumMedal = ((ImageView) view.findViewById(R.id.platinum_medal));
    View tinMedalLayout = view.findViewById(R.id.tin_medal_layout);
    View bronzeMedalLayout = view.findViewById(R.id.bronze_medal_layout);
    View silverMedalLayout = view.findViewById(R.id.silver_medal_layout);
    View goldMedalLayout = view.findViewById(R.id.gold_medal_layout);
    View platinumMedalLayout = view.findViewById(R.id.platinum_medal_layout);
    TextView medalText = (TextView) view.findViewById(R.id.medal_title);
    TextView congratulationsMessage = (TextView) view.findViewById(R.id.congratulations_message);
    TextView uploadedAppsTv = (TextView) view.findViewById(R.id.uploaded_apps);
    TextView downloadsTv = (TextView) view.findViewById(R.id.downloads);
    TextView followersTv = (TextView) view.findViewById(R.id.followers);
    TextView reviewsTv = (TextView) view.findViewById(R.id.reviews);
    View progress1 = view.findViewById(R.id.progress1);
    View progress2 = view.findViewById(R.id.progress2);
    View progress3 = view.findViewById(R.id.progress3);
    View progress4 = view.findViewById(R.id.progress4);

    headerBackground.setBackgroundColor(resources.getColor(viewModel.getMainColor()));
    medalIcon.setImageDrawable(resources.getDrawable(viewModel.getStoreMedalIcon()));
    medalText.setText(viewModel.getMedalText());
    congratulationsMessage.setText(viewModel.getCongratulationsMessage());

    if (viewModel.isShowChecks()) {
      setDrawableColor(resources, viewModel.getMainColor(), uploadedAppsTv);
      setDrawableColor(resources, viewModel.getMainColor(), downloadsTv);
      setDrawableColor(resources, viewModel.getMainColor(), followersTv);
      setDrawableColor(resources, viewModel.getMainColor(), reviewsTv);
    } else {
      setDrawableColor(resources, R.color.grey_fog_light, uploadedAppsTv);
      setDrawableColor(resources, R.color.white, downloadsTv);
      setDrawableColor(resources, R.color.grey_fog_light, followersTv);
      setDrawableColor(resources, R.color.white, reviewsTv);
    }

    String uploadedAppsMessage = viewModel.getUploadedAppsMessage();
    if (uploadedAppsMessage != null && !uploadedAppsMessage.isEmpty()) {
      uploadedAppsTv.setText(uploadedAppsMessage);
      uploadedAppsTv.setVisibility(View.VISIBLE);
    } else {
      uploadedAppsTv.setVisibility(View.GONE);
    }
    String downloadsMessage = viewModel.getDownloadsMessage();
    if (downloadsMessage != null && !downloadsMessage.isEmpty()) {
      downloadsTv.setText(downloadsMessage);
      downloadsTv.setVisibility(View.VISIBLE);
    } else {
      downloadsTv.setVisibility(View.GONE);
    }
    String followersMessage = viewModel.getFollowersMessage();
    if (followersMessage != null && !followersMessage.isEmpty()) {
      followersTv.setText(followersMessage);
      followersTv.setVisibility(View.VISIBLE);
      followersBackground.setVisibility(View.VISIBLE);
    } else {
      followersTv.setVisibility(View.GONE);
      followersBackground.setVisibility(View.GONE);
    }
    String reviewsMessage = viewModel.getReviewsMessage();
    if (reviewsMessage != null && !reviewsMessage.isEmpty()) {
      reviewsTv.setText(reviewsMessage);
      reviewsTv.setVisibility(View.VISIBLE);
    } else {
      reviewsTv.setVisibility(View.GONE);
    }

    GridStoreMetaWidget.HomeMeta.Badge storeBadge = viewModel.getStoreBadge();
    setupMedal(tinMedal, viewModel.isTinBadgeSelected(), viewModel.getTinBadgeColor(), resources);
    setupMedal(bronzeMedal, viewModel.isBronzeBadgeSelected(), viewModel.getBronzeBadgeColor(),
        resources);
    setupMedal(silverMedal, viewModel.isSilverBadgeSelected(), viewModel.getSilverBadgeColor(),
        resources);
    setupMedal(goldMedal, viewModel.isGoldBadgeSelected(), viewModel.getGoldBadgeColor(),
        resources);
    setupMedal(platinumMedal, viewModel.isPlatinumBadgeSelected(),
        viewModel.getPlatinumBadgeColor(), resources);

    tinMedalLayout.setOnClickListener(
        v -> createViewModel(storeBadge, resources, GridStoreMetaWidget.HomeMeta.Badge.NONE, view,
            viewModel.isStoreOwner()));
    bronzeMedalLayout.setOnClickListener(
        v -> createViewModel(storeBadge, resources, GridStoreMetaWidget.HomeMeta.Badge.BRONZE, view,
            viewModel.isStoreOwner()));
    silverMedalLayout.setOnClickListener(
        v -> createViewModel(storeBadge, resources, GridStoreMetaWidget.HomeMeta.Badge.SILVER, view,
            viewModel.isStoreOwner()));
    goldMedalLayout.setOnClickListener(
        v -> createViewModel(storeBadge, resources, GridStoreMetaWidget.HomeMeta.Badge.GOLD, view,
            viewModel.isStoreOwner()));
    platinumMedalLayout.setOnClickListener(
        v -> createViewModel(storeBadge, resources, GridStoreMetaWidget.HomeMeta.Badge.PLATINUM,
            view, viewModel.isStoreOwner()));

    progress1.setBackgroundColor(resources.getColor(viewModel.getProgress1()));
    progress2.setBackgroundColor(resources.getColor(viewModel.getProgress2()));
    progress3.setBackgroundColor(resources.getColor(viewModel.getProgress3()));
    progress4.setBackgroundColor(resources.getColor(viewModel.getProgress4()));

    if (viewModel.isStoreOwner()) {
      congratulationsMessage.setVisibility(View.VISIBLE);
      tinMedalLayout.setVisibility(View.VISIBLE);
      bronzeMedalLayout.setVisibility(View.VISIBLE);
      silverMedalLayout.setVisibility(View.VISIBLE);
      goldMedalLayout.setVisibility(View.VISIBLE);
      platinumMedalLayout.setVisibility(View.VISIBLE);
      progress1.setVisibility(View.VISIBLE);
      progress2.setVisibility(View.VISIBLE);
      progress3.setVisibility(View.VISIBLE);
      progress4.setVisibility(View.VISIBLE);
    } else {
      congratulationsMessage.setVisibility(View.GONE);
      tinMedalLayout.setVisibility(View.GONE);
      bronzeMedalLayout.setVisibility(View.GONE);
      silverMedalLayout.setVisibility(View.GONE);
      goldMedalLayout.setVisibility(View.GONE);
      platinumMedalLayout.setVisibility(View.GONE);
      progress1.setVisibility(View.GONE);
      progress2.setVisibility(View.GONE);
      progress3.setVisibility(View.GONE);
      progress4.setVisibility(View.GONE);
    }
  }

  private void setBackground(ImageView view, int color) {
    GradientDrawable shape = new GradientDrawable();
    shape.setShape(GradientDrawable.OVAL);
    shape.setColor(color);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      view.setBackground(shape);
    } else {
      view.setBackgroundDrawable(shape);
    }
  }

  private void setupMedal(ImageView badge, boolean isBadgeSelected, int badgeColor,
      Resources resources) {
    if (isBadgeSelected) {
      badge.getLayoutParams().width = selectedMedalSize;
      badge.getLayoutParams().height = selectedMedalSize;
      badge.setScaleType(ImageView.ScaleType.FIT_XY);
      badge.requestLayout();
    } else {
      badge.getLayoutParams().width = normalMedalSize;
      badge.getLayoutParams().height = normalMedalSize;
      badge.setScaleType(ImageView.ScaleType.FIT_XY);
      badge.requestLayout();
    }
    Drawable drawable = badge.getDrawable();
    setDrawableColor(resources, badgeColor, drawable);
    badge.setImageDrawable(drawable);
    setBackground(badge, resources.getColor(R.color.white));
  }

  private void setDrawableColor(Resources resources, @ColorRes int color, TextView view) {

    Drawable[] compoundDrawables = view.getCompoundDrawables();
    for (int i = 0, compoundDrawablesLength = compoundDrawables.length; i < compoundDrawablesLength;
        i++) {
      Drawable drawable = compoundDrawables[i];
      if (drawable != null) {
        drawable.mutate();
        drawable.setColorFilter(
            new PorterDuffColorFilter(resources.getColor(color), PorterDuff.Mode.SRC_IN));
        compoundDrawables[i] = drawable;
      }
    }
    view.setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], compoundDrawables[1],
        compoundDrawables[2], compoundDrawables[3]);
  }

  private void setDrawableColor(Resources resources, @ColorRes int color,
      Drawable... compoundDrawables) {
    for (Drawable drawable : compoundDrawables) {
      if (drawable != null) {
        drawable.setColorFilter(
            new PorterDuffColorFilter(resources.getColor(color), PorterDuff.Mode.SRC_IN));
      }
    }
  }
}
