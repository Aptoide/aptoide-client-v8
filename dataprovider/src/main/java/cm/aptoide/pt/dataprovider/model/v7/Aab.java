package cm.aptoide.pt.dataprovider.model.v7;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Aab {
  @JsonProperty("required_split_types") private List<String> requiredSplits;
  private List<Split> splits;

  public List<Split> getSplits() {
    return splits;
  }

  public void setSplits(List<Split> splits) {
    this.splits = splits;
  }

  public List<String> getRequiredSplits() {
    return requiredSplits;
  }

  public void setRequiredSplits(List<String> requiredSplits) {
    this.requiredSplits = requiredSplits;
  }
}
