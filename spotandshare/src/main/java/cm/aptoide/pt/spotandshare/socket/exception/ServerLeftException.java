package cm.aptoide.pt.spotandshare.socket.exception;

import java.io.IOException;

/**
 * Created by neuro on 21-03-2017.
 */

public class ServerLeftException extends IOException {

  public ServerLeftException() {
  }

  public ServerLeftException(String message) {
    super(message);
  }

  public ServerLeftException(String message, Throwable cause) {
    super(message, cause);
  }

  public ServerLeftException(Throwable cause) {
    super(cause);
  }
}
