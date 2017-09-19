package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.Data;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * First install class
 */

@SuppressWarnings("WeakerAccess") @Data public class FirstInstall {
  private boolean enable;

  /**
   * First Install constructor
   *
   * @param enable defines if the first install on first app launch is enabled for this partner
   */
  public FirstInstall(boolean enable) {
    this.enable = enable;
  }
}