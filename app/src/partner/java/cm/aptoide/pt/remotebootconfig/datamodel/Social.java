package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.Data;

/**
 * Created by diogoloureiro on 01/06/2017.
 *
 * Social class
 */

@Data public class Social {
  private Login login;

  /**
   * Social constructor
   *
   * @param login defines social login configurations
   */
  public Social(Login login) {
    this.login = login;
  }
}
