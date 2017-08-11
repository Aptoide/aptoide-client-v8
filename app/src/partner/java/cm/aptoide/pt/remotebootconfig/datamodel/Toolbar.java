package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.Data;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Toolbar class
 */

@Data public class Toolbar {
  private Icon icon;

  /**
   * Toolbar constructor
   *
   * @param icon define toolbar icon configs
   */
  public Toolbar(Icon icon) {
    this.icon = icon;
  }
}