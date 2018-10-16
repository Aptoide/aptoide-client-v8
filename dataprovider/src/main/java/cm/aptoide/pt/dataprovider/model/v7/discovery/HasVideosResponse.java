package cm.aptoide.pt.dataprovider.model.v7.discovery;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;

/**
 * Created by franciscocalado on 27/09/2018.
 */

public class HasVideosResponse extends BaseV7Response {

  private Data data;

  private AbTesting abTesting;

  public HasVideosResponse() {
  }

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public AbTesting getAbTesting() {
    return abTesting;
  }

  public class Data {
    private int liveStreaming;
    private int nonStreaming;

    public Data() {
    }

    public int getLiveStreaming() {
      return liveStreaming;
    }

    public void setLiveStreaming(int liveStreaming) {
      this.liveStreaming = liveStreaming;
    }

    public int getNonStreaming() {
      return nonStreaming;
    }

    public void setNonStreaming(int nonStreaming) {
      this.nonStreaming = nonStreaming;
    }
  }

  public class AbTesting {
    private String name;
    private String assignment;

    public AbTesting() {
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getAssignment() {
      return assignment;
    }

    public void setAssignment(String assignment) {
      this.assignment = assignment;
    }
  }
}
