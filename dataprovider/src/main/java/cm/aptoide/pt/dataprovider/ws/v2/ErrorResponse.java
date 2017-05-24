/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v2;

import lombok.Data;

@Data public class ErrorResponse {

  private String error;
  private String errorDescription;
}
