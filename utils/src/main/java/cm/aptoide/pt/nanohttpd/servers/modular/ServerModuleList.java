package cm.aptoide.pt.nanohttpd.servers.modular;

import fi.iki.elonen.NanoHTTPD;
import java.util.LinkedList;
import java.util.List;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

/**
 * Created by neuro on 08-05-2017.
 */
public class ServerModuleList extends MimeTypeServerModule {

  private final List<AbstractServerModule> abstractServerModules;

  public ServerModuleList(List<AbstractServerModule> abstractServerModules) {
    if (validateServerModules(abstractServerModules)) {
      this.abstractServerModules = new LinkedList<>(abstractServerModules);
    } else {
      throw new IllegalArgumentException(
          "More than one AbstractServerModule is registered for the same endpoint!");
    }
  }

  private boolean validateServerModules(List<AbstractServerModule> serverModules) {
    for (AbstractServerModule serverModule : serverModules) {
      for (AbstractServerModule innerServerModule : serverModules) {
        if (serverModule != innerServerModule && serverModule.getEndpoint()
            .equals(innerServerModule.getEndpoint())) {
          return false;
        }
      }
    }

    return true;
  }

  @Override public boolean accepts(NanoHTTPD.IHTTPSession session) {
    for (AbstractServerModule server : abstractServerModules) {
      if (server.accepts(session)) {
        return true;
      }
    }

    return false;
  }

  @Override public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
    for (AbstractServerModule server : abstractServerModules) {
      if (server.accepts(session)) {
        return server.serve(session);
      }
    }

    NanoHTTPD.Response serve = super.serve(session);
    return serve != null ? serve : newDefaultErrorResponse();
  }

  private NanoHTTPD.Response newDefaultErrorResponse() {
    return newFixedLengthResponse("Sorry, endpoint not implemented :)");
  }
}
