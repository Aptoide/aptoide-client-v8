package cm.aptoide.pt.app.view.donations.model;

public class DonationsDialogResult {

  private final String packageName;
  private final String nickname;
  private final float value;

  public DonationsDialogResult(String packageName, String nickname, float value) {
    this.packageName = packageName;
    this.nickname = nickname;
    this.value = value;
  }

  public float getValue() {
    return value;
  }

  public String getNickname() {
    return nickname;
  }

  public String getPackageName() {
    return packageName;
  }
}
