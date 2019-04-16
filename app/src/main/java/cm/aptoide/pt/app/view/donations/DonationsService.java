package cm.aptoide.pt.app.view.donations;

import cm.aptoide.pt.app.view.donations.data.GetDonations;
import java.util.ArrayList;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

/**
 * Created by franciscocalado on 11/10/2018.
 */

public class DonationsService {

  private ServiceV8 service;
  private Scheduler ioScheduler;

  public DonationsService(ServiceV8 service, Scheduler ioScheduler) {
    this.service = service;
    this.ioScheduler = ioScheduler;
  }

  public Single<List<Donation>> getDonations(String packageName) {
    return service.getDonations(packageName, 5)
        .subscribeOn(ioScheduler)
        .map(this::mapToDonationsList)
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
