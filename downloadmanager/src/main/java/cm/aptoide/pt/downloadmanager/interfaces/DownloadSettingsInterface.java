package cm.aptoide.pt.downloadmanager.interfaces;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

/**
 * Created by trinkes on 6/28/16.
 */
public interface DownloadSettingsInterface {

  @DrawableRes int getMainIcon();

  @DrawableRes int getButton1Icon();

  String getButton1Text(Context context);

  /**
   * gets the cache size that should be used
   *
   * @return cache size in mb
   */
  long getMaxCacheSize();

  @NonNull String getDownloadDir();

  String getObbDir();
}
