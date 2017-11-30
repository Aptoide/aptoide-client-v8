package cm.aptoide.pt.store.view.my;

import android.content.Context;
import android.text.TextUtils;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.TimelineStats;
import cm.aptoide.pt.dataprovider.model.v7.store.GetHomeMeta;
import cm.aptoide.pt.dataprovider.ws.v7.MyStore;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import lombok.Getter;

/**
 * Created by trinkes on 05/12/2016.
 */

public class MyStoreDisplayable extends Displayable {
  @Getter private GetHomeMeta meta;
  private boolean isLongTime;
  private TimelineStats timelineStats;
  private StoreContext storeContext;

  public MyStoreDisplayable() {
  }

  public MyStoreDisplayable(MyStore myStore, StoreContext storeContext) {
    this.meta = myStore.getGetHomeMeta();
    timelineStats = myStore.getTimelineStats();
    this.storeContext = storeContext;
    Calendar aWeekBefore = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    aWeekBefore.add(Calendar.DAY_OF_MONTH, -Calendar.DAY_OF_WEEK);
    Date added = meta.getData()
        .getStore()
        .getAdded();
    isLongTime = added.before(aWeekBefore.getTime());
  }

  public TimelineStats getTimelineStats() {
    return timelineStats;
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
    if (TextUtils.isEmpty(meta.getData()
        .getStore()
        .getAppearance()
        .getDescription())) {
      message = context.getString(R.string.create_store_displayable_empty_description_message);
    } else {
      message = meta.getData()
          .getStore()
          .getAppearance()
          .getDescription();
    }
    return message;
  }

  public int getExploreButtonText() {
    if (isLongTime) {
      return R.string.create_store_displayable_explore_long_term_button;
    } else {
      return R.string.create_store_displayable_explore_button;
    }
  }

  public long getFollowers() {
    return timelineStats.getData()
        .getFollowers();
  }

  public long getFollowings() {
    return timelineStats.getData()
        .getFollowing();
  }

  public StoreContext getStoreContext() {
    return storeContext;
  }
}
