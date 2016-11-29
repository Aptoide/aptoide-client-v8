package cm.aptoide.pt.aptoidesdk;

import cm.aptoide.pt.aptoidesdk.ads.Aptoide;

/**
 * Created by neuro on 25-11-2016.
 */

public interface Ad {

  /**
   * The appId of this ad.
   * <br><br><b>Attention!</b><br>
   * <b>Do not use in conjunction with getApp, use {@link Aptoide#getApp(Ad)} instead.</b>
   */
  long getAppId();

  /**
   * The package name of this ad.
   */
  String getPackageName();

  /**
   * The name of this ad.
   */
  String getName();

  /**
   * The path ot the icon of this ad.
   */
  String getIconPath();

  /**
   * The size of this ad.
   */
  long getSize();

  /**
   * The version code of this ad.
   */
  int getVercode();

  /**
   * The version name of this ad.
   */
  String getVername();

  /**
   * The description of this ad.
   */
  String getDescription();
}
