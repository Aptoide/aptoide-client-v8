package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created by pedroribeiro on 27/02/17.
 */

public class FacebookModel {

  private Long id;
  private String accessToken;

  public FacebookModel() {
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAccessToken() {
    return this.accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $id = this.getId();
    result = result * PRIME + ($id == null ? 43 : $id.hashCode());
    final Object $accessToken = this.getAccessToken();
    result = result * PRIME + ($accessToken == null ? 43 : $accessToken.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof FacebookModel)) return false;
    final FacebookModel other = (FacebookModel) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$id = this.getId();
    final Object other$id = other.getId();
    if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
    final Object this$accessToken = this.getAccessToken();
    final Object other$accessToken = other.getAccessToken();
    if (this$accessToken == null ? other$accessToken != null
        : !this$accessToken.equals(other$accessToken)) {
      return false;
    }
    return true;
  }

  public String toString() {
    return "FacebookModel(id=" + this.getId() + ", accessToken=" + this.getAccessToken() + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof FacebookModel;
  }
}
