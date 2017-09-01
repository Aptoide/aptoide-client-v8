package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.Data;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Splash class
 */

@SuppressWarnings("WeakerAccess") @Data public class Splash {
  private boolean enable;
  private String portrait;
  private String landscape;
  private int timeout;

  /**
   * @param enable defines if the splash screen is enables for this partner
   * @param portrait url for the portrait splash screen image
   * @param landscape url for the landscape splash screen image
   * @param timeout time to display the splash screen
   */
  public Splash(boolean enable, String portrait, String landscape, int timeout) {
    this.enable = enable;
    this.portrait = portrait;
    this.landscape = landscape;
    this.timeout = timeout;
  }
}