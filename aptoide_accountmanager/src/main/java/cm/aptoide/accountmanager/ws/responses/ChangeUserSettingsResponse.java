package cm.aptoide.accountmanager.ws.responses;

import java.util.List;
import lombok.Data;

/**
 * Created by trinkes on 5/6/16.
 */
@Data public class ChangeUserSettingsResponse {

  String status;

  List<ErrorResponse> errors;

  public String getStatus() {
    return status;
  }

  public List<ErrorResponse> getErrors() {
    return errors;
  }
}
