package cm.aptoide.pt.download;

import android.content.SharedPreferences;
import android.support.annotation.CallSuper;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.DownloadAnalyticsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.Result;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.analytics.Event;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by trinkes on 02/01/2017.
 */

public class DownloadInstallBaseEvent implements Event {
  private final SharedPreferences sharedPreferences;
  private Action action;
  private int versionCode;
  private Origin origin;
  private String packageName;
  private String url;
  private ObbType obbType;
  private String obbUrl;
  private ObbType patchObbType;
  private String patchObbUrl;
  private String name;
  private AppContext context;
  private DownloadInstallEventConverter downloadInstallEventConverter;
  private Result.ResultStatus resultStatus;
  private Throwable error;
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;

  public DownloadInstallBaseEvent(Action action, Origin origin, String packageName, String url,
      String obbUrl, String patchObbUrl, AppContext context, int versionCode,
      DownloadInstallEventConverter downloadInstallEventConverter, String eventName,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this.action = action;
    this.versionCode = versionCode;
    this.origin = origin;
    this.packageName = packageName;
    this.url = url;
    this.obbType = ObbType.MAIN;
    this.obbUrl = obbUrl;
    this.patchObbType = ObbType.PATCH;
    this.patchObbUrl = patchObbUrl;
    this.name = eventName;
    this.context = context;
    this.downloadInstallEventConverter = downloadInstallEventConverter;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  @Override public String toString() {
    return "DownloadInstallBaseEvent{"
        + "sharedPreferences="
        + sharedPreferences
        + ", action="
        + action
        + ", versionCode="
        + versionCode
        + ", origin="
        + origin
        + ", packageName='"
        + packageName
        + '\''
        + ", url='"
        + url
        + '\''
        + ", obbType="
        + obbType
        + ", obbUrl='"
        + obbUrl
        + '\''
        + ", patchObbType="
        + patchObbType
        + ", patchObbUrl='"
        + patchObbUrl
        + '\''
        + ", name='"
        + name
        + '\''
        + ", context="
        + context
        + ", downloadInstallEventConverter="
        + downloadInstallEventConverter
        + ", resultStatus="
        + resultStatus
        + ", error="
        + error
        + ", bodyInterceptor="
        + bodyInterceptor
        + ", httpClient="
        + httpClient
        + ", converterFactory="
        + converterFactory
        + ", tokenInvalidator="
        + tokenInvalidator
        + '}';
  }

  public SharedPreferences getSharedPreferences() {
    return sharedPreferences;
  }

  public Action getAction() {
    return action;
  }

  public void setAction(Action action) {
    this.action = action;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public void setVersionCode(int versionCode) {
    this.versionCode = versionCode;
  }

  public Origin getOrigin() {
    return origin;
  }

  public void setOrigin(Origin origin) {
    this.origin = origin;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public ObbType getObbType() {
    return obbType;
  }

  public void setObbType(ObbType obbType) {
    this.obbType = obbType;
  }

  public String getObbUrl() {
    return obbUrl;
  }

  public void setObbUrl(String obbUrl) {
    this.obbUrl = obbUrl;
  }

  public ObbType getPatchObbType() {
    return patchObbType;
  }

  public void setPatchObbType(ObbType patchObbType) {
    this.patchObbType = patchObbType;
  }

  public String getPatchObbUrl() {
    return patchObbUrl;
  }

  public void setPatchObbUrl(String patchObbUrl) {
    this.patchObbUrl = patchObbUrl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AppContext getContext() {
    return context;
  }

  public void setContext(AppContext context) {
    this.context = context;
  }

  public DownloadInstallEventConverter getDownloadInstallEventConverter() {
    return downloadInstallEventConverter;
  }

  public void setDownloadInstallEventConverter(
      DownloadInstallEventConverter downloadInstallEventConverter) {
    this.downloadInstallEventConverter = downloadInstallEventConverter;
  }

  public Result.ResultStatus getResultStatus() {
    return resultStatus;
  }

  public void setResultStatus(Result.ResultStatus resultStatus) {
    this.resultStatus = resultStatus;
  }

  public Throwable getError() {
    return error;
  }

  public void setError(Throwable error) {
    this.error = error;
  }

  public BodyInterceptor<BaseBody> getBodyInterceptor() {
    return bodyInterceptor;
  }

  public void setBodyInterceptor(BodyInterceptor<BaseBody> bodyInterceptor) {
    this.bodyInterceptor = bodyInterceptor;
  }

  public OkHttpClient getHttpClient() {
    return httpClient;
  }

  public void setHttpClient(OkHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public Converter.Factory getConverterFactory() {
    return converterFactory;
  }

  public void setConverterFactory(Converter.Factory converterFactory) {
    this.converterFactory = converterFactory;
  }

  public TokenInvalidator getTokenInvalidator() {
    return tokenInvalidator;
  }

  public void setTokenInvalidator(TokenInvalidator tokenInvalidator) {
    this.tokenInvalidator = tokenInvalidator;
  }

  @Override public void send() {
    if (isReadyToSend()) {
      DownloadAnalyticsRequest.of(downloadInstallEventConverter.convert(this, resultStatus, error),
          action.name(), name, context.name(), bodyInterceptor, httpClient, converterFactory,
          tokenInvalidator, sharedPreferences)
          .observe()
          .subscribe(baseV7Response -> Logger.d(this, "onResume: " + baseV7Response),
              throwable -> throwable.printStackTrace());
    } else {
      Logger.e(this, "The event was not ready to send!");
    }
  }

  @CallSuper public boolean isReadyToSend() {
    return resultStatus != null;
  }

  public enum Action {
    CLICK, AUTO
  }

  public enum Origin {
    INSTALL, UPDATE, DOWNGRADE, UPDATE_ALL
  }

  private enum ObbType {
    MAIN, PATCH
  }

  public enum AppContext {
    TIMELINE, APPVIEW, UPDATE_TAB, SCHEDULED, DOWNLOADS
  }
}
