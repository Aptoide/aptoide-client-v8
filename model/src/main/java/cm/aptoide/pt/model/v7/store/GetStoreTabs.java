/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 10/05/2016.
 */

package cm.aptoide.pt.model.v7.store;

import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.Event;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by hsousa on 17/09/15.
 */
@Data @EqualsAndHashCode(callSuper = true) public class GetStoreTabs extends BaseV7Response {

  private List<Tab> list;

  @Data public static class Tab {

    private String label;
    private String tag;
    private Event event;
  }
}
