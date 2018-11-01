package cm.aptoide.pt.app.view.donations;

public class DonationsDialogResult {

  private String packageName;
  private String nickname;
  private float value;

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
