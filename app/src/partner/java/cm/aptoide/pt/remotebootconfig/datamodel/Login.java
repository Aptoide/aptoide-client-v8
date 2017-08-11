package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.Data;

/**
 * Created by diogoloureiro on 01/06/2017.
 *
 * Login Class
 */

@Data public class Login {
  private boolean facebook;

  /**
   * @param facebook defines if the facebook login is available
   */
  public Login(boolean facebook) {
    this.facebook = facebook;
  }
}
