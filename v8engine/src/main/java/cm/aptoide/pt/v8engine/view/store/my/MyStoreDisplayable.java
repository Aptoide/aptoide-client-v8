package cm.aptoide.pt.v8engine.view.store.my;

import android.content.Context;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.view.View;
import cm.aptoide.pt.model.v7.store.GetHomeMeta;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import lombok.Getter;

/**
 * Created by trinkes on 05/12/2016.
 */

public class MyStoreDisplayable extends Displayable {
  @Getter private GetHomeMeta meta;
  private boolean isLongTime;

  public MyStoreDisplayable() {
  }

  public MyStoreDisplayable(GetHomeMeta meta) {
    this.meta = meta;
    Calendar aWeekBefore = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    aWeekBefore.add(Calendar.DAY_OF_MONTH, -Calendar.DAY_OF_WEEK);
    Date added = meta.getData().getStore().getAdded();
    isLongTime = added.before(aWeekBefore.getTime());
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.my_store_displayable_layout;
  }

  public String getSuggestionMessage(Context context) {
    if (isLongTime) {
      return getStoreDescription(context);
    } else {
      return context.getString(R.string.create_store_displayable_explore_message);
    }
  }

  private String getStoreDescription(Context context) {
    String message;
    if (TextUtils.isEmpty(meta.getData().getStore().getAppearance().getDescription())) {
      message = context.getString(R.string.create_store_displayable_empty_description_message);
    } else {
      message = meta.getData().getStore().getAppearance().getDescription();
    }
    return message;
  }

  public int getCreateStoreText() {
    if (isLongTime) {
      return R.string.create_store_displayable_created_store_long_term_message;
    } else {
      return R.string.create_store_displayable_created_store_short_term_message;
    }
  }

  public int getExploreButtonText() {
    if (isLongTime) {
      return R.string.create_store_displayable_explore_long_term_button;
    } else {
      return R.string.create_store_displayable_explore_button;
    }
  }

  public @Visibility int getCreateStoreTextViewVisibility() {
    return isLongTime ? View.GONE : View.VISIBLE;
  }

  public List<Store.SocialChannel> getSocialChannels() {
    return meta.getData().getStore().getSocialChannels() == null ? Collections.EMPTY_LIST
        : meta.getData().getStore().getSocialChannels();
  }

  @IntDef({ View.VISIBLE, View.INVISIBLE, View.GONE }) @Retention(RetentionPolicy.SOURCE)
  @interface Visibility {
  }
}
