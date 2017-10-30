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

/**
 * Created by trinkes on 25/10/2017.
 */

public class BadgeDialogFactory {
  public static final float MEDAL_SCALE = 1.25f;
  private final Context context;

  public BadgeDialogFactory(Context context) {
    this.context = context;
  }

  public Dialog create(GridStoreMetaWidget.HomeMeta.Badge badge) {
    View view = LayoutInflater.from(context)
        .inflate(R.layout.store_badge_dialog, null);
    Resources resources = view.getContext()
        .getResources();
    setupView(view, createViewModel(badge, resources), resources);
    Dialog dialog = new AlertDialog.Builder(context).setView(view)
        .create();
    return dialog;
  }

  private StoreMedalPopupViewModel createViewModel(GridStoreMetaWidget.HomeMeta.Badge badge,
      Resources resources) {

    @ColorRes int mainColor;
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
    @ColorRes int tinBadgeColor = R.color.progress_bar_color;
    @ColorRes int bronzeBadgeColor = R.color.progress_bar_color;
    @ColorRes int silverBadgeColor = R.color.progress_bar_color;
    @ColorRes int goldBadgeColor = R.color.progress_bar_color;
    @ColorRes int platinumBadgeColor = R.color.progress_bar_color;
    @ColorRes int progress1 = R.color.progress_bar_color;
    @ColorRes int progress2 = R.color.progress_bar_color;
    @ColorRes int progress3 = R.color.progress_bar_color;
    @ColorRes int progress4 = R.color.progress_bar_color;

    switch (badge) {
      case NONE:
      default:
        mainColor = R.color.tin_medal;
        storeMedal = R.drawable.tin;
        medalText = resources.getString(R.string.badgedialog_title_bronze);
        congratulationsMessage = resources.getString(R.string.badgedialog_message_bronze);
        uploadedAppsMessage = resources.getString(R.string.badgedialog_message_bronze_1);
        downloadsMessage = resources.getString(R.string.badgedialog_message_bronze_2);
        followersMessage = resources.getString(R.string.badgedialog_message_bronze_3);
        reviewsMessage = resources.getString(R.string.badgedialog_message_bronze_4);
        tinBadgeSelected = true;
        break;
      case BRONZE:
        storeMedal = R.drawable.bronze;
        mainColor = R.color.bronze_medal;
        medalText = resources.getString(R.string.badgedialog_title_bronze);
        congratulationsMessage = resources.getString(R.string.badgedialog_message_bronze);
        uploadedAppsMessage = resources.getString(R.string.badgedialog_message_bronze_1);
        downloadsMessage = resources.getString(R.string.badgedialog_message_bronze_2);
        followersMessage = resources.getString(R.string.badgedialog_message_bronze_3);
        reviewsMessage = resources.getString(R.string.badgedialog_message_bronze_4);
        bronzeBadgeSelected = true;
        break;
      case SILVER:
        storeMedal = R.drawable.silver;
        mainColor = R.color.silver_medal;
        medalText = resources.getString(R.string.badgedialog_title_silver);
        congratulationsMessage = resources.getString(R.string.badgedialog_message_silver);
        uploadedAppsMessage = resources.getString(R.string.badgedialog_message_silver_1);
        downloadsMessage = resources.getString(R.string.badgedialog_message_silver_2);
        followersMessage = resources.getString(R.string.badgedialog_message_silver_3);
        reviewsMessage = resources.getString(R.string.badgedialog_message_silver_4);
        silverBadgeSelected = true;
        break;
      case GOLD:
        storeMedal = R.drawable.gold;
        mainColor = R.color.gold_medal;
        medalText = resources.getString(R.string.badgedialog_title_gold);
        congratulationsMessage = resources.getString(R.string.badgedialog_message_gold);
        uploadedAppsMessage = resources.getString(R.string.badgedialog_message_gold_1);
        downloadsMessage = resources.getString(R.string.badgedialog_message_gold_2);
        followersMessage = resources.getString(R.string.badgedialog_message_gold_3);
        reviewsMessage = resources.getString(R.string.badgedialog_message_gold_4);
        goldBadgeSelected = true;
        break;
      case PLATINUM:
        storeMedal = R.drawable.platinum;
        mainColor = R.color.platinum_medal;
        medalText = resources.getString(R.string.badgedialog_title_platinum);
        congratulationsMessage = resources.getString(R.string.badgedialog_message_platinum);
        uploadedAppsMessage = resources.getString(R.string.badgedialog_message_platinum_1);
        downloadsMessage = resources.getString(R.string.badgedialog_message_platinum_2);
        followersMessage = resources.getString(R.string.badgedialog_message_platinum_3);
        reviewsMessage = resources.getString(R.string.badgedialog_message_platinum_4);
        platinumBadgeSelected = true;
        break;
    }

    int badgeRank = badge.ordinal();
    if (badgeRank >= GridStoreMetaWidget.HomeMeta.Badge.NONE.ordinal()) {
      tinBadgeColor = mainColor;
    }
    if (badgeRank >= GridStoreMetaWidget.HomeMeta.Badge.BRONZE.ordinal()) {
      bronzeBadgeColor = mainColor;
      progress1 = mainColor;
    }
    if (badgeRank >= GridStoreMetaWidget.HomeMeta.Badge.SILVER.ordinal()) {
      silverBadgeColor = mainColor;
      progress2 = mainColor;
    }
    if (badgeRank >= GridStoreMetaWidget.HomeMeta.Badge.GOLD.ordinal()) {
      goldBadgeColor = mainColor;
      progress3 = mainColor;
    }
    if (badgeRank >= GridStoreMetaWidget.HomeMeta.Badge.PLATINUM.ordinal()) {
      platinumBadgeColor = mainColor;
      progress4 = mainColor;
    }

    return new StoreMedalPopupViewModel(mainColor, storeMedal, medalText, congratulationsMessage,
        uploadedAppsMessage, downloadsMessage, followersMessage, reviewsMessage, tinBadgeSelected,
        bronzeBadgeSelected, silverBadgeSelected, goldBadgeSelected, platinumBadgeSelected,
        tinBadgeColor, bronzeBadgeColor, silverBadgeColor, goldBadgeColor, platinumBadgeColor,
        progress1, progress2, progress3, progress4);
  }

  public void setupView(View view, StoreMedalPopupViewModel viewModel, Resources resources) {
    ImageView headerBackground = ((ImageView) view.findViewById(R.id.header_background));
    ImageView medalIcon = ((ImageView) view.findViewById(R.id.medal_icon));
    ImageView tinMedal = ((ImageView) view.findViewById(R.id.tin_medal));
    ImageView bronzeMedal = ((ImageView) view.findViewById(R.id.bronze_medal));
    ImageView silverMedal = ((ImageView) view.findViewById(R.id.silver_medal));
    ImageView goldMedal = ((ImageView) view.findViewById(R.id.gold_medal));
    ImageView platinumMedal = ((ImageView) view.findViewById(R.id.platinum_medal));
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
    medalIcon.setImageDrawable(resources.getDrawable(viewModel.getStoreMedal()));
    medalText.setText(viewModel.getMedalText());
    congratulationsMessage.setText(viewModel.getCongratulationsMessage());
    uploadedAppsTv.setText(viewModel.getUploadedAppsMessage());
    setDrawableColor(resources, viewModel.getMainColor(), uploadedAppsTv.getCompoundDrawables());
    downloadsTv.setText(viewModel.getDownloadsMessage());
    setDrawableColor(resources, viewModel.getMainColor(), downloadsTv.getCompoundDrawables());
    followersTv.setText(viewModel.getFollowersMessage());
    setDrawableColor(resources, viewModel.getMainColor(), followersTv.getCompoundDrawables());
    reviewsTv.setText(viewModel.getReviewsMessage());
    setDrawableColor(resources, viewModel.getMainColor(), reviewsTv.getCompoundDrawables());

    setupMedal(tinMedal, viewModel.isTinBadgeSelected(), viewModel.getTinBadgeColor(), resources);
    setupMedal(bronzeMedal, viewModel.isBronzeBadgeSelected(), viewModel.getBronzeBadgeColor(),
        resources);
    setupMedal(silverMedal, viewModel.isSilverBadgeSelected(), viewModel.getSilverBadgeColor(),
        resources);
    setupMedal(goldMedal, viewModel.isGoldBadgeSelected(), viewModel.getGoldBadgeColor(),
        resources);
    setupMedal(platinumMedal, viewModel.isPlatinumBadgeSelected(),
        viewModel.getPlatinumBadgeColor(), resources);

    progress1.setBackgroundColor(resources.getColor(viewModel.getProgress1()));
    progress2.setBackgroundColor(resources.getColor(viewModel.getProgress2()));
    progress3.setBackgroundColor(resources.getColor(viewModel.getProgress3()));
    progress4.setBackgroundColor(resources.getColor(viewModel.getProgress4()));
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
      badge.getLayoutParams().width = (int) (badge.getLayoutParams().width * MEDAL_SCALE);
      badge.getLayoutParams().height = (int) (badge.getLayoutParams().height * MEDAL_SCALE);
      badge.setScaleType(ImageView.ScaleType.FIT_XY);
      badge.requestLayout();
    }
    Drawable drawable = badge.getDrawable();
    setDrawableColor(resources, badgeColor, drawable);
    badge.setImageDrawable(drawable);
    setBackground(badge, resources.getColor(R.color.white));
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
