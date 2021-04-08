package cm.aptoide.pt.smart.appfiltering;

import okhttp3.Interceptor;
import okhttp3.Response;
import android.util.Log;
import java.io.IOException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.ResponseBody;


public class ApplicationRemoveWrapper implements Interceptor {
    private static final String LIST_APP = "/api/7/listApps";
    private static final String SEARCH_APP = "/api/7/listSearchApps";
    private RemoveHelper helper = new RemoveHelper();

    @Override public Response intercept(Interceptor.Chain chain) throws IOException {

        Response originalResponse = chain.proceed(chain.request());
        String path = chain.request().url().uri().getPath();
        if (path.contains(LIST_APP) || path.contains(SEARCH_APP)) {
            String bodyString = originalResponse.body().string();
            MediaType contentType = originalResponse.body().contentType();
            JSONObject object = helper.filterResponse(bodyString, FilteringList.removingList);
            if (object != null) {
                Response r = new Response.Builder()
                        .body(ResponseBody.create(contentType, object.toString()))
                        .request(chain.request())
                        .protocol(originalResponse.protocol())
                        .code(originalResponse.code())
                        .message(originalResponse.message())
                        .build();
                return r;
            } else {
                return originalResponse;
            }
        }
        return originalResponse;
    }
}
