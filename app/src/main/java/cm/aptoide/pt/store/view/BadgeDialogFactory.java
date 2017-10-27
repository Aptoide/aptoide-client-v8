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
import android.widget.TextView;
import cm.aptoide.pt.R;

/**
 * Created by trinkes on 25/10/2017.
 */

public class BadgeDialogFactory {
  public static final float MEDAL_SCALE = 2f;
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

    @ColorRes int color = R.color.green_700;
    switch (badge) {
      case NONE:
        headerBackground.setBackgroundColor(resources.getColor(color));
        medalIcon.setImageDrawable(resources.getDrawable(R.drawable.tin));
        medalText.setText(R.string.badgedialog_title_bronze);
        congratulationsMessage.setText(R.string.badgedialog_message_bronze);
        uploadedAppsTv.setText(R.string.badgedialog_message_bronze_1);
        downloadsTv.setText(R.string.badgedialog_message_bronze_2);
        followersTv.setText(R.string.badgedialog_message_bronze_3);
        reviewsTv.setText(R.string.badgedialog_message_bronze_4);
        color = R.color.green_700;
        setDrawableColor(resources, color, uploadedAppsTv.getCompoundDrawables());
        setDrawableColor(resources, color, downloadsTv.getCompoundDrawables());
        setDrawableColor(resources, color, followersTv.getCompoundDrawables());
        setDrawableColor(resources, color, reviewsTv.getCompoundDrawables());
        tinMedal.getLayoutParams().width = (int) (tinMedal.getLayoutParams().width * MEDAL_SCALE);
        tinMedal.getLayoutParams().height = (int) (tinMedal.getLayoutParams().height * MEDAL_SCALE);
        tinMedal.setScaleType(ImageView.ScaleType.FIT_XY);
        tinMedal.requestLayout();
        break;
      case BRONZE:
        color = R.color.bronze_medal;
        headerBackground.setBackgroundColor(resources.getColor(color));
        medalIcon.setImageDrawable(resources.getDrawable(R.drawable.bronze));
        medalText.setText(R.string.badgedialog_title_bronze);
        congratulationsMessage.setText(R.string.badgedialog_message_bronze);
        uploadedAppsTv.setText(R.string.badgedialog_message_bronze_1);
        setDrawableColor(resources, color, uploadedAppsTv.getCompoundDrawables());
        setDrawableColor(resources, color, downloadsTv.getCompoundDrawables());
        setDrawableColor(resources, color, followersTv.getCompoundDrawables());
        setDrawableColor(resources, color, reviewsTv.getCompoundDrawables());
        downloadsTv.setText(R.string.badgedialog_message_bronze_2);
        followersTv.setText(R.string.badgedialog_message_bronze_3);
        reviewsTv.setText(R.string.badgedialog_message_bronze_4);
        bronzeMedal.getLayoutParams().width = (int) (bronzeMedal.getLayoutParams().width * MEDAL_SCALE);
        bronzeMedal.getLayoutParams().height = (int) (bronzeMedal.getLayoutParams().height * MEDAL_SCALE);
        bronzeMedal.setScaleType(ImageView.ScaleType.FIT_XY);
        bronzeMedal.requestLayout();
        break;
      case SILVER:
        color = R.color.silver_medal;
        headerBackground.setBackgroundColor(resources.getColor(color));
        medalIcon.setImageDrawable(resources.getDrawable(R.drawable.silver));
        medalText.setText(R.string.badgedialog_title_silver);
        congratulationsMessage.setText(R.string.badgedialog_message_silver);
        uploadedAppsTv.setText(R.string.badgedialog_message_silver_1);
        downloadsTv.setText(R.string.badgedialog_message_silver_2);
        followersTv.setText(R.string.badgedialog_message_silver_3);
        reviewsTv.setText(R.string.badgedialog_message_silver_4);
        setDrawableColor(resources, color, uploadedAppsTv.getCompoundDrawables());
        setDrawableColor(resources, color, downloadsTv.getCompoundDrawables());
        setDrawableColor(resources, color, followersTv.getCompoundDrawables());
        setDrawableColor(resources, color, reviewsTv.getCompoundDrawables());
        silverMedal.getLayoutParams().width = (int) (silverMedal.getLayoutParams().width * MEDAL_SCALE);
        silverMedal.getLayoutParams().height = (int) (silverMedal.getLayoutParams().height * MEDAL_SCALE);
        silverMedal.setScaleType(ImageView.ScaleType.FIT_XY);
        silverMedal.requestLayout();
        break;
      case GOLD:
        color = R.color.gold_medal;
        headerBackground.setBackgroundColor(resources.getColor(color));
        medalIcon.setImageDrawable(resources.getDrawable(R.drawable.gold));
        medalText.setText(R.string.badgedialog_title_gold);
        congratulationsMessage.setText(R.string.badgedialog_message_gold);
        uploadedAppsTv.setText(R.string.badgedialog_message_gold_1);
        downloadsTv.setText(R.string.badgedialog_message_gold_2);
        followersTv.setText(R.string.badgedialog_message_gold_3);
        reviewsTv.setText(R.string.badgedialog_message_gold_4);
        setDrawableColor(resources, color, uploadedAppsTv.getCompoundDrawables());
        setDrawableColor(resources, color, downloadsTv.getCompoundDrawables());
        setDrawableColor(resources, color, followersTv.getCompoundDrawables());
        setDrawableColor(resources, color, reviewsTv.getCompoundDrawables());
        goldMedal.getLayoutParams().width = (int) (goldMedal.getLayoutParams().width * MEDAL_SCALE);
        goldMedal.getLayoutParams().height = (int) (goldMedal.getLayoutParams().height * MEDAL_SCALE);
        goldMedal.setScaleType(ImageView.ScaleType.FIT_XY);
        goldMedal.requestLayout();
        break;
      case PLATINUM:
        color = R.color.platinum_medal;
        headerBackground.setBackgroundColor(resources.getColor(color));
        medalIcon.setImageDrawable(resources.getDrawable(R.drawable.platinum));
        medalText.setText(R.string.badgedialog_title_platinum);
        congratulationsMessage.setText(R.string.badgedialog_message_platinum);
        uploadedAppsTv.setText(R.string.badgedialog_message_platinum_1);
        downloadsTv.setText(R.string.badgedialog_message_platinum_2);
        followersTv.setText(R.string.badgedialog_message_platinum_3);
        reviewsTv.setText(R.string.badgedialog_message_platinum_4);
        setDrawableColor(resources, color, uploadedAppsTv.getCompoundDrawables());
        setDrawableColor(resources, color, downloadsTv.getCompoundDrawables());
        setDrawableColor(resources, color, followersTv.getCompoundDrawables());
        setDrawableColor(resources, color, reviewsTv.getCompoundDrawables());
        platinumMedal.getLayoutParams().width = (int) (platinumMedal.getLayoutParams().width * MEDAL_SCALE);
        platinumMedal.getLayoutParams().height = (int) (platinumMedal.getLayoutParams().height * MEDAL_SCALE);
        platinumMedal.setScaleType(ImageView.ScaleType.FIT_XY);
        platinumMedal.requestLayout();
        break;
    }

    Drawable drawable = tinMedal.getDrawable();
    setDrawableColor(resources, color, drawable);
    tinMedal.setImageDrawable(drawable);
    setBackground(tinMedal, resources.getColor(R.color.white));

    drawable = bronzeMedal.getDrawable();
    setDrawableColor(resources, color, drawable);
    bronzeMedal.setImageDrawable(drawable);
    setBackground(bronzeMedal, resources.getColor(R.color.white));

    drawable = silverMedal.getDrawable();
    setDrawableColor(resources, color, drawable);
    silverMedal.setImageDrawable(drawable);
    setBackground(silverMedal, resources.getColor(R.color.white));

    drawable = goldMedal.getDrawable();
    setDrawableColor(resources, color, drawable);
    goldMedal.setImageDrawable(drawable);
    setBackground(goldMedal, resources.getColor(R.color.white));

    drawable = platinumMedal.getDrawable();
    setDrawableColor(resources, color, drawable);
    platinumMedal.setImageDrawable(drawable);
    setBackground(platinumMedal, resources.getColor(R.color.white));
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
