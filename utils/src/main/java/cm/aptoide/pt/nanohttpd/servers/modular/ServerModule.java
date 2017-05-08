package cm.aptoide.pt.nanohttpd.servers.modular;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by neuro on 08-05-2017.
 */
public interface ServerModule {

  boolean accepts(NanoHTTPD.IHTTPSession session);

  NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session);
}
