package cm.aptoide.pt.store.view.my;

import android.content.Context;
import android.text.TextUtils;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TargetStoreDisplayable extends Displayable {
  private Store store;
  private boolean isLongTime;
  private StoreContext storeContext;

  public TargetStoreDisplayable() {
  }

  public TargetStoreDisplayable(Store store, StoreContext storeContext) {
    this.store = store;
    this.storeContext = storeContext;
    Calendar aWeekBefore = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    aWeekBefore.add(Calendar.DAY_OF_MONTH, -Calendar.DAY_OF_WEEK);
    Date added = store.getAdded();
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
    if (TextUtils.isEmpty(store.getAppearance()
        .getDescription())) {
      message = context.getString(R.string.create_store_displayable_empty_description_message);
    } else {
      message = store.getAppearance()
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
    // TODO: 22/01/2019 no followers here
    return 15;
  }

  public long getFollowings() {
    // TODO: 22/01/2019 no followings here
    return 20;
  }

  public StoreContext getStoreContext() {
    return storeContext;
  }

  public Store getStore() {
    return store;
  }
}
