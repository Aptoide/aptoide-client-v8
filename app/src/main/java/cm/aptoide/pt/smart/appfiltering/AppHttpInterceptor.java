package cm.aptoide.pt.smart.appfiltering;

import okhttp3.Interceptor;
import okhttp3.Response;
import java.io.IOException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.ResponseBody;

public class AppHttpInterceptor implements Interceptor {
    private static final String LIST_APP = "/api/7/listApps";
    private static final String SEARCH_APP = "/api/7/listSearchApps";
    private static final String GET_APP = "/api/7/getApp";
    private static final String LIST_VERSIONS = "/api/7/listAppVersions";

    private final RemoveHelper removeHelper = new RemoveHelper();
    private final FilterHelper filterHelper = new FilterHelper();
    private final AddHelper addHelper = new AddHelper();

    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        String path = chain.request().url().uri().getPath();
        if (path.contains(LIST_APP) || path.contains(SEARCH_APP)) {

            ResponseBody responseBody = originalResponse.body();
            if(responseBody != null) {
                String bodyString = responseBody.string();
                JSONObject objectAfterRemove = removeHelper.filterResponse(bodyString, FilteringList.removingList);
                JSONObject objectAfterAdd = null;

                // Here's the order:
                // 1) Apply the filter/removing list first to the original response.
                // 2) If that succeeds, use the returned list & then apply the added list.
                //   - If it fails, use the original response & apply the added list.
                // 3) If the added list is non-null, then use it - it will have apps filtered/removed (if step 2 succeeded) + added apps.
                //   - If it is null, then just use the filtered/removed list (if it's not null) since adding apps failed.
                // 4) If any of this fails, then just return the original response (at the end of the function).

                if (objectAfterRemove != null) {
                    objectAfterAdd = addHelper.filterResponse(path, objectAfterRemove.toString(), AddingList.addingList);
                } else {
                    objectAfterAdd = addHelper.filterResponse(path, bodyString, AddingList.addingList);
                }

                if (objectAfterAdd != null) {
                    return rebuildResponse(originalResponse, objectAfterAdd, chain);
                }
                if (objectAfterRemove != null) {
                    return rebuildResponse(originalResponse, objectAfterRemove, chain);
                }
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
        return new Response.Builder()
                .body(ResponseBody.create(contentType, modifiedResponse.toString()))
                .request(chain.request())
                .protocol(originalResponse.protocol())
                .code(originalResponse.code())
                .message(originalResponse.message())
                .build();
    }
}
