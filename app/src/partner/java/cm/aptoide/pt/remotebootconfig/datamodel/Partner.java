package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.Data;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Partner Class
 */

@SuppressWarnings("WeakerAccess") @Data public class Partner {
  private int config_id;
  private String uid;
  private String type;
  private Feedback feedback;
  private Store store;
  private Appearance appearance;
  private Switches switches;
  private Social social;

  /**
   * Partner constructor
   *
   * @param config_id server configuration id
   * @param uid defines the unique identifier of the partner, aka OEM ID
   * @param type defines the vertical type of the partner
   * @param feedback defines the feedback configs
   * @param store defines the store configs
   * @param appearance defines the appearance configs
   * @param switches defines the switchable configs
   * @param social defines the social components configs
   */
  public Partner(int config_id, String uid, String type, Feedback feedback, Store store,
      Appearance appearance, Switches switches, Social social) {
    this.config_id = config_id;
    this.uid = uid;
    this.type = type;
    this.feedback = feedback;
    this.store = store;
    this.appearance = appearance;
    this.switches = switches;
    this.social = social;
  }
}