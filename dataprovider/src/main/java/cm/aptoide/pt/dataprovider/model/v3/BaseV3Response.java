/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v3;

import java.util.List;

public class BaseV3Response {

  private String status;
  private List<ErrorResponse> errors;

  public BaseV3Response() {
  }

  public boolean isOk() {
    return status != null && status.equalsIgnoreCase("ok");
  }

  public boolean hasErrors() {
    return errors != null && !errors.isEmpty();
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<ErrorResponse> getErrors() {
    return this.errors;
  }

  public void setErrors(List<ErrorResponse> errors) {
    this.errors = errors;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $status = this.getStatus();
    result = result * PRIME + ($status == null ? 43 : $status.hashCode());
    final Object $errors = this.getErrors();
    result = result * PRIME + ($errors == null ? 43 : $errors.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof BaseV3Response)) return false;
    final BaseV3Response other = (BaseV3Response) o;
    if (!other.canEqual(this)) return false;
    final Object this$status = this.getStatus();
    final Object other$status = other.getStatus();
    if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
      return false;
    }
    final Object this$errors = this.getErrors();
    final Object other$errors = other.getErrors();
    return this$errors == null ? other$errors == null : this$errors.equals(other$errors);
  }

  public String toString() {
    return "BaseV3Response(status=" + this.getStatus() + ", errors=" + this.getErrors() + ")";
  }

  protected boolean canEqual(Object other) {
    return other instanceof BaseV3Response;
  }
}
