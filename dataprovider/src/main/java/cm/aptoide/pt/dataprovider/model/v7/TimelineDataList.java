package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created by jdandrade on 21/11/2017.
 */

public class TimelineDataList<T> extends DataList<T> {

  private Data data;

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public class Data {
    private String version;

    public String getVersion() {
      return version;
    }

    public void setVersion(String version) {
      this.version = version;
    }
  }
}
