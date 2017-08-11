package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.Data;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Option Class
 */

@Data public class Options {
  private boolean shortcut;
  private Multistore multistore;
  private FirstInstall first_install;

  /**
   * Options constructor
   *
   * @param multistore defines the multistore configurations
   * @param first_install defines the first install configurations
   */
  public Options(Multistore multistore, FirstInstall first_install) {
    this.multistore = multistore;
    this.first_install = first_install;
  }
}