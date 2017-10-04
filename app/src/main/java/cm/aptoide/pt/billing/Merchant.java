package cm.aptoide.pt.billing;

public class Merchant {

  private final int id;
  private final String name;

  public Merchant(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
