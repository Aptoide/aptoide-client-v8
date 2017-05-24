package cm.aptoide.pt.nanohttpd.servers.modular.asset.html;

import cm.aptoide.pt.nanohttpd.servers.modular.asset.html.parser.HtmlParser;
import java.util.HashMap;

/**
 * Created by neuro on 08-05-2017.
 */
public class HtmlLocalizedAssetServer extends HtmlAssetServer {

  private final String startToken;
  private final String endToken;
  private final HashMap<String, String> tokensMap;

  public HtmlLocalizedAssetServer(String accepts, String htmlPath, String startToken,
      String endToken, HashMap<String, String> tokensMap) {
    super(accepts, htmlPath);
    this.startToken = startToken;
    this.endToken = endToken;
    this.tokensMap = tokensMap;
  }

  @Override protected String loadTextAsset(String assetPath) {
    String htmlString = super.loadTextAsset(assetPath);

    HtmlParser htmlParser = new HtmlParser(htmlString, startToken, endToken, tokensMap);

    return htmlParser.parse();
  }
}
