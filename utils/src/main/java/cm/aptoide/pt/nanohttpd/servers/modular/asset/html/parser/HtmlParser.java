package cm.aptoide.pt.nanohttpd.servers.modular.asset.html.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neuro on 19-05-2017.
 */

public class HtmlParser {

  private final String htmlStr;
  private final String startToken;
  private final String endToken;
  private final HashMap<String, String> tokensMap;

  public HtmlParser(String htmlStr, String startToken, String endToken,
      HashMap<String, String> tokensMap) {
    this.htmlStr = htmlStr;
    this.startToken = startToken;
    this.endToken = endToken;
    this.tokensMap = tokensMap;
  }

  public String parse() {
    final List<String> tokens =
        new HtmlParserTokenExtractor(htmlStr, startToken, endToken).extractTokens();

    validateTokens(tokens);

    return parseHtml();
  }

  private String parseHtml() {
    String tmp = htmlStr;

    for (Map.Entry<String, String> entry : tokensMap.entrySet()) {
      tmp = tmp.replace(entry.getKey(), entry.getValue());
    }

    return tmp;
  }

  private void validateTokens(List<String> tokens) {
    for (String token : tokens) {
      if (!tokensMap.containsKey(token)) {
        throw new RuntimeException("Not all tokens are defined!");
      }
    }
  }
}
