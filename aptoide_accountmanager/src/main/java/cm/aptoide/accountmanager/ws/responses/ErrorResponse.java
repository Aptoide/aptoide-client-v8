/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.accountmanager.ws.responses;

import lombok.Data;

/**
 * Created by rmateus on 03-01-2014.
 * <p>
 * This class was simply called "Error" in v6
 */
@Data public class ErrorResponse {

  public String code;
  public String msg;
}
