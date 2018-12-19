package cm.aptoide.pt.promotions;

public class ClaimDialogResultWrapper {

  private String packageName;
  private boolean status;

  public ClaimDialogResultWrapper(String packageName, boolean status) {
    this.packageName = packageName;
    this.status = status;
  }

  public String getPackageName() {
    return packageName;
  }

  public boolean isOk() {
    return status && !packageName.equals("");
  }
}
