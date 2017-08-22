package cm.aptoide.pt.dataprovider.ws.v7.post;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;

/**
 * Created by jdandrade on 31/07/2017.
 */

public class PostInTimelineResponse extends BaseV7Response {
  private Data data;

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public static class Data {
    private PostData data;

    public Data() {

    }

    public PostData getData() {
      return data;
    }

    public void setData(PostData data) {
      this.data = data;
    }
  }

  public static class PostData {
    private String uid;

    public PostData() {

    }

    public String getUid() {
      return uid;
    }

    public void setUid(String uid) {
      this.uid = uid;
    }
  }
}

