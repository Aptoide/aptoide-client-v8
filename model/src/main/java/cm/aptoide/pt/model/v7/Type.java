/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 25/08/2016.
 */

package cm.aptoide.pt.model.v7;

import cm.aptoide.pt.utils.AptoideUtils;
import lombok.Getter;

/**
 * Created by neuro on 06-05-2016.
 */
public enum Type {
  _EMPTY(1, true), // FIXME for tests only

  // Server
  APPS_GROUP(3, false), STORES_GROUP(3, false), FOLLOW_STORE(3, false), DISPLAYS(2, true), ADS(3,
      false), HOME_META(1, true), COMMENTS_GROUP(1, true),

  //Reviews Screen
  REVIEWS_GROUP(1, false), //widget to create the user's store
  MY_STORE_META(1, true), MY_STORES_SUBSCRIBED(3, false), STORES_RECOMMENDED(3, false), MY_STORE(1,
      true), APP_META(1, true);

  @Getter private int defaultPerLineCount;
  @Getter private boolean fixedPerLineCount;

  Type(int defaultPerLineCount, boolean fixedPerLineCount) {
    this.defaultPerLineCount = defaultPerLineCount;
    this.fixedPerLineCount = fixedPerLineCount;
  }

  public int getPerLineCount() {
    int n = isFixedPerLineCount() ? getDefaultPerLineCount()
        : (int) (AptoideUtils.ScreenU.getScreenWidthInDip()
            / AptoideUtils.ScreenU.REFERENCE_WIDTH_DPI * getDefaultPerLineCount());
    return n > 0 ? n : 1;
  }
}
