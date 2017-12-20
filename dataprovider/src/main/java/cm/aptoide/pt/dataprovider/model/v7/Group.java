/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * Created by neuro on 22-04-2016.
 */
public class Group {

  private long id;
  private String name;
  private String title;
  private String icon;
  private String graphic;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") private Date added;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") private Date modified;
  private Parent parent;
  private Stats stats;

  public Group() {
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getIcon() {
    return this.icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getGraphic() {
    return this.graphic;
  }

  public void setGraphic(String graphic) {
    this.graphic = graphic;
  }

  public Date getAdded() {
    return this.added;
  }

  public void setAdded(Date added) {
    this.added = added;
  }

  public Date getModified() {
    return this.modified;
  }

  public void setModified(Date modified) {
    this.modified = modified;
  }

  public Parent getParent() {
    return this.parent;
  }

  public void setParent(Parent parent) {
    this.parent = parent;
  }

  public Stats getStats() {
    return this.stats;
  }

  public void setStats(Stats stats) {
    this.stats = stats;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final long $id = this.getId();
    result = result * PRIME + (int) ($id >>> 32 ^ $id);
    final Object $name = this.getName();
    result = result * PRIME + ($name == null ? 43 : $name.hashCode());
    final Object $title = this.getTitle();
    result = result * PRIME + ($title == null ? 43 : $title.hashCode());
    final Object $icon = this.getIcon();
    result = result * PRIME + ($icon == null ? 43 : $icon.hashCode());
    final Object $graphic = this.getGraphic();
    result = result * PRIME + ($graphic == null ? 43 : $graphic.hashCode());
    final Object $added = this.getAdded();
    result = result * PRIME + ($added == null ? 43 : $added.hashCode());
    final Object $modified = this.getModified();
    result = result * PRIME + ($modified == null ? 43 : $modified.hashCode());
    final Object $parent = this.getParent();
    result = result * PRIME + ($parent == null ? 43 : $parent.hashCode());
    final Object $stats = this.getStats();
    result = result * PRIME + ($stats == null ? 43 : $stats.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof Group;
  }

  public static class Parent {

    private long id;
    private String name;
    private String title;
    private String icon;
    private String graphic;

    public Parent() {
    }

    public long getId() {
      return this.id;
    }

    public void setId(long id) {
      this.id = id;
    }

    public String getName() {
      return this.name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getTitle() {
      return this.title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getIcon() {
      return this.icon;
    }

    public void setIcon(String icon) {
      this.icon = icon;
    }

    public String getGraphic() {
      return this.graphic;
    }

    public void setGraphic(String graphic) {
      this.graphic = graphic;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Parent;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Parent)) return false;
      final Parent other = (Parent) o;
      if (!other.canEqual(this)) return false;
      if (this.getId() != other.getId()) return false;
      final Object this$name = this.getName();
      final Object other$name = other.getName();
      if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
      final Object this$title = this.getTitle();
      final Object other$title = other.getTitle();
      if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
      final Object this$icon = this.getIcon();
      final Object other$icon = other.getIcon();
      if (this$icon == null ? other$icon != null : !this$icon.equals(other$icon)) return false;
      final Object this$graphic = this.getGraphic();
      final Object other$graphic = other.getGraphic();
      return this$graphic == null ? other$graphic == null : this$graphic.equals(other$graphic);
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final long $id = this.getId();
      result = result * PRIME + (int) ($id >>> 32 ^ $id);
      final Object $name = this.getName();
      result = result * PRIME + ($name == null ? 43 : $name.hashCode());
      final Object $title = this.getTitle();
      result = result * PRIME + ($title == null ? 43 : $title.hashCode());
      final Object $icon = this.getIcon();
      result = result * PRIME + ($icon == null ? 43 : $icon.hashCode());
      final Object $graphic = this.getGraphic();
      result = result * PRIME + ($graphic == null ? 43 : $graphic.hashCode());
      return result;
    }

    public String toString() {
      return "Group.Parent(id="
          + this.getId()
          + ", name="
          + this.getName()
          + ", title="
          + this.getTitle()
          + ", icon="
          + this.getIcon()
          + ", graphic="
          + this.getGraphic()
          + ")";
    }
  }

  public static class Stats {

    private int groups;
    private int items;

    public Stats() {
    }

    public int getGroups() {
      return this.groups;
    }

    public void setGroups(int groups) {
      this.groups = groups;
    }

    public int getItems() {
      return this.items;
    }

    public void setItems(int items) {
      this.items = items;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Stats;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Stats)) return false;
      final Stats other = (Stats) o;
      if (!other.canEqual(this)) return false;
      if (this.getGroups() != other.getGroups()) return false;
      return this.getItems() == other.getItems();
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      result = result * PRIME + this.getGroups();
      result = result * PRIME + this.getItems();
      return result;
    }

    public String toString() {
      return "Group.Stats(groups=" + this.getGroups() + ", items=" + this.getItems() + ")";
    }
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Group)) return false;
    final Group other = (Group) o;
    if (!other.canEqual(this)) return false;
    if (this.getId() != other.getId()) return false;
    final Object this$name = this.getName();
    final Object other$name = other.getName();
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
    final Object this$title = this.getTitle();
    final Object other$title = other.getTitle();
    if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
    final Object this$icon = this.getIcon();
    final Object other$icon = other.getIcon();
    if (this$icon == null ? other$icon != null : !this$icon.equals(other$icon)) return false;
    final Object this$graphic = this.getGraphic();
    final Object other$graphic = other.getGraphic();
    if (this$graphic == null ? other$graphic != null : !this$graphic.equals(other$graphic)) {
      return false;
    }
    final Object this$added = this.getAdded();
    final Object other$added = other.getAdded();
    if (this$added == null ? other$added != null : !this$added.equals(other$added)) return false;
    final Object this$modified = this.getModified();
    final Object other$modified = other.getModified();
    if (this$modified == null ? other$modified != null : !this$modified.equals(other$modified)) {
      return false;
    }
    final Object this$parent = this.getParent();
    final Object other$parent = other.getParent();
    if (this$parent == null ? other$parent != null : !this$parent.equals(other$parent)) {
      return false;
    }
    final Object this$stats = this.getStats();
    final Object other$stats = other.getStats();
    return this$stats == null ? other$stats == null : this$stats.equals(other$stats);
  }

  public String toString() {
    return "Group(id="
        + this.getId()
        + ", name="
        + this.getName()
        + ", title="
        + this.getTitle()
        + ", icon="
        + this.getIcon()
        + ", graphic="
        + this.getGraphic()
        + ", added="
        + this.getAdded()
        + ", modified="
        + this.getModified()
        + ", parent="
        + this.getParent()
        + ", stats="
        + this.getStats()
        + ")";
  }
}
