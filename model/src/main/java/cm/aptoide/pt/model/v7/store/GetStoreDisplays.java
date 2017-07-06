/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.model.v7.store;

import cm.aptoide.pt.model.v7.base.BaseV7Response;
import cm.aptoide.pt.model.v7.Event;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by neuro on 22-04-2016.
 */
@Data @EqualsAndHashCode(callSuper = true) public class GetStoreDisplays extends BaseV7Response {

  private List<EventImage> list;

  @Data public static class EventImage {

    private String label;
    private String graphic;
    private Event event;
  }
}
