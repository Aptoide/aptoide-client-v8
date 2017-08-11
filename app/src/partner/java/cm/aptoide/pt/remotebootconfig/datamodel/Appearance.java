package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.Data;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Appearance Class
 */

@SuppressWarnings("WeakerAccess") @Data public class Appearance {
  private String theme;
  private Splash splash;
  private Toolbar toolbar;

  /**
   * Appearance constructor
   *
   * @param theme defines the partner theme to apply to the all store
   * @param splash defines the splash screen options
   * @param toolbar defines the toolbar options
   */
  public Appearance(String theme, Splash splash, Toolbar toolbar) {
    this.theme = theme;
    this.splash = splash;
    this.toolbar = toolbar;
  }
}