/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import java.util.List;

/**
 * Created by neuro on 22-04-2016.
 */
public class GetStoreWidgets extends BaseV7EndlessDataListResponse<GetStoreWidgets.WSWidget> {

  public GetStoreWidgets() {
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof GetStoreWidgets;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof GetStoreWidgets)) return false;
    final GetStoreWidgets other = (GetStoreWidgets) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    return true;
  }

  public String toString() {
    return "GetStoreWidgets()";
  }

  public static class WSWidget {

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

    private cm.aptoide.pt.dataprovider.model.v7.Type type;
    private String title; // Highlighted, Games, Categories, Timeline, Recommended for you,
    private String tag;
    // Aptoide Publishers
    private String view;
    // Object that will hold view response.
    private Object viewObject;
    private List<Action> actions;
    private Data data;

    public WSWidget() {
    }

    public boolean hasActions() {
      return (actions != null
          && actions.size() > 0
          && actions.get(0)
          .getEvent()
          .getName() != null);
    }

    public cm.aptoide.pt.dataprovider.model.v7.Type getType() {
      return this.type;
    }

    public WSWidget setType(cm.aptoide.pt.dataprovider.model.v7.Type type) {
      this.type = type;
      return this;
    }

    public String getTitle() {
      return this.title;
    }

    public WSWidget setTitle(String title) {
      this.title = title;
      return this;
    }

    public String getTag() {
      return this.tag;
    }

    public WSWidget setTag(String tag) {
      this.tag = tag;
      return this;
    }

    public String getView() {
      return this.view;
    }

    public WSWidget setView(String view) {
      this.view = view;
      return this;
    }

    public Object getViewObject() {
      return this.viewObject;
    }

    public WSWidget setViewObject(Object viewObject) {
      this.viewObject = viewObject;
      return this;
    }

    public List<Action> getActions() {
      return this.actions;
    }

    public WSWidget setActions(List<Action> actions) {
      this.actions = actions;
      return this;
    }

    public Data getData() {
      return this.data;
    }

    public WSWidget setData(Data data) {
      this.data = data;
      return this;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $type = this.getType();
      result = result * PRIME + ($type == null ? 43 : $type.hashCode());
      final Object $title = this.getTitle();
      result = result * PRIME + ($title == null ? 43 : $title.hashCode());
      final Object $tag = this.getTag();
      result = result * PRIME + ($tag == null ? 43 : $tag.hashCode());
      final Object $view = this.getView();
      result = result * PRIME + ($view == null ? 43 : $view.hashCode());
      final Object $viewObject = this.getViewObject();
      result = result * PRIME + ($viewObject == null ? 43 : $viewObject.hashCode());
      final Object $actions = this.getActions();
      result = result * PRIME + ($actions == null ? 43 : $actions.hashCode());
      final Object $data = this.getData();
      result = result * PRIME + ($data == null ? 43 : $data.hashCode());
      return result;
    }

    protected boolean canEqual(Object other) {
      return other instanceof WSWidget;
    }

    public static class Data {

      private Layout layout;
      private String icon;
      private String message;
      private List<Data.Categories> categories; //only present if type": "DISPLAYS"
      private Review.User user; //only on tabs (timeline)

      public Data() {
      }

      public Layout getLayout() {
        return this.layout;
      }

      public Data setLayout(Layout layout) {
        this.layout = layout;
        return this;
      }

      public String getIcon() {
        return this.icon;
      }

      public Data setIcon(String icon) {
        this.icon = icon;
        return this;
      }

      public String getMessage() {
        return this.message;
      }

      public Data setMessage(String message) {
        this.message = message;
        return this;
      }

      public List<Categories> getCategories() {
        return this.categories;
      }

      public Data setCategories(List<Categories> categories) {
        this.categories = categories;
        return this;
      }

      public Review.User getUser() {
        return this.user;
      }

      public Data setUser(Review.User user) {
        this.user = user;
        return this;
      }

      protected boolean canEqual(Object other) {
        return other instanceof Data;
      }

      public static class Categories {

        private long id;
        private String refId;
        private String parentId;
        private String parentRefId;
        private String name;
        private String graphic;
        private String icon;
        private int adsCount;

        public Categories() {
        }

        public long getId() {
          return this.id;
        }

        public Categories setId(long id) {
          this.id = id;
          return this;
        }

        public String getRefId() {
          return this.refId;
        }

        public Categories setRefId(String refId) {
          this.refId = refId;
          return this;
        }

        public String getParentId() {
          return this.parentId;
        }

        public Categories setParentId(String parentId) {
          this.parentId = parentId;
          return this;
        }

        public String getParentRefId() {
          return this.parentRefId;
        }

        public Categories setParentRefId(String parentRefId) {
          this.parentRefId = parentRefId;
          return this;
        }

        public String getName() {
          return this.name;
        }

        public Categories setName(String name) {
          this.name = name;
          return this;
        }

        public String getGraphic() {
          return this.graphic;
        }

        public Categories setGraphic(String graphic) {
          this.graphic = graphic;
          return this;
        }

        public String getIcon() {
          return this.icon;
        }

        public Categories setIcon(String icon) {
          this.icon = icon;
          return this;
        }

        public int getAdsCount() {
          return this.adsCount;
        }

        public Categories setAdsCount(int adsCount) {
          this.adsCount = adsCount;
          return this;
        }

        protected boolean canEqual(Object other) {
          return other instanceof Categories;
        }

        public boolean equals(Object o) {
          if (o == this) return true;
          if (!(o instanceof Categories)) return false;
          final Categories other = (Categories) o;
          if (!other.canEqual((Object) this)) return false;
          if (this.getId() != other.getId()) return false;
          final Object this$refId = this.getRefId();
          final Object other$refId = other.getRefId();
          if (this$refId == null ? other$refId != null : !this$refId.equals(other$refId)) {
            return false;
          }
          final Object this$parentId = this.getParentId();
          final Object other$parentId = other.getParentId();
          if (this$parentId == null ? other$parentId != null
              : !this$parentId.equals(other$parentId)) {
            return false;
          }
          final Object this$parentRefId = this.getParentRefId();
          final Object other$parentRefId = other.getParentRefId();
          if (this$parentRefId == null ? other$parentRefId != null
              : !this$parentRefId.equals(other$parentRefId)) {
            return false;
          }
          final Object this$name = this.getName();
          final Object other$name = other.getName();
          if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
          final Object this$graphic = this.getGraphic();
          final Object other$graphic = other.getGraphic();
          if (this$graphic == null ? other$graphic != null : !this$graphic.equals(other$graphic)) {
            return false;
          }
          final Object this$icon = this.getIcon();
          final Object other$icon = other.getIcon();
          if (this$icon == null ? other$icon != null : !this$icon.equals(other$icon)) return false;
          if (this.getAdsCount() != other.getAdsCount()) return false;
          return true;
        }

        public int hashCode() {
          final int PRIME = 59;
          int result = 1;
          final long $id = this.getId();
          result = result * PRIME + (int) ($id >>> 32 ^ $id);
          final Object $refId = this.getRefId();
          result = result * PRIME + ($refId == null ? 43 : $refId.hashCode());
          final Object $parentId = this.getParentId();
          result = result * PRIME + ($parentId == null ? 43 : $parentId.hashCode());
          final Object $parentRefId = this.getParentRefId();
          result = result * PRIME + ($parentRefId == null ? 43 : $parentRefId.hashCode());
          final Object $name = this.getName();
          result = result * PRIME + ($name == null ? 43 : $name.hashCode());
          final Object $graphic = this.getGraphic();
          result = result * PRIME + ($graphic == null ? 43 : $graphic.hashCode());
          final Object $icon = this.getIcon();
          result = result * PRIME + ($icon == null ? 43 : $icon.hashCode());
          result = result * PRIME + this.getAdsCount();
          return result;
        }

        public String toString() {
          return "GetStoreWidgets.WSWidget.Data.Categories(id="
              + this.getId()
              + ", refId="
              + this.getRefId()
              + ", parentId="
              + this.getParentId()
              + ", parentRefId="
              + this.getParentRefId()
              + ", name="
              + this.getName()
              + ", graphic="
              + this.getGraphic()
              + ", icon="
              + this.getIcon()
              + ", adsCount="
              + this.getAdsCount()
              + ")";
        }
      }

      public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Data)) return false;
        final Data other = (Data) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$layout = this.getLayout();
        final Object other$layout = other.getLayout();
        if (this$layout == null ? other$layout != null : !this$layout.equals(other$layout)) {
          return false;
        }
        final Object this$icon = this.getIcon();
        final Object other$icon = other.getIcon();
        if (this$icon == null ? other$icon != null : !this$icon.equals(other$icon)) return false;
        final Object this$message = this.getMessage();
        final Object other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) {
          return false;
        }
        final Object this$categories = this.getCategories();
        final Object other$categories = other.getCategories();
        if (this$categories == null ? other$categories != null
            : !this$categories.equals(other$categories)) {
          return false;
        }
        final Object this$user = this.getUser();
        final Object other$user = other.getUser();
        if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
        return true;
      }

      public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $layout = this.getLayout();
        result = result * PRIME + ($layout == null ? 43 : $layout.hashCode());
        final Object $icon = this.getIcon();
        result = result * PRIME + ($icon == null ? 43 : $icon.hashCode());
        final Object $message = this.getMessage();
        result = result * PRIME + ($message == null ? 43 : $message.hashCode());
        final Object $categories = this.getCategories();
        result = result * PRIME + ($categories == null ? 43 : $categories.hashCode());
        final Object $user = this.getUser();
        result = result * PRIME + ($user == null ? 43 : $user.hashCode());
        return result;
      }

      public String toString() {
        return "GetStoreWidgets.WSWidget.Data(layout="
            + this.getLayout()
            + ", icon="
            + this.getIcon()
            + ", message="
            + this.getMessage()
            + ", categories="
            + this.getCategories()
            + ", user="
            + this.getUser()
            + ")";
      }
    }

    public static class Action {

      private String type; // button
      private String label;
      private String tag;
      private Event event;

      public Action() {
      }

      public String getType() {
        return this.type;
      }

      public Action setType(String type) {
        this.type = type;
        return this;
      }

      public String getLabel() {
        return this.label;
      }

      public Action setLabel(String label) {
        this.label = label;
        return this;
      }

      public String getTag() {
        return this.tag;
      }

      public Action setTag(String tag) {
        this.tag = tag;
        return this;
      }

      public Event getEvent() {
        return this.event;
      }

      public Action setEvent(Event event) {
        this.event = event;
        return this;
      }

      protected boolean canEqual(Object other) {
        return other instanceof Action;
      }

      public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Action)) return false;
        final Action other = (Action) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final Object this$label = this.getLabel();
        final Object other$label = other.getLabel();
        if (this$label == null ? other$label != null : !this$label.equals(other$label)) {
          return false;
        }
        final Object this$tag = this.getTag();
        final Object other$tag = other.getTag();
        if (this$tag == null ? other$tag != null : !this$tag.equals(other$tag)) return false;
        final Object this$event = this.getEvent();
        final Object other$event = other.getEvent();
        if (this$event == null ? other$event != null : !this$event.equals(other$event)) {
          return false;
        }
        return true;
      }

      public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final Object $label = this.getLabel();
        result = result * PRIME + ($label == null ? 43 : $label.hashCode());
        final Object $tag = this.getTag();
        result = result * PRIME + ($tag == null ? 43 : $tag.hashCode());
        final Object $event = this.getEvent();
        result = result * PRIME + ($event == null ? 43 : $event.hashCode());
        return result;
      }

      public String toString() {
        return "GetStoreWidgets.WSWidget.Action(type="
            + this.getType()
            + ", label="
            + this.getLabel()
            + ", tag="
            + this.getTag()
            + ", event="
            + this.getEvent()
            + ")";
      }
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof WSWidget)) return false;
      final WSWidget other = (WSWidget) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$type = this.getType();
      final Object other$type = other.getType();
      if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
      final Object this$title = this.getTitle();
      final Object other$title = other.getTitle();
      if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
      final Object this$tag = this.getTag();
      final Object other$tag = other.getTag();
      if (this$tag == null ? other$tag != null : !this$tag.equals(other$tag)) return false;
      final Object this$view = this.getView();
      final Object other$view = other.getView();
      if (this$view == null ? other$view != null : !this$view.equals(other$view)) return false;
      final Object this$viewObject = this.getViewObject();
      final Object other$viewObject = other.getViewObject();
      if (this$viewObject == null ? other$viewObject != null
          : !this$viewObject.equals(other$viewObject)) {
        return false;
      }
      final Object this$actions = this.getActions();
      final Object other$actions = other.getActions();
      if (this$actions == null ? other$actions != null : !this$actions.equals(other$actions)) {
        return false;
      }
      final Object this$data = this.getData();
      final Object other$data = other.getData();
      if (this$data == null ? other$data != null : !this$data.equals(other$data)) return false;
      return true;
    }

    public String toString() {
      return "GetStoreWidgets.WSWidget(type="
          + this.getType()
          + ", title="
          + this.getTitle()
          + ", tag="
          + this.getTag()
          + ", view="
          + this.getView()
          + ", viewObject="
          + this.getViewObject()
          + ", actions="
          + this.getActions()
          + ", data="
          + this.getData()
          + ")";
    }
  }
}
