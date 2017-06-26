/*
 * Copyright (c) 2016.
 * Modified on 03/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import lombok.Data;

/**
 * Created on 02/08/16.
 */
public class FullComment {
  //	private GetAppMeta.App data;

  private AppData data;

  @Data public static class AppData {

    private GetAppMeta.App app;
  }
}
