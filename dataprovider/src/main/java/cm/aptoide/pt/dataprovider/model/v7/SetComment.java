package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created by jdandrade on 08/02/2017.
 */

public class SetComment extends BaseV7Response {

  private Data data;

  public SetComment() {
  }

  public Data getData() {
    return this.data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SetComment)) return false;
    final SetComment other = (SetComment) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    final Object this$data = this.getData();
    final Object other$data = other.getData();
    if (this$data == null ? other$data != null : !this$data.equals(other$data)) return false;
    return true;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    final Object $data = this.getData();
    result = result * PRIME + ($data == null ? 43 : $data.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof SetComment;
  }

  public String toString() {
    return "SetComment(data=" + this.getData() + ")";
  }

  public static class Data {
    private long id;
    private String body;
    private Comment.User user;
    private String status;
    private String mode;

    public Data() {
    }

    public long getId() {
      return this.id;
    }

    public void setId(long id) {
      this.id = id;
    }

    public String getBody() {
      return this.body;
    }

    public void setBody(String body) {
      this.body = body;
    }

    public Comment.User getUser() {
      return this.user;
    }

    public void setUser(Comment.User user) {
      this.user = user;
    }

    public String getStatus() {
      return this.status;
    }

    public void setStatus(String status) {
      this.status = status;
    }

    public String getMode() {
      return this.mode;
    }

    public void setMode(String mode) {
      this.mode = mode;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final long $id = this.getId();
      result = result * PRIME + (int) ($id >>> 32 ^ $id);
      final Object $body = this.getBody();
      result = result * PRIME + ($body == null ? 43 : $body.hashCode());
      final Object $user = this.getUser();
      result = result * PRIME + ($user == null ? 43 : $user.hashCode());
      final Object $status = this.getStatus();
      result = result * PRIME + ($status == null ? 43 : $status.hashCode());
      final Object $mode = this.getMode();
      result = result * PRIME + ($mode == null ? 43 : $mode.hashCode());
      return result;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Data)) return false;
      final Data other = (Data) o;
      if (!other.canEqual((Object) this)) return false;
      if (this.getId() != other.getId()) return false;
      final Object this$body = this.getBody();
      final Object other$body = other.getBody();
      if (this$body == null ? other$body != null : !this$body.equals(other$body)) return false;
      final Object this$user = this.getUser();
      final Object other$user = other.getUser();
      if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
      final Object this$status = this.getStatus();
      final Object other$status = other.getStatus();
      if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
        return false;
      }
      final Object this$mode = this.getMode();
      final Object other$mode = other.getMode();
      if (this$mode == null ? other$mode != null : !this$mode.equals(other$mode)) return false;
      return true;
    }

    public String toString() {
      return "SetComment.Data(id="
          + this.getId()
          + ", body="
          + this.getBody()
          + ", user="
          + this.getUser()
          + ", status="
          + this.getStatus()
          + ", mode="
          + this.getMode()
          + ")";
    }

    protected boolean canEqual(Object other) {
      return other instanceof Data;
    }
  }
}
