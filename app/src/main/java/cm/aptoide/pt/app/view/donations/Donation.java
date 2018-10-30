package cm.aptoide.pt.app.view.donations;

/**
 * Created by franciscocalado on 11/10/2018.
 */

public class Donation {

  private String domain;
  private String owner;
  private float appc;

  public Donation(String domain, String owner, float appc) {
    this.domain = domain;
    this.owner = owner;
    this.appc = appc;
  }

  public float getAppc() {
    return appc;
  }

  public String getOwner() {
    return owner;
  }

  public String getDomain() {
    return domain;
  }
}
