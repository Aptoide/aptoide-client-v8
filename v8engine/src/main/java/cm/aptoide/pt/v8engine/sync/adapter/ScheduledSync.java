/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.sync.adapter;

import android.content.SyncResult;

/**
 * Created by marcelobenites on 22/11/16.
 */
public abstract class ScheduledSync {

  public abstract void sync(SyncResult syncResult);

  protected void rescheduleSync(SyncResult syncResult) {
    syncResult.stats.numIoExceptions++;
  }
}
