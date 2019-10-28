package cm.aptoide.pt.home.apps.model;

import cm.aptoide.pt.home.apps.App;

public interface StateApp extends App {

  boolean isIndeterminate();

  void setIndeterminate(boolean indeterminate);

  Status getStatus();

  void setStatus(Status status);

  int getProgress();

  enum Status {
    ACTIVE, STANDBY, COMPLETED, ERROR, UPDATE, UPDATING, PAUSING, INSTALLING
  }
}
