/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.networkclient.util.HashMapNotNull;

public class BaseBody extends HashMapNotNull<String, Object> {

  public void setAptoideMd5sum(String aptoideMd5sum) {
    put("aptoide_md5sum", aptoideMd5sum);
  }

  public void setAptoidePackage(String aptoidePackage) {
    put("aptoide_package", aptoidePackage);
  }

  public void setAccessToken(String accessToken) {
    put("access_token", accessToken);
  }

  public void setAptoideUid(String aptoideUid) {
    put("aptoide_uid", aptoideUid);
  }

  public void setQ(String q) {
    put("q", q);
  }
}
