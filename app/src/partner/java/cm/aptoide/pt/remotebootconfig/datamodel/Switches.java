package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.Data;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Switches class
 */

@SuppressWarnings("WeakerAccess") @Data public class Switches {
  private Mature mature;
  private Options options;

  /**
   * @param mature mature options configs
   * @param options partner store options
   */
  public Switches(Mature mature, Options options) {
    this.mature = mature;
    this.options = options;
  }
}