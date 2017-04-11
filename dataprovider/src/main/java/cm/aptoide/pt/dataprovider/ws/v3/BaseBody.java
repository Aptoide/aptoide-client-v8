/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by neuro on 18-05-2016.
 */
public class BaseBody extends HashMapNotNull<String, Object> {

  @Getter @Setter private String aptoideMd5sum;
  @Getter @Setter private String aptoidePackage;

  public void setAccess_token(String token) {
    put("access_token", token);
  }
}
