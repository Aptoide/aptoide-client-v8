package cm.aptoide.pt.nanohttpd.servers.modular;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by neuro on 08-05-2017.
 */
public abstract class AbstractServerModule implements ServerModule {

  private final String accepts;

  public AbstractServerModule(String accepts) {
    this.accepts = accepts;
  }

  @Override public boolean accepts(NanoHTTPD.IHTTPSession session) {
    return accepts.equals(session.getUri());
  }
}
