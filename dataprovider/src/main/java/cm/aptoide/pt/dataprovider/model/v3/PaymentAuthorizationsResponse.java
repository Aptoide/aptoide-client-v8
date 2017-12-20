/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 15/11/2016.
 */

package cm.aptoide.pt.dataprovider.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PaymentAuthorizationsResponse extends BaseV3Response {

  @JsonProperty("authorizations") private List<PaymentAuthorizationResponse> authorizations;

  public PaymentAuthorizationsResponse() {
  }

  public List<PaymentAuthorizationResponse> getAuthorizations() {
    return this.authorizations;
  }

  public void setAuthorizations(List<PaymentAuthorizationResponse> authorizations) {
    this.authorizations = authorizations;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    final Object $authorizations = this.getAuthorizations();
    result = result * PRIME + ($authorizations == null ? 43 : $authorizations.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof PaymentAuthorizationsResponse)) return false;
    final PaymentAuthorizationsResponse other = (PaymentAuthorizationsResponse) o;
    if (!other.canEqual(this)) return false;
    if (!super.equals(o)) return false;
    final Object this$authorizations = this.getAuthorizations();
    final Object other$authorizations = other.getAuthorizations();
    return this$authorizations == null ? other$authorizations == null
        : this$authorizations.equals(other$authorizations);
  }

  public String toString() {
    return "PaymentAuthorizationsResponse(authorizations=" + this.getAuthorizations() + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof PaymentAuthorizationsResponse;
  }
}
