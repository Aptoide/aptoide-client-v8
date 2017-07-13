package cm.aptoide.pt.v8engine.networking;

import java.io.IOException;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class BasicAuthenticator implements Authenticator {

  private final String username;
  private final String password;

  public BasicAuthenticator(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override public Request authenticate(Route route, Response response) throws IOException {
    return response.request()
        .newBuilder()
        .header("Authorization", Credentials.basic(username, password))
        .build();
  }
}
