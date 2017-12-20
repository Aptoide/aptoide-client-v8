package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created by pedroribeiro on 23/02/17.
 *
 * Twitter user information for followers extraction (this information is sent to userconnection)
 */

public class TwitterModel {

  private long id;
  private String token;
  private String secret;

  public TwitterModel() {
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getToken() {
    return this.token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getSecret() {
    return this.secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final long $id = this.getId();
    result = result * PRIME + (int) ($id >>> 32 ^ $id);
    final Object $token = this.getToken();
    result = result * PRIME + ($token == null ? 43 : $token.hashCode());
    final Object $secret = this.getSecret();
    result = result * PRIME + ($secret == null ? 43 : $secret.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof TwitterModel)) return false;
    final TwitterModel other = (TwitterModel) o;
    if (!other.canEqual(this)) return false;
    if (this.getId() != other.getId()) return false;
    final Object this$token = this.getToken();
    final Object other$token = other.getToken();
    if (this$token == null ? other$token != null : !this$token.equals(other$token)) return false;
    final Object this$secret = this.getSecret();
    final Object other$secret = other.getSecret();
    return this$secret == null ? other$secret == null : this$secret.equals(other$secret);
  }

  public String toString() {
    return "TwitterModel(id="
        + this.getId()
        + ", token="
        + this.getToken()
        + ", secret="
        + this.getSecret()
        + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof TwitterModel;
  }
}
