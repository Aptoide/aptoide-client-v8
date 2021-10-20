package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.aab.AppBundlesVisibilityManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.ListApps;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class GetEskillsAppsRequest extends V7<ListApps, GetEskillsAppsRequest.Body> {

  private final AppBundlesVisibilityManager appBundlesVisibilityManager;

  public GetEskillsAppsRequest(Body body, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences,
      AppBundlesVisibilityManager appBundlesVisibilityManager) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
    this.appBundlesVisibilityManager = appBundlesVisibilityManager;
  }

  @Override
  protected Observable<ListApps> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.
        getEskillsApps(body, bypassCache, body.getLimit(), body.getGroupId(),
            appBundlesVisibilityManager.shouldEnableAppBundles());
  }

  public static class Body extends BaseBody implements Endless {
    private int offset;
    private final int limit;
    private final long groupId;

    public Body(int offset, int limit, long groupId) {
      this.offset = offset;
      this.limit = limit;
      this.groupId = groupId;
    }

    public int getOffset() {
      return offset;
    }

    public void setOffset(int offset) {
      this.offset = offset;
    }

    @Override public Integer getLimit() {
      return this.limit;
    }

    public long getGroupId() {
      return groupId;
    }
  }
}
