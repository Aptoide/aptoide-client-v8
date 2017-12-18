package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created by pedroribeiro on 01/06/17.
 */

public class GetUserSettings extends BaseV7Response {

  private Data data;

  public GetUserSettings() {
  }

  public Data getData() {
    return this.data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $data = this.getData();
    result = result * PRIME + ($data == null ? 43 : $data.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof GetUserSettings;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof GetUserSettings)) return false;
    final GetUserSettings other = (GetUserSettings) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$data = this.getData();
    final Object other$data = other.getData();
    if (this$data == null ? other$data != null : !this$data.equals(other$data)) return false;
    return true;
  }

  public String toString() {
    return "GetUserSettings(data=" + this.getData() + ")";
  }

  public static class Data {
    private boolean mature;
    private Access access;

    public Data() {
    }

    public boolean isMature() {
      return this.mature;
    }

    public void setMature(boolean mature) {
      this.mature = mature;
    }

    public Access getAccess() {
      return this.access;
    }

    public void setAccess(Access access) {
      this.access = access;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      result = result * PRIME + (this.isMature() ? 79 : 97);
      final Object $access = this.getAccess();
      result = result * PRIME + ($access == null ? 43 : $access.hashCode());
      return result;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Data;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Data)) return false;
      final Data other = (Data) o;
      if (!other.canEqual((Object) this)) return false;
      if (this.isMature() != other.isMature()) return false;
      final Object this$access = this.getAccess();
      final Object other$access = other.getAccess();
      if (this$access == null ? other$access != null : !this$access.equals(other$access)) {
        return false;
      }
      return true;
    }

    public String toString() {
      return "GetUserSettings.Data(mature="
          + this.isMature()
          + ", access="
          + this.getAccess()
          + ")";
    }
  }

  public static class Access {
    private boolean confirmed;

    public Access() {
    }

    public boolean isConfirmed() {
      return this.confirmed;
    }

    public void setConfirmed(boolean confirmed) {
      this.confirmed = confirmed;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Access;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Access)) return false;
      final Access other = (Access) o;
      if (!other.canEqual((Object) this)) return false;
      if (this.isConfirmed() != other.isConfirmed()) return false;
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      result = result * PRIME + (this.isConfirmed() ? 79 : 97);
      return result;
    }

    public String toString() {
      return "GetUserSettings.Access(confirmed=" + this.isConfirmed() + ")";
    }
  }
}
