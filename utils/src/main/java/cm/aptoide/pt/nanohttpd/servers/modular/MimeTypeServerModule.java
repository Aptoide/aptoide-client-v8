package cm.aptoide.pt.nanohttpd.servers.modular;

import android.content.res.AssetManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.nanohttpd.MimeType;
import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by neuro on 17-05-2017.
 */
public class MimeTypeServerModule implements ServerModule {

  private static final String TAG = MimeTypeServerModule.class.getSimpleName();

  private final AssetManager assetManager;

  public MimeTypeServerModule(AssetManager assetManager) {
    this.assetManager = assetManager;
  }

  @Override public boolean accepts(NanoHTTPD.IHTTPSession session) {
    String uri = session.getUri();

    for (MimeType mimeType : MimeType.values()) {
      if (uri.endsWith("." + mimeType.getExtension())) {
        return true;
      }
    }

    return false;
  }

  @Override public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {

    String uri = session.getUri();
    InputStream mbuffer;

    Logger.d(TAG, "serve() called with: " + "uri = [" + uri + "]");

    if (uri != null) {
      for (MimeType mimeType : MimeType.values()) {
        if (uri.endsWith("." + mimeType.getExtension())) {
          mbuffer = getInputStream(uri.substring(1));
          return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, mimeType.getValue(),
              mbuffer);
        }
      }
    }

    return null;
  }

  // TODO: 17-05-2017 neuro remover dependencia do contexto
  private InputStream getInputStream(String assetPath) {

    try {
      return assetManager.open(assetPath);
    } catch (IOException e) {
      throw new IllegalArgumentException("Couldn't load asset! " + assetPath, e);
    }
  }
}
