package cm.aptoide.pt.app.view.donations;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetDonations;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.donations.GetWalletAddressRequest;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Single;

/**
 * Created by franciscocalado on 11/10/2018.
 */

public class DonationsService {

  private final BodyInterceptor<BaseBody> bodyInterceptorPoolV7;
  private ServiceV8 service;
  private SharedPreferences sharedPreferences;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;

  public DonationsService(ServiceV8 service, SharedPreferences sharedPreferences,
      OkHttpClient okHttpClient, Converter.Factory converterFactory,
      BodyInterceptor<BaseBody> bodyInterceptorPoolV7, TokenInvalidator tokenInvalidator) {
    this.service = service;
    this.sharedPreferences = sharedPreferences;
    this.httpClient = okHttpClient;
    this.converterFactory = converterFactory;
    this.bodyInterceptorPoolV7 = bodyInterceptorPoolV7;
    this.tokenInvalidator = tokenInvalidator;
  }

  public Single<List<Donation>> getDonations(String packageName) {
    return service.getDonations(packageName, 5)
        .map(this::mapToDonationsList)
        .toSingle();
  }

  public Single<String> getWalletAddress(String packageName) {
    return GetWalletAddressRequest.of(packageName, sharedPreferences, httpClient, converterFactory,
        bodyInterceptorPoolV7, tokenInvalidator)
        .observe()
        .map(response -> response.getData()
            .getAddress())
        .toSingle();
  }

  private List<Donation> mapToDonationsList(GetDonations donationsResponse) {
    List<Donation> result = new ArrayList<>();

    for (GetDonations.Donor donor : donationsResponse.getItems())
      result.add(
          new Donation(donor.getDomain(), donor.getOwner(), Float.parseFloat(donor.getAppc())));
    return result;
  }

  public interface ServiceV8 {
    @GET("broker/8.20181010/leaderboard/donations") Observable<GetDonations> getDonations(
        @Query("domain") String packageName, @Query("limit") int limit);
  }
}
