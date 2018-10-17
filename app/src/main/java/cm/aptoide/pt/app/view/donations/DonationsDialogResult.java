package cm.aptoide.pt.app.view.donations;

public class DonationsDialogResult {

  private String nickname;
  private float value;

  public DonationsDialogResult(String nickname, float value) {

    this.nickname = nickname;
    this.value = value;
  }

  public float getValue() {
    return value;
  }

  public String getNickname() {
    return nickname;
  }
}
