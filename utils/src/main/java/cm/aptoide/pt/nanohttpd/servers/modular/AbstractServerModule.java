package cm.aptoide.pt.nanohttpd.servers.modular;

import fi.iki.elonen.NanoHTTPD;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 08-05-2017.
 */
public class AbstractServerModule implements ServerModule {

  private final List<ServerModule> servers = new LinkedList<>();

  public AbstractServerModule() {
  }

  public AbstractServerModule(ServerModule serverModule) {
    servers.add(serverModule);
  }

  private String preprocessUri(String uri) {
    if (uri.startsWith("/")) {
      uri = uri.substring(1, uri.length());
    }

    return uri;
  }

  @Override public boolean accepts(String uri) {
    uri = preprocessUri(uri);

    for (ServerModule server : servers) {
      if (server.accepts(uri)) {
        return true;
      }
    }

    return false;
  }

  @Override public NanoHTTPD.Response process(String uri) {
    uri = preprocessUri(uri);

    for (ServerModule server : servers) {
      if (server.accepts(uri)) {
        return server.process(uri);
      }
    }

    throw new IllegalArgumentException("Given uri not supported!");
  }

  @Override
  public ServerModule register(ServerModule serverModule) {
    servers.add(serverModule);
    return this;
  }
}
