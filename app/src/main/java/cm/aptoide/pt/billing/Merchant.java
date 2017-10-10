package cm.aptoide.pt.billing;

public class Merchant {

  private final long id;
  private final String name;

  public Merchant(long id, String name) {
    this.id = id;
    this.name = name;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
