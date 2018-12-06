package cm.aptoide.pt.app.view.donations;

import cm.aptoide.pt.app.view.donations.data.GetWalletAddressResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

public class WalletService {

  private ServiceV7 service;
  private Scheduler viewScheduler;

  public WalletService(ServiceV7 service, Scheduler viewScheduler) {
    this.service = service;
    this.viewScheduler = viewScheduler;
  }

  public Single<String> getWalletAddress(String packageName) {
    return service.getWallet(packageName)
        .map(response -> response.getData()
            .getAddress())
        .toSingle()
        .subscribeOn(viewScheduler);
  }

  public interface ServiceV7 {
    @GET("bds/apks/package/getOwnerWallet") Observable<GetWalletAddressResponse> getWallet(
        @Query("package_name") String packageName);
  }
}
