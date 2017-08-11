package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.Data;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Store class
 */

@Data public class Store {
  private int id;
  private String name;
  private String label;

  /**
   * @param id partner store id
   * @param name partner store name, aka default store name
   * @param label partner label name
   */
  public Store(int id, String name, String label) {
    this.id = id;
    this.name = name;
    this.label = label;
  }
}