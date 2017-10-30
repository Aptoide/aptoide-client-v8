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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
    fillView(view, badge);
    Dialog dialog = new AlertDialog.Builder(context).setView(view)
        .create();
    return dialog;
  }

  private void fillView(View view, GridStoreMetaWidget.HomeMeta.Badge badge) {
    Resources resources = view.getContext()
        .getResources();
    StoreMedalPopupViewHolder holder = new StoreMedalPopupViewHolder(view);

    @ColorRes int color = R.color.tin_medal;
    switch (badge) {
      case NONE:
        holder.getHeaderBackground()
            .setBackgroundColor(resources.getColor(color));
        holder.getMedalIcon()
            .setImageDrawable(resources.getDrawable(R.drawable.tin));
        holder.getMedalText()
            .setText(R.string.badgedialog_title_bronze);
        holder.getCongratulationsMessage()
            .setText(R.string.badgedialog_message_bronze);
        holder.getUploadedAppsTv()
            .setText(R.string.badgedialog_message_bronze_1);
        holder.getDownloadsTv()
            .setText(R.string.badgedialog_message_bronze_2);
        holder.getFollowersTv()
            .setText(R.string.badgedialog_message_bronze_3);
        holder.getReviewsTv()
            .setText(R.string.badgedialog_message_bronze_4);
        setDrawableColor(resources, color, holder.getUploadedAppsTv()
            .getCompoundDrawables());
        setDrawableColor(resources, color, holder.getDownloadsTv()
            .getCompoundDrawables());
        setDrawableColor(resources, color, holder.getFollowersTv()
            .getCompoundDrawables());
        setDrawableColor(resources, color, holder.getReviewsTv()
            .getCompoundDrawables());
        holder.getTinMedal()
            .getLayoutParams().width = (int) (holder.getTinMedal()
            .getLayoutParams().width * MEDAL_SCALE);
        holder.getTinMedal()
            .getLayoutParams().height = (int) (holder.getTinMedal()
            .getLayoutParams().height * MEDAL_SCALE);
        holder.getTinMedal()
            .setScaleType(ImageView.ScaleType.FIT_XY);
        holder.getTinMedal()
            .requestLayout();
        break;
      case BRONZE:
        color = R.color.bronze_medal;
        holder.getHeaderBackground()
            .setBackgroundColor(resources.getColor(color));
        holder.getMedalIcon()
            .setImageDrawable(resources.getDrawable(R.drawable.bronze));
        holder.getMedalText()
            .setText(R.string.badgedialog_title_bronze);
        holder.getCongratulationsMessage()
            .setText(R.string.badgedialog_message_bronze);
        holder.getUploadedAppsTv()
            .setText(R.string.badgedialog_message_bronze_1);
        setDrawableColor(resources, color, holder.getUploadedAppsTv()
            .getCompoundDrawables());
        setDrawableColor(resources, color, holder.getDownloadsTv()
            .getCompoundDrawables());
        setDrawableColor(resources, color, holder.getFollowersTv()
            .getCompoundDrawables());
        setDrawableColor(resources, color, holder.getReviewsTv()
            .getCompoundDrawables());
        holder.getDownloadsTv()
            .setText(R.string.badgedialog_message_bronze_2);
        holder.getFollowersTv()
            .setText(R.string.badgedialog_message_bronze_3);
        holder.getReviewsTv()
            .setText(R.string.badgedialog_message_bronze_4);
        holder.getBronzeMedal()
            .getLayoutParams().width = (int) (holder.getBronzeMedal()
            .getLayoutParams().width * MEDAL_SCALE);
        holder.getBronzeMedal()
            .getLayoutParams().height = (int) (holder.getBronzeMedal()
            .getLayoutParams().height * MEDAL_SCALE);
        holder.getBronzeMedal()
            .setScaleType(ImageView.ScaleType.FIT_XY);
        holder.getBronzeMedal()
            .requestLayout();
        break;
      case SILVER:
        color = R.color.silver_medal;
        holder.getHeaderBackground()
            .setBackgroundColor(resources.getColor(color));
        holder.getMedalIcon()
            .setImageDrawable(resources.getDrawable(R.drawable.silver));
        holder.getMedalText()
            .setText(R.string.badgedialog_title_silver);
        holder.getCongratulationsMessage()
            .setText(R.string.badgedialog_message_silver);
        holder.getUploadedAppsTv()
            .setText(R.string.badgedialog_message_silver_1);
        holder.getDownloadsTv()
            .setText(R.string.badgedialog_message_silver_2);
        holder.getFollowersTv()
            .setText(R.string.badgedialog_message_silver_3);
        holder.getReviewsTv()
            .setText(R.string.badgedialog_message_silver_4);
        setDrawableColor(resources, color, holder.getUploadedAppsTv()
            .getCompoundDrawables());
        setDrawableColor(resources, color, holder.getDownloadsTv()
            .getCompoundDrawables());
        setDrawableColor(resources, color, holder.getFollowersTv()
            .getCompoundDrawables());
        setDrawableColor(resources, color, holder.getReviewsTv()
            .getCompoundDrawables());
        holder.getSilverMedal()
            .getLayoutParams().width = (int) (holder.getSilverMedal()
            .getLayoutParams().width * MEDAL_SCALE);
        holder.getSilverMedal()
            .getLayoutParams().height = (int) (holder.getSilverMedal()
            .getLayoutParams().height * MEDAL_SCALE);
        holder.getSilverMedal()
            .setScaleType(ImageView.ScaleType.FIT_XY);
        holder.getSilverMedal()
            .requestLayout();
        break;
      case GOLD:
        color = R.color.gold_medal;
        holder.getHeaderBackground()
            .setBackgroundColor(resources.getColor(color));
        holder.getMedalIcon()
            .setImageDrawable(resources.getDrawable(R.drawable.gold));
        holder.getMedalText()
            .setText(R.string.badgedialog_title_gold);
        holder.getCongratulationsMessage()
            .setText(R.string.badgedialog_message_gold);
        holder.getUploadedAppsTv()
            .setText(R.string.badgedialog_message_gold_1);
        holder.getDownloadsTv()
            .setText(R.string.badgedialog_message_gold_2);
        holder.getFollowersTv()
            .setText(R.string.badgedialog_message_gold_3);
        holder.getReviewsTv()
            .setText(R.string.badgedialog_message_gold_4);
        setDrawableColor(resources, color, holder.getUploadedAppsTv()
            .getCompoundDrawables());
        setDrawableColor(resources, color, holder.getDownloadsTv()
            .getCompoundDrawables());
        setDrawableColor(resources, color, holder.getFollowersTv()
            .getCompoundDrawables());
        setDrawableColor(resources, color, holder.getReviewsTv()
            .getCompoundDrawables());
        holder.getGoldMedal()
            .getLayoutParams().width = (int) (holder.getGoldMedal()
            .getLayoutParams().width * MEDAL_SCALE);
        holder.getGoldMedal()
            .getLayoutParams().height = (int) (holder.getGoldMedal()
            .getLayoutParams().height * MEDAL_SCALE);
        holder.getGoldMedal()
            .setScaleType(ImageView.ScaleType.FIT_XY);
        holder.getGoldMedal()
            .requestLayout();
        break;
      case PLATINUM:
        color = R.color.platinum_medal;
        holder.getHeaderBackground()
            .setBackgroundColor(resources.getColor(color));
        holder.getMedalIcon()
            .setImageDrawable(resources.getDrawable(R.drawable.platinum));
        holder.getMedalText()
            .setText(R.string.badgedialog_title_platinum);
        holder.getCongratulationsMessage()
            .setText(R.string.badgedialog_message_platinum);
        holder.getUploadedAppsTv()
            .setText(R.string.badgedialog_message_platinum_1);
        holder.getDownloadsTv()
            .setText(R.string.badgedialog_message_platinum_2);
        holder.getFollowersTv()
            .setText(R.string.badgedialog_message_platinum_3);
        holder.getReviewsTv()
            .setText(R.string.badgedialog_message_platinum_4);
        setDrawableColor(resources, color, holder.getUploadedAppsTv()
            .getCompoundDrawables());
        setDrawableColor(resources, color, holder.getDownloadsTv()
            .getCompoundDrawables());
        setDrawableColor(resources, color, holder.getFollowersTv()
            .getCompoundDrawables());
        setDrawableColor(resources, color, holder.getReviewsTv()
            .getCompoundDrawables());
        holder.getPlatinumMedal()
            .getLayoutParams().width = (int) (holder.getPlatinumMedal()
            .getLayoutParams().width * MEDAL_SCALE);
        holder.getPlatinumMedal()
            .getLayoutParams().height = (int) (holder.getPlatinumMedal()
            .getLayoutParams().height * MEDAL_SCALE);
        holder.getPlatinumMedal()
            .setScaleType(ImageView.ScaleType.FIT_XY);
        holder.getPlatinumMedal()
            .requestLayout();
        break;
    }

    int badgeRank = badge.ordinal();

    Drawable drawable = holder.getTinMedal()
        .getDrawable();
    if (badgeRank >= 0) {
      setDrawableColor(resources, color, drawable);
    } else {
      setDrawableColor(resources, R.color.grey_fog_light, drawable);
    }
    holder.getTinMedal()
        .setImageDrawable(drawable);
    setBackground(holder.getTinMedal(), resources.getColor(R.color.white));

    drawable = holder.getBronzeMedal()
        .getDrawable();
    if (badgeRank >= 1) {
      setDrawableColor(resources, color, drawable);
    } else {
      setDrawableColor(resources, R.color.grey_fog_light, drawable);
    }
    holder.getBronzeMedal()
        .setImageDrawable(drawable);
    setBackground(holder.getBronzeMedal(), resources.getColor(R.color.white));

    drawable = holder.getSilverMedal()
        .getDrawable();
    if (badgeRank >= 2) {
      setDrawableColor(resources, color, drawable);
    } else {
      setDrawableColor(resources, R.color.grey_fog_light, drawable);
    }
    holder.getSilverMedal()
        .setImageDrawable(drawable);
    setBackground(holder.getSilverMedal(), resources.getColor(R.color.white));

    drawable = holder.getGoldMedal()
        .getDrawable();
    if (badgeRank >= 3) {
      setDrawableColor(resources, color, drawable);
    } else {
      setDrawableColor(resources, R.color.grey_fog_light, drawable);
    }
    holder.getGoldMedal()
        .setImageDrawable(drawable);
    setBackground(holder.getGoldMedal(), resources.getColor(R.color.white));

    drawable = holder.getPlatinumMedal()
        .getDrawable();
    if (badgeRank >= 4) {
      setDrawableColor(resources, color, drawable);
    } else {
      setDrawableColor(resources, R.color.grey_fog_light, drawable);
    }
    holder.getPlatinumMedal()
        .setImageDrawable(drawable);
    setBackground(holder.getPlatinumMedal(), resources.getColor(R.color.white));

    if (badgeRank >= 1) {
      holder.getProgress1()
          .setBackgroundColor(resources.getColor(color));
    }
    if (badgeRank >= 2) {
      holder.getProgress2()
          .setBackgroundColor(resources.getColor(color));
    }
    if (badgeRank >= 3) {
      holder.getProgress3()
          .setBackgroundColor(resources.getColor(color));
    }
    if (badgeRank >= 4) {
      holder.getProgress4()
          .setBackgroundColor(resources.getColor(color));
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
