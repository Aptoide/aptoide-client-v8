package cm.aptoide.pt.home.apps;

public interface StateApp extends App {

  boolean isIndeterminate();

  void setIndeterminate(boolean indeterminate);

  Status getStatus();

  void setStatus(Status status);

  public enum Status {
    ACTIVE, STANDBY, COMPLETED, ERROR, UPDATE, UPDATING, PAUSING
  }
}
