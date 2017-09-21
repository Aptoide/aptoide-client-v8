package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.Data;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Mature Class
 */

@SuppressWarnings("WeakerAccess") @Data public class Mature {
  private boolean enable;
  private boolean value;

  /**
   * Mature constructor
   *
   * @param enable defines if the mature option is available to the AppStore
   * @param value defines the default value for the mature switch
   */
  public Mature(boolean enable, boolean value) {
    this.enable = enable;
    this.value = value;
  }
}