package cm.aptoide.pt.nanohttpd.servers.modular.asset;

import android.content.res.AssetManager;
import cm.aptoide.pt.nanohttpd.servers.modular.AbstractServerModule;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by neuro on 08-05-2017.
 */

public abstract class AbstractAssetServer extends AbstractServerModule {

  private final AssetManager assetManager;

  public AbstractAssetServer(String accepts, AssetManager assetManager) {
    super(accepts);
    this.assetManager = assetManager;
  }

  // TODO: 23-05-2017 neuro add cache to this
  protected String loadTextAsset(String assetPath) {

    InputStream inputStream;
    String out;

    try {
      inputStream = assetManager.open(assetPath);

      byte[] buffer;
      buffer = new byte[inputStream.available()];
      int bytesRead = inputStream.read(buffer);
      out = new String(buffer);
    } catch (IOException e) {
      throw new IllegalArgumentException("Couldn't load asset! " + assetPath, e);
    }

    return out;
  }
}
