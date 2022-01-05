package cm.aptoide.pt.aab;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.Split;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class DynamicSplitsResponse extends BaseV7Response {

  @JsonProperty("list") private List<DynamicSplit> dynamicSplitList;

  public DynamicSplitsResponse() {
  }

  public List<DynamicSplit> getDynamicSplitList() {
    return dynamicSplitList;
  }

  public void setDynamicSplitList(List<DynamicSplit> dynamicSplitList) {
    this.dynamicSplitList = dynamicSplitList;
  }

  public static class DynamicSplit {

    @JsonProperty("delivery_types") List<String> deliveryTypes;
    private String name;
    private String type;
    private String md5sum;
    private String path;
    private Long filesize;
    private List<Split> splits;

    public DynamicSplit() {
    }

    public List<String> getDeliveryTypes() {
      return deliveryTypes;
    }

    public void setDeliveryTypes(List<String> deliveryTypes) {
      this.deliveryTypes = deliveryTypes;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getMd5sum() {
      return md5sum;
    }

    public void setMd5sum(String md5sum) {
      this.md5sum = md5sum;
    }

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }

    public Long getFilesize() {
      return filesize;
    }

    public void setFilesize(Long filesize) {
      this.filesize = filesize;
    }

    public List<Split> getSplits() {
      return splits;
    }

    public void setSplits(List<Split> splits) {
      this.splits = splits;
    }
  }
}
