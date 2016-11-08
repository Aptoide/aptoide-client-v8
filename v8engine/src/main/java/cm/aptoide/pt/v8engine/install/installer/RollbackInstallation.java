/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/09/2016.
 */

package cm.aptoide.pt.v8engine.install.installer;

import android.support.annotation.Nullable;

/**
 * Created by trinkes on 9/8/16.
 */
public interface RollbackInstallation extends Installation {

  String getAppName();

  String getIcon();

  String downloadLink();

  @Nullable String getAltDownloadLink();

  @Nullable String getMainObbName();

  @Nullable String getPatchObbPath();

  @Nullable String getPatchObbName();

  @Nullable String getMainObbPath();

  long getTimeStamp();
}
