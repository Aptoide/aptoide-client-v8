/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v2;

import java.util.List;

/**
 * Created by j-pac on 30-05-2014.
 */
public class GenericResponseV2 {

  String status;
  List<ErrorResponse> errors;
  boolean refresh;

  public boolean isRefresh() {
    return refresh;
  }

  public void setRefresh(boolean refresh) {
    this.refresh = refresh;
  }

  public String getStatus() {
    return status;
  }

  public List<ErrorResponse> getErrors() {
    return errors;
  }

  public boolean hasErrors() {
    return errors != null && !errors.isEmpty();
  }

  public boolean isOk() {
    return status != null && status.equalsIgnoreCase("ok");
  }
}
