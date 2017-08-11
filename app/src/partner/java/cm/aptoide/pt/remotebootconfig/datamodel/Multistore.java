package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.Data;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Multistore class
 */

@SuppressWarnings("WeakerAccess") @Data public class Multistore {
  private boolean enable;
  private boolean search;

  /**
   * @param enable allows the user to view other Store views
   * @param search allows users to search apps on other Stores
   */
  public Multistore(boolean enable, boolean search) {
    this.enable = enable;
    this.search = search;
  }
}