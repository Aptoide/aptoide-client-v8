package cm.aptoide.pt.dataprovider.ws.smart;

import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.smart.FilteredAppsDto;
import okhttp3.OkHttpClient;
import retrofit2.http.GET;
import rx.Observable;

public final class SMARTAppsAdd extends WebService<SMARTAppsAdd.Interfaces, FilteredAppsDto> {

    private static final String FILTERED_APPS_BASE_URL = "https://downloads.smarttech.com";

    public SMARTAppsAdd(OkHttpClient httpClient) {
        super(Interfaces.class, httpClient, getDefaultConverter(), FILTERED_APPS_BASE_URL);
    }

    @Override
    protected Observable<FilteredAppsDto> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
        return interfaces.getAddedApps();
    }

    interface Interfaces {
        @GET("/software/iq/appstore/addedapps.json")
        Observable<FilteredAppsDto> getAddedApps();
    }
}
