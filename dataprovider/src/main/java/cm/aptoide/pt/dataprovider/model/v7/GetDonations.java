package cm.aptoide.pt.dataprovider.model.v7;

import java.util.List;

/**
 * Created by franciscocalado on 11/10/2018.
 */

public class GetDonations extends BaseV7Response {

  private String next;
  private List<Donor> items;

  public GetDonations() {
  }

  public String getNext() {
    return next;
  }

  public void setNext(String next) {
    this.next = next;
  }

  public List<Donor> getItems() {
    return items;
  }

  public void setItems(List<Donor> items) {
    this.items = items;
  }

  public static class Donor {
    private String domain;
    private String owner;
    private String appc;

    public Donor() {
    }

    public String getDomain() {
      return domain;
    }

    public void setDomain(String domain) {
      this.domain = domain;
    }

    public String getOwner() {
      return owner;
    }

    public void setOwner(String owner) {
      this.owner = owner;
    }

    public String getAppc() {
      return appc;
    }

    public void setAppc(String appc) {
      this.appc = appc;
    }
  }
}
