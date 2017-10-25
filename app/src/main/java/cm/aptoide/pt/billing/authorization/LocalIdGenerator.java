package cm.aptoide.pt.billing.authorization;

public class LocalIdGenerator {

  public String generate() {
    return "-1";
  }

  public boolean isLocal(String id) {
    return id.equals("-1");
  }
}
