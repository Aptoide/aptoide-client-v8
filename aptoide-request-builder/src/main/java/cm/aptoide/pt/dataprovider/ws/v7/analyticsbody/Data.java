package cm.aptoide.pt.dataprovider.ws.v7.analyticsbody;

import java.util.List;

public class Data {
  private DataOrigin origin;
  private App app;
  private List<Obb> obb;
  private String network;
  private String teleco; //typo telco - this will change in v2
  private Result result;
  private Root root;

  public DataOrigin getOrigin() {
    return origin;
  }

  public void setOrigin(DataOrigin origin) {
    this.origin = origin;
  }

  public App getApp() {
    return app;
  }

  public void setApp(App app) {
    this.app = app;
  }

  public List<Obb> getObb() {
    return obb;
  }

  public void setObb(List<Obb> obb) {
    this.obb = obb;
  }

  public String getNetwork() {
    return network;
  }

  public void setNetwork(String network) {
    this.network = network;
  }

  public String getTeleco() {
    return teleco;
  }

  public void setTeleco(String teleco) {
    this.teleco = teleco;
  }

  public Result getResult() {
    return result;
  }

  public void setResult(Result result) {
    this.result = result;
  }

  public Root getRoot() {
    return root;
  }

  public void setRoot(Root root) {
    this.root = root;
  }

  public enum DataOrigin {
    INSTALL, UPDATE, DOWNGRADE, UPDATE_ALL
  }
}
