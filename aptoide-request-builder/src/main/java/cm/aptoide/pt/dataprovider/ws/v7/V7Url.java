/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.utils.AptoideUtils;
import java.util.regex.Matcher;

/**
 * Created by neuro on 23-05-2016.
 */
public class V7Url {

  private String url;

  public V7Url(String url) {
    this.url = url;
  }

  public String get() {
    return url;
  }

  public String getStoreName() {
    Matcher matcher = AptoideUtils.RegexU.getStoreNameFromGetUrlPattern()
        .matcher(url);
    if (matcher.find()) {
      return matcher.group(1);
    }

    return null;
  }

  public Long getStoreId() {
    Matcher matcher = AptoideUtils.RegexU.getStoreIdFromGetUrlPattern()
        .matcher(url);
    if (matcher.find()) {
      return Long.parseLong(matcher.group(1));
    }

    return null;
  }

  public V7Url remove(String toRemove) {
    this.url = url.replace(toRemove, "");
    return this;
  }

  public boolean containsLimit() {
    return url.contains("limit=");
  }
}
