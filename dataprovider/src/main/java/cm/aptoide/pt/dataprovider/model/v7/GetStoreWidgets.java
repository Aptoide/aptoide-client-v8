/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by neuro on 22-04-2016.
 */
@Data @Accessors(chain = true) @EqualsAndHashCode(callSuper = true) public class GetStoreWidgets
    extends BaseV7EndlessDataListResponse<GetStoreWidgets.WSWidget> {

  @Data public static class WSWidget {

    /**
     * Constants for values of type
     */
    public static final String ADS_TYPE = "ADS";
    public static final String APPS_GROUP_TYPE = "APPS_GROUP";
    public static final String CATEGORIES_TYPE = "DISPLAYS";
    public static final String TIMELINE_TYPE = "TIMELINE";
    public static final String REVIEWS_TYPE = "REVIEWS";
    public static final String COMMENTS_TYPE = "COMMENTS";
    public static final String STORE_GROUP = "STORES_GROUP";

    private Type type;
    private String title; // Highlighted, Games, Categories, Timeline, Recommended for you,
    private String tag;
    // Aptoide Publishers
    private String view;
    // Object that will hold view response.
    private Object viewObject;
    private List<Action> actions;
    private Data data;

    public boolean hasActions() {
      return (actions != null
          && actions.size() > 0
          && actions.get(0)
          .getEvent()
          .getName() != null);
    }

    @lombok.Data public static class Data {

      private Layout layout;
      private String icon;
      private String message;
      private List<Data.Categories> categories; //only present if type": "DISPLAYS"
      private Review.User user; //only on tabs (timeline)

      @lombok.Data public static class Categories {

        private long id;
        private String refId;
        private String parentId;
        private String parentRefId;
        private String name;
        private String graphic;
        private String icon;
        private int adsCount;
      }
    }

    @lombok.Data public static class Action {

      private String type; // button
      private String label;
      private String tag;
      private Event event;
    }
  }
}
