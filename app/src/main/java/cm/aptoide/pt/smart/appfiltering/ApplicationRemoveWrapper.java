package cm.aptoide.pt.smart.appfiltering;

import okhttp3.Interceptor;
import okhttp3.Response;
import android.util.Log;
import java.io.IOException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.json.JSONException;

public class ApplicationRemoveWrapper implements Interceptor {
    private static final String LIST_APP = "/api/7/listApps";
    private static final String SEARCH_APP = "/api/7/listSearchApps";
    private static final String GET_APP = "/api/7/getApp";
    private static final String LIST_VERSIONS = "/api/7/listAppVersions";

    private RemoveHelper helper = new RemoveHelper();
    private FilterHelper filterHelper = new FilterHelper();

    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        String path = chain.request().url().uri().getPath();
        if (path.contains(LIST_APP) || path.contains(SEARCH_APP)) {
            String bodyString = originalResponse.body().string();
            MediaType contentType = originalResponse.body().contentType();
            JSONObject object = helper.filterResponse(bodyString, FilteringList.removingList);
            if (object != null) {
                return rebuildResponse(originalResponse, object, chain);
            }
        }

        if (path.contains(GET_APP) ) {
            String bodyString = originalResponse.body().string();
            JSONObject object = filterHelper.filterResponse(bodyString, FilteringList.filteringList);
            if (object != null) {
                return rebuildResponse(originalResponse, object, chain);
            }
        }

        if (path.contains(LIST_VERSIONS)) {
            String bodyString = originalResponse.body().string();
            JSONObject object = filterHelper.filterVersionRequest(bodyString, FilteringList.filteringList);
            if (object != null) {
                return rebuildResponse(originalResponse, object, chain);
            }
        }
        return originalResponse;
    }


    private Response rebuildResponse(Response originalResponse, JSONObject modifiedResponse, Interceptor.Chain chain) {
        MediaType contentType = originalResponse.body().contentType();
        Response r = new Response.Builder()
                .body(ResponseBody.create(contentType, modifiedResponse.toString()))
                .request(chain.request())
                .protocol(originalResponse.protocol())
                .code(originalResponse.code())
                .message(originalResponse.message())
                .build();

        return r;
    }
}
