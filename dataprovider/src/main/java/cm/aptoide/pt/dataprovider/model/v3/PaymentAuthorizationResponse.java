package cm.aptoide.pt.dataprovider.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentAuthorizationResponse extends BaseV3Response {

  @JsonProperty("paymentTypeId") private int paymentId;
  @JsonProperty("url") private String url;
  @JsonProperty("successUrl") private String successUrl;
  @JsonProperty("authorizationStatus") private String authorizationStatus;

  public PaymentAuthorizationResponse() {
  }

  public int getPaymentId() {
    return this.paymentId;
  }

  public void setPaymentId(int paymentId) {
    this.paymentId = paymentId;
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getSuccessUrl() {
    return this.successUrl;
  }

  public void setSuccessUrl(String successUrl) {
    this.successUrl = successUrl;
  }

  public String getAuthorizationStatus() {
    return this.authorizationStatus;
  }

  public void setAuthorizationStatus(String authorizationStatus) {
    this.authorizationStatus = authorizationStatus;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    result = result * PRIME + this.getPaymentId();
    final Object $url = this.getUrl();
    result = result * PRIME + ($url == null ? 43 : $url.hashCode());
    final Object $successUrl = this.getSuccessUrl();
    result = result * PRIME + ($successUrl == null ? 43 : $successUrl.hashCode());
    final Object $authorizationStatus = this.getAuthorizationStatus();
    result = result * PRIME + ($authorizationStatus == null ? 43 : $authorizationStatus.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof PaymentAuthorizationResponse)) return false;
    final PaymentAuthorizationResponse other = (PaymentAuthorizationResponse) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    if (this.getPaymentId() != other.getPaymentId()) return false;
    final Object this$url = this.getUrl();
    final Object other$url = other.getUrl();
    if (this$url == null ? other$url != null : !this$url.equals(other$url)) return false;
    final Object this$successUrl = this.getSuccessUrl();
    final Object other$successUrl = other.getSuccessUrl();
    if (this$successUrl == null ? other$successUrl != null
        : !this$successUrl.equals(other$successUrl)) {
      return false;
    }
    final Object this$authorizationStatus = this.getAuthorizationStatus();
    final Object other$authorizationStatus = other.getAuthorizationStatus();
    if (this$authorizationStatus == null ? other$authorizationStatus != null
        : !this$authorizationStatus.equals(other$authorizationStatus)) {
      return false;
    }
    return true;
  }

  public String toString() {
    return "PaymentAuthorizationResponse(paymentId="
        + this.getPaymentId()
        + ", url="
        + this.getUrl()
        + ", successUrl="
        + this.getSuccessUrl()
        + ", authorizationStatus="
        + this.getAuthorizationStatus()
        + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof PaymentAuthorizationResponse;
  }
}
