package cm.aptoide.pt.nanohttpd.servers.modular;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by neuro on 08-05-2017.
 */
public abstract class AbstractServerModule implements ServerModule {

  private final String endpoint;

  public String getEndpoint() {
    return endpoint;
  }

  public AbstractServerModule(String endpoint) {
    this.endpoint = endpoint;
  }

  @Override public boolean accepts(NanoHTTPD.IHTTPSession session) {
    return endpoint.equals(session.getUri());
  }
}
