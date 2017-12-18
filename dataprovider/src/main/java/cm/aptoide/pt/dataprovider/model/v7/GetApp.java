/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import cm.aptoide.pt.dataprovider.model.v7.listapp.ListAppVersions;

/**
 * Created by hsousa on 28/10/15.
 */
public class GetApp extends BaseV7Response {

  private Nodes nodes;

  public GetApp() {
  }

  public Nodes getNodes() {
    return this.nodes;
  }

  public void setNodes(Nodes nodes) {
    this.nodes = nodes;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof GetApp)) return false;
    final GetApp other = (GetApp) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    final Object this$nodes = this.getNodes();
    final Object other$nodes = other.getNodes();
    if (this$nodes == null ? other$nodes != null : !this$nodes.equals(other$nodes)) return false;
    return true;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    final Object $nodes = this.getNodes();
    result = result * PRIME + ($nodes == null ? 43 : $nodes.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof GetApp;
  }

  public String toString() {
    return "GetApp(nodes=" + this.getNodes() + ")";
  }

  public static class Nodes {

    private GetAppMeta meta;
    private ListAppVersions versions;
    private GroupDatalist groups;

    public Nodes() {
    }

    public GetAppMeta getMeta() {
      return this.meta;
    }

    public void setMeta(GetAppMeta meta) {
      this.meta = meta;
    }

    public ListAppVersions getVersions() {
      return this.versions;
    }

    public void setVersions(ListAppVersions versions) {
      this.versions = versions;
    }

    public GroupDatalist getGroups() {
      return this.groups;
    }

    public void setGroups(GroupDatalist groups) {
      this.groups = groups;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $meta = this.getMeta();
      result = result * PRIME + ($meta == null ? 43 : $meta.hashCode());
      final Object $versions = this.getVersions();
      result = result * PRIME + ($versions == null ? 43 : $versions.hashCode());
      final Object $groups = this.getGroups();
      result = result * PRIME + ($groups == null ? 43 : $groups.hashCode());
      return result;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Nodes)) return false;
      final Nodes other = (Nodes) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$meta = this.getMeta();
      final Object other$meta = other.getMeta();
      if (this$meta == null ? other$meta != null : !this$meta.equals(other$meta)) return false;
      final Object this$versions = this.getVersions();
      final Object other$versions = other.getVersions();
      if (this$versions == null ? other$versions != null : !this$versions.equals(other$versions)) {
        return false;
      }
      final Object this$groups = this.getGroups();
      final Object other$groups = other.getGroups();
      if (this$groups == null ? other$groups != null : !this$groups.equals(other$groups)) {
        return false;
      }
      return true;
    }

    public String toString() {
      return "GetApp.Nodes(meta="
          + this.getMeta()
          + ", versions="
          + this.getVersions()
          + ", groups="
          + this.getGroups()
          + ")";
    }

    protected boolean canEqual(Object other) {
      return other instanceof Nodes;
    }
  }
}
