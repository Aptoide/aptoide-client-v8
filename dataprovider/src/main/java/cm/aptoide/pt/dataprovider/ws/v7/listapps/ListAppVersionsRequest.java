/*
 * Copyright (c) 2016.
 * Modified on 17/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import android.content.SharedPreferences;
import android.content.res.Resources;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithApp;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.LinkedList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
public class ListAppVersionsRequest extends V7<ListAppVersions, ListAppVersionsRequest.Body> {

  private static final Integer MAX_LIMIT = 10;

  private ListAppVersionsRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static ListAppVersionsRequest of(String packageName,
      HashMapNotNull<String, List<String>> storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, Resources resources) {
    Body body =
        new Body(packageName, sharedPreferences, AptoideUtils.SystemU.getCountryCode(resources));
    body.setStoresAuthMap(storeCredentials);
    body.setLimit(MAX_LIMIT);
    return new ListAppVersionsRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<ListAppVersions> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.listAppVersions(body, bypassCache, true);
  }

  public static class Body extends BaseBodyWithApp implements Endless {

    private Integer apkId;
    private String apkMd5sum;
    private Integer appId;
    private String lang;
    private Integer packageId;
    private String packageName;
    private List<Long> storeIds;
    private List<String> storeNames;
    private Integer limit;
    private int offset;
    private HashMapNotNull<String, List<String>> storesAuthMap;

    public Body(SharedPreferences sharedPreferences, String lang) {
      super(sharedPreferences);
      this.lang = lang;
    }

    public Body(String packageName, SharedPreferences sharedPreferences, String lang) {
      this(sharedPreferences, lang);
      this.packageName = packageName;
    }

    public Body(String packageName, List<String> storeNames,
        HashMapNotNull<String, List<String>> storesAuthMap, SharedPreferences sharedPreferences,
        String lang) {
      this(packageName, sharedPreferences, lang);
      this.storeNames = storeNames;
      setStoresAuthMap(storesAuthMap);
    }

    public HashMapNotNull<String, List<String>> getStoresAuthMap() {
      return storesAuthMap;
    }

    public void setStoresAuthMap(HashMapNotNull<String, List<String>> storesAuthMap) {
      if (storesAuthMap != null) {
        this.storesAuthMap = storesAuthMap;
        this.storeNames = new LinkedList<>(storesAuthMap.keySet());
      }
    }

    @Override public int getOffset() {
      return offset;
    }

    @Override public void setOffset(int offset) {
      this.offset = offset;
    }

    @Override public Integer getLimit() {
      return limit;
    }

    public void setLimit(Integer limit) {
      this.limit = limit;
    }

    public Integer getApkId() {
      return this.apkId;
    }

    public void setApkId(Integer apkId) {
      this.apkId = apkId;
    }

    public String getApkMd5sum() {
      return this.apkMd5sum;
    }

    public void setApkMd5sum(String apkMd5sum) {
      this.apkMd5sum = apkMd5sum;
    }

    public Integer getAppId() {
      return this.appId;
    }

    public void setAppId(Integer appId) {
      this.appId = appId;
    }

    public String getLang() {
      return this.lang;
    }

    public void setLang(String lang) {
      this.lang = lang;
    }

    public Integer getPackageId() {
      return this.packageId;
    }

    public void setPackageId(Integer packageId) {
      this.packageId = packageId;
    }

    public String getPackageName() {
      return this.packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }

    public List<Long> getStoreIds() {
      return this.storeIds;
    }

    public void setStoreIds(List<Long> storeIds) {
      this.storeIds = storeIds;
    }

    public List<String> getStoreNames() {
      return this.storeNames;
    }

    public void setStoreNames(List<String> storeNames) {
      this.storeNames = storeNames;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      result = result * PRIME + super.hashCode();
      final Object $apkId = this.getApkId();
      result = result * PRIME + ($apkId == null ? 43 : $apkId.hashCode());
      final Object $apkMd5sum = this.getApkMd5sum();
      result = result * PRIME + ($apkMd5sum == null ? 43 : $apkMd5sum.hashCode());
      final Object $appId = this.getAppId();
      result = result * PRIME + ($appId == null ? 43 : $appId.hashCode());
      final Object $lang = this.getLang();
      result = result * PRIME + ($lang == null ? 43 : $lang.hashCode());
      final Object $packageId = this.getPackageId();
      result = result * PRIME + ($packageId == null ? 43 : $packageId.hashCode());
      final Object $packageName = this.getPackageName();
      result = result * PRIME + ($packageName == null ? 43 : $packageName.hashCode());
      final Object $storeIds = this.getStoreIds();
      result = result * PRIME + ($storeIds == null ? 43 : $storeIds.hashCode());
      final Object $storeNames = this.getStoreNames();
      result = result * PRIME + ($storeNames == null ? 43 : $storeNames.hashCode());
      final Object $limit = this.getLimit();
      result = result * PRIME + ($limit == null ? 43 : $limit.hashCode());
      result = result * PRIME + this.getOffset();
      final Object $storesAuthMap = this.getStoresAuthMap();
      result = result * PRIME + ($storesAuthMap == null ? 43 : $storesAuthMap.hashCode());
      return result;
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Body)) return false;
      final Body other = (Body) o;
      if (!other.canEqual((Object) this)) return false;
      if (!super.equals(o)) return false;
      final Object this$apkId = this.getApkId();
      final Object other$apkId = other.getApkId();
      if (this$apkId == null ? other$apkId != null : !this$apkId.equals(other$apkId)) return false;
      final Object this$apkMd5sum = this.getApkMd5sum();
      final Object other$apkMd5sum = other.getApkMd5sum();
      if (this$apkMd5sum == null ? other$apkMd5sum != null
          : !this$apkMd5sum.equals(other$apkMd5sum)) {
        return false;
      }
      final Object this$appId = this.getAppId();
      final Object other$appId = other.getAppId();
      if (this$appId == null ? other$appId != null : !this$appId.equals(other$appId)) return false;
      final Object this$lang = this.getLang();
      final Object other$lang = other.getLang();
      if (this$lang == null ? other$lang != null : !this$lang.equals(other$lang)) return false;
      final Object this$packageId = this.getPackageId();
      final Object other$packageId = other.getPackageId();
      if (this$packageId == null ? other$packageId != null
          : !this$packageId.equals(other$packageId)) {
        return false;
      }
      final Object this$packageName = this.getPackageName();
      final Object other$packageName = other.getPackageName();
      if (this$packageName == null ? other$packageName != null
          : !this$packageName.equals(other$packageName)) {
        return false;
      }
      final Object this$storeIds = this.getStoreIds();
      final Object other$storeIds = other.getStoreIds();
      if (this$storeIds == null ? other$storeIds != null : !this$storeIds.equals(other$storeIds)) {
        return false;
      }
      final Object this$storeNames = this.getStoreNames();
      final Object other$storeNames = other.getStoreNames();
      if (this$storeNames == null ? other$storeNames != null
          : !this$storeNames.equals(other$storeNames)) {
        return false;
      }
      final Object this$limit = this.getLimit();
      final Object other$limit = other.getLimit();
      if (this$limit == null ? other$limit != null : !this$limit.equals(other$limit)) return false;
      if (this.getOffset() != other.getOffset()) return false;
      final Object this$storesAuthMap = this.getStoresAuthMap();
      final Object other$storesAuthMap = other.getStoresAuthMap();
      if (this$storesAuthMap == null ? other$storesAuthMap != null
          : !this$storesAuthMap.equals(other$storesAuthMap)) {
        return false;
      }
      return true;
    }

    public String toString() {
      return "ListAppVersionsRequest.Body(apkId="
          + this.getApkId()
          + ", apkMd5sum="
          + this.getApkMd5sum()
          + ", appId="
          + this.getAppId()
          + ", lang="
          + this.getLang()
          + ", packageId="
          + this.getPackageId()
          + ", packageName="
          + this.getPackageName()
          + ", storeIds="
          + this.getStoreIds()
          + ", storeNames="
          + this.getStoreNames()
          + ", limit="
          + this.getLimit()
          + ", offset="
          + this.getOffset()
          + ", storesAuthMap="
          + this.getStoresAuthMap()
          + ")";
    }

    protected boolean canEqual(Object other) {
      return other instanceof Body;
    }
  }
}
