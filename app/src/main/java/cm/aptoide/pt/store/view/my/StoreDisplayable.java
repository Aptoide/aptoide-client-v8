package cm.aptoide.pt.store.view.my;

import android.content.Context;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by trinkes on 05/12/2016.
 */

public class StoreDisplayable extends Displayable {
  private Store store;
  private boolean isLongTime;
  private StoreContext storeContext;
  private long firstStatsNumber;
  private long secondStatsNumber;
  private int firstStatsLabel;
  private int secondStatsLabel;
  private boolean statsClickable;
  private String message;

  public StoreDisplayable() {
  }

  public StoreDisplayable(Store store, StoreContext storeContext, long firstStatsNumber,
      long secondStatsNumber, int firstStatsLabelStringId, int secondStatsLabelStringId,
      boolean statsClickable, String message) {
    this.store = store;
    this.storeContext = storeContext;
    this.firstStatsNumber = firstStatsNumber;
    this.secondStatsNumber = secondStatsNumber;
    this.firstStatsLabel = firstStatsLabelStringId;
    this.secondStatsLabel = secondStatsLabelStringId;
    this.statsClickable = statsClickable;
    this.message = message;
    Calendar aWeekBefore = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    aWeekBefore.add(Calendar.DAY_OF_MONTH, -Calendar.DAY_OF_WEEK);
    Date added = store.getAdded();
    isLongTime = added.before(aWeekBefore.getTime());
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.store_displayable_layout;
  }

  public String getSuggestionMessage(Context context) {
    if (isLongTime) {
      return getStoreDescription();
    } else {
      return context.getString(R.string.create_store_displayable_explore_message);
    }
  }

  private String getStoreDescription() {
    return message;
  }

  public int getExploreButtonText() {
    if (isLongTime) {
      return R.string.create_store_displayable_explore_long_term_button;
    } else {
      return R.string.create_store_displayable_explore_button;
    }
  }

  long getFirstStatsNumber() {
    return firstStatsNumber;
  }

  long getSecondStatsNumber() {
    return secondStatsNumber;
  }

  int getFirstStatsLabel() {
    return firstStatsLabel;
  }

  int getSecondStatsLabel() {
    return secondStatsLabel;
  }

  public StoreContext getStoreContext() {
    return storeContext;
  }

  public Store getStore() {
    return this.store;
  }

  public boolean isStatsClickable() {
    return statsClickable;
  }
}
