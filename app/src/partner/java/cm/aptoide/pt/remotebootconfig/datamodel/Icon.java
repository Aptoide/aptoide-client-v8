package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.Data;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Icon class
 */

@Data public class Icon {
  private boolean enable;

  /**
   * Icon constructor
   *
   * @param enable defines if the ic_launcher icon should appear on the toolbar
   */
  public Icon(boolean enable) {
    this.enable = enable;
  }
}