package cm.aptoide.pt.social.analytics;

import java.util.HashMap;

/**
 * Created by franciscocalado on 10/31/17.
 */

public class EventErrorHandler {

  public HashMap<String, Object> handleGenericErrorParsing(GenericErrorEvent errorEvent) {
    HashMap<String, Object> error = new HashMap<>();

    switch (errorEvent) {
      case LOGIN:
        error.put("message", "User not logged in");
        error.put("type", "LOGIN");
        break;
      case NO_STORE:
        error.put("message", "User has no store");
        error.put("type", "NO_STORE");
        break;
      case PRIVATE_USER:
        error.put("message", "User/Store set to private");
        error.put("type", "PRIVATE_USER");
        break;
    }

    return error;
  }

  public HashMap<String, Object> handleShareErrorParsing(ShareErrorEvent errorEvent) {
    HashMap<String, Object> error = new HashMap<>();

    switch (errorEvent) {
      case CANCELLED:
        error.put("message", "Share Action Cancelled");
        error.put("type", "CANCELLED");
        break;
      case UNKNOWN_ERROR:
        error.put("message", "Unknown Case 1");
        error.put("type", "UNK1");
        break;
    }

    return error;
  }

  public enum GenericErrorEvent {
    OK, LOGIN, NO_STORE, PRIVATE_USER
  }

  public enum ShareErrorEvent {
    OK, CANCELLED, UNKNOWN_ERROR
  }
}
