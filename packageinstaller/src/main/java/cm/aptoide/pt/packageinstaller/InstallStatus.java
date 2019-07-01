package cm.aptoide.pt.packageinstaller;

public final class InstallStatus {
  private final String message;
  private final Status status;
  private final String packageName;

  InstallStatus(Status status, String message, String packageName) {
    this.message = message;
    this.status = status;
    this.packageName = packageName;
  }

  public String getMessage() {
    return message;
  }

  public Status getStatus() {
    return status;
  }

  public String getPackageName() {
    return packageName;
  }

  public enum Status {
    INSTALLING, SUCCESS, FAIL, CANCELED, UNKNOWN_ERROR
  }
}
