package cm.aptoide.pt.view.app;

import cm.aptoide.pt.billing.BillingIdResolver;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.install.AppAction;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.utils.q.QManager;
import java.util.List;

public class AppViewModel {
  private String packageName;
  private String appName;
  private String wUrl;
  private String md5;
  private String uname;
  private AppViewFragment.OpenType openType;
  private SearchAdResult searchAdResult;
  private long appId;
  private boolean sponsored;
  private String storeTheme;
  private String storeName;
  private GetAppMeta.App app;
  private AppAction appAction;
  private boolean suggestedShowing;
  private List<String> keywords;
  private BillingIdResolver billingIdResolver;
  private String marketName;
  private String defaultTheme;
  private long storeId;
  private String editorsBrickPosition;
  private String originTag;
  private QManager qManager;

  AppViewModel() {
    this.appAction = AppAction.OPEN;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  String getwUrl() {
    return wUrl;
  }

  void setwUrl(String wUrl) {
    this.wUrl = wUrl;
  }

  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  String getUname() {
    return uname;
  }

  void setUname(String uname) {
    this.uname = uname;
  }

  public AppViewFragment.OpenType getOpenType() {
    return openType;
  }

  public void setOpenType(AppViewFragment.OpenType openType) {
    this.openType = openType;
  }

  SearchAdResult getSearchAdResult() {
    return searchAdResult;
  }

  void setSearchAdResult(SearchAdResult searchAdResult) {
    this.searchAdResult = searchAdResult;
  }

  public long getAppId() {
    return appId;
  }

  public void setAppId(long appId) {
    this.appId = appId;
  }

  boolean isSponsored() {
    return sponsored;
  }

  void setSponsored(boolean sponsored) {
    this.sponsored = sponsored;
  }

  public String getStoreTheme() {
    return storeTheme;
  }

  public void setStoreTheme(String storeTheme) {
    this.storeTheme = storeTheme;
  }

  public String getStoreName() {
    return storeName;
  }

  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }

  public GetAppMeta.App getApp() {
    return app;
  }

  public void setApp(GetAppMeta.App app) {
    this.app = app;
  }

  AppAction getAppAction() {
    return appAction;
  }

  void setAppAction(AppAction appAction) {
    this.appAction = appAction;
  }

  boolean isSuggestedShowing() {
    return suggestedShowing;
  }

  void setSuggestedShowing(boolean suggestedShowing) {
    this.suggestedShowing = suggestedShowing;
  }

  List<String> getKeywords() {
    return keywords;
  }

  void setKeywords(List<String> keywords) {
    this.keywords = keywords;
  }

  BillingIdResolver getBillingIdResolver() {
    return billingIdResolver;
  }

  void setBillingIdResolver(BillingIdResolver billingIdResolver) {
    this.billingIdResolver = billingIdResolver;
  }

  public String getMarketName() {
    return marketName;
  }

  public void setMarketName(String marketName) {
    this.marketName = marketName;
  }

  public String getDefaultTheme() {
    return defaultTheme;
  }

  public void setDefaultTheme(String defaultTheme) {
    this.defaultTheme = defaultTheme;
  }

  public long getStoreId() {
    return storeId;
  }

  public void setStoreId(long storeId) {
    this.storeId = storeId;
  }

  String getEditorsBrickPosition() {
    return editorsBrickPosition;
  }

  void setEditorsBrickPosition(String editorsBrickPosition) {
    this.editorsBrickPosition = editorsBrickPosition;
  }

  String getOriginTag() {
    return originTag;
  }

  void setOriginTag(String originTag) {
    this.originTag = originTag;
  }

  QManager getqManager() {
    return qManager;
  }

  void setqManager(QManager qManager) {
    this.qManager = qManager;
  }
}
