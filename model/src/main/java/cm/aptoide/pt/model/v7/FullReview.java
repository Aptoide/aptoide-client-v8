/*
 * Copyright (c) 2016.
 * Modified on 03/08/2016.
 */

package cm.aptoide.pt.model.v7;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created on 02/08/16.
 */
@EqualsAndHashCode(callSuper = true) @Data public class FullReview extends Review {

  private AppData data;

  @Data public static class AppData {

    private GetAppMeta.App app;
  }
}
