/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.model.v7.listapp;

/**
 * Created by neuro on 22-04-2016.
 */

import cm.aptoide.pt.model.v7.base.BaseV7Response;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true) public class ListAppsUpdates extends BaseV7Response {

  private List<App> list;

  /*
   * fixme
   * <p>this hack is to prevent updates from not being emited.</p>
   *
   * When a ListAppsUpdates request is made,
   * the request is broken down to 3 different listAppsUpdates (blocks of 50 apps) and
   * then the respective answers are being merged and shown to us as a result.
   * The requests info are not being merged into the final answer and
   * isOk is always returning false
   *
   */
  @Override public boolean isOk() {
    return (super.isOk() || list != null);
  }
}
