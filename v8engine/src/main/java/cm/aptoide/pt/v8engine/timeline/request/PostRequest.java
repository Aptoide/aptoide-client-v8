package cm.aptoide.pt.v8engine.timeline.request;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PostRequest extends BaseBody {
  private String url;
  private String content;
  @JsonProperty("package_name") private String packageName;

  public PostRequest(String url, String content, String packageName) {
    this.url = url;
    this.content = content;
    this.packageName = packageName;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }
}
