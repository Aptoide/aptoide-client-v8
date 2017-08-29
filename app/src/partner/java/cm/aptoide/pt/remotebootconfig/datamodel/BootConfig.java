package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.Data;

/**
 * Created by diogoloureiro on 17/01/2017.
 *
 * Boot Config Class
 */

@Data public class BootConfig {
  private Partner partner;

  /**
   * Boot Config constructor
   *
   * @param partner defines all the partner configs
   */
  public BootConfig(Partner partner) {
    this.partner = partner;
  }
}