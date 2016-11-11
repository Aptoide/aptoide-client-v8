/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.model.v3.ErrorResponse;
import java.util.List;
import lombok.Data;

/**
 * Created by j-pac on 30-05-2014.
 */
@Data public class GenericResponseV3 {

  private String status;
  private List<ErrorResponse> errors;

  // Oauth api error
  private String error;
  private String errorDescription;
}
