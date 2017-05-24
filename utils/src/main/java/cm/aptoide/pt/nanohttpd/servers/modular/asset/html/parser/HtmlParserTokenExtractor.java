package cm.aptoide.pt.nanohttpd.servers.modular.asset.html.parser;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 19-05-2017.
 */

public class HtmlParserTokenExtractor {

  private final String htmlStr;
  private final String startToken;
  private final String endToken;

  public HtmlParserTokenExtractor(String htmlStr, String startToken, String endToken) {
    this.htmlStr = htmlStr;
    this.startToken = startToken;
    this.endToken = endToken;
  }

  public List<String> extractTokens() {
    List<String> htmlParserTokens = new LinkedList<>();

    int i = 0;
    while ((i = htmlStr.indexOf(startToken, (i + startToken.length()))) != -1) {
      htmlParserTokens.add(htmlStr.substring(i, htmlStr.indexOf(endToken, i) + endToken.length()));
    }

    return htmlParserTokens;
  }
}
