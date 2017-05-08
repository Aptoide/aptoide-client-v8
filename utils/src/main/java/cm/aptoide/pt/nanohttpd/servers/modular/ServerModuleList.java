package cm.aptoide.pt.nanohttpd.servers.modular;

import fi.iki.elonen.NanoHTTPD;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 08-05-2017.
 */
public class ServerModuleList implements ServerModule {

  private final List<ServerModule> serverModules;

  public ServerModuleList(List<ServerModule> serverModules) {
    this.serverModules = new LinkedList<>(serverModules);
  }

  @Override public boolean accepts(NanoHTTPD.IHTTPSession session) {
    for (ServerModule server : serverModules) {
      if (server.accepts(session)) {
        return true;
      }
    }

    return false;
  }

  @Override public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
    for (ServerModule server : serverModules) {
      if (server.accepts(session)) {
        return server.serve(session);
      }
    }

    throw new IllegalArgumentException("Given uri not supported!");
  }

  public ServerModule register(ServerModule serverModule) {
    serverModules.add(serverModule);
    return this;
  }
}
