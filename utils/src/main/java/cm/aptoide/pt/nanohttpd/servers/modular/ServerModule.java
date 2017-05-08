package cm.aptoide.pt.nanohttpd.servers.modular;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by neuro on 08-05-2017.
 */
public interface ServerModule {

  boolean accepts(String uri);

  NanoHTTPD.Response process(String uri);

  ServerModule register(ServerModule serverModule);
}
