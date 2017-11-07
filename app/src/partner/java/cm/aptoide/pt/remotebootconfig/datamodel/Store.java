package cm.aptoide.pt.remotebootconfig.datamodel;

/**
 * Created by diogoloureiro on 18/01/2017.
 *
 * Store class
 */

public class Store {
  private int id;
  private String name;
  private String label;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }
}