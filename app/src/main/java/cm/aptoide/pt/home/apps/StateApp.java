package cm.aptoide.pt.home.apps;

public interface StateApp extends App {

  boolean isIndeterminate();

  Status getStatus();

  public enum Status {
    ACTIVE, STANDBY, COMPLETED, ERROR, UPDATE, UPDATING, PAUSING
  }
}
