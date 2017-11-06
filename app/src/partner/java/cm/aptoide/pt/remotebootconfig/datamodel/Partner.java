package cm.aptoide.pt.remotebootconfig.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Partner Class
 */

public class Partner {
  @JsonProperty("config_id") private int configId;
  private String uid;
  private String type;
  private Feedback feedback;
  private Store store;
  private Appearance appearance;
  private Switches switches;
  private Social social;

  public int getConfigId() {
    return configId;
  }

  public void setConfigId(int configId) {
    this.configId = configId;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Feedback getFeedback() {
    return feedback;
  }

  public void setFeedback(Feedback feedback) {
    this.feedback = feedback;
  }

  public Store getStore() {
    return store;
  }

  public void setStore(Store store) {
    this.store = store;
  }

  public Appearance getAppearance() {
    return appearance;
  }

  public void setAppearance(Appearance appearance) {
    this.appearance = appearance;
  }

  public Switches getSwitches() {
    return switches;
  }

  public void setSwitches(Switches switches) {
    this.switches = switches;
  }

  public Social getSocial() {
    return social;
  }

  public void setSocial(Social social) {
    this.social = social;
  }
}