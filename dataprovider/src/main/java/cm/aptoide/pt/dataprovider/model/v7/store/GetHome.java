package cm.aptoide.pt.dataprovider.model.v7.store;

/**
 * Created by trinkes on 23/02/2017.
 */
public class GetHome extends StoreUserAbstraction<GetHomeMeta> {

  public GetHome() {
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof GetHome;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof GetHome)) return false;
    final GetHome other = (GetHome) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    return true;
  }

  public String toString() {
    return "GetHome()";
  }
}
