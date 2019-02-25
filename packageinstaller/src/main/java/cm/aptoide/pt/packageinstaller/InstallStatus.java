package cm.aptoide.pt.packageinstaller;

public final class InstallStatus {
  private final String message;
  private final Status status;

  InstallStatus(Status status, String message) {
    this.message = message;
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public Status getStatus() {
    return status;
  }

  public enum Status {
    SUCCESS, FAIL, CANCELED, UNKNOWN_ERROR
  }
}
