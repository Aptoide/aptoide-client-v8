/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v3;

import java.util.List;
import lombok.Data;

@Data public class BaseV3Response {

  private String status;
  private List<ErrorResponse> errors;

  public boolean isOk() {
    return status != null && status.equalsIgnoreCase("ok");
  }

  public boolean hasErrors() {
    return errors != null && !errors.isEmpty();
  }
}
