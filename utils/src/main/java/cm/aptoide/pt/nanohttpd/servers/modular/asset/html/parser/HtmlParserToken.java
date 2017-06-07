package cm.aptoide.pt.nanohttpd.servers.modular.asset.html.parser;

import android.support.annotation.StringRes;

/**
 * Created by neuro on 19-05-2017.
 */

public class HtmlParserToken {

  private final String token;
  @StringRes private final int string;

  public HtmlParserToken(String token, int string) {
    this.token = token;
    this.string = string;
  }
}
