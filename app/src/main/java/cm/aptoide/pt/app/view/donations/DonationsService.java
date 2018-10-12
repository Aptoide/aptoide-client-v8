package cm.aptoide.pt.app.view.donations;

import cm.aptoide.pt.dataprovider.model.v7.GetDonations;
import java.util.ArrayList;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Single;

/**
 * Created by franciscocalado on 11/10/2018.
 */

public class DonationsService {

  private ServiceV7 service;

  public DonationsService(ServiceV7 service) {
    this.service = service;
  }

  public Single<List<Donation>> getDonations(String packageName) {
    return service.getDonations(packageName, 5)
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

  public interface ServiceV7 {
    @GET("broker/8.20181010/leaderboard/donations")//?domain=<package name>&limit=5"
    Observable<GetDonations> getDonations(@Query("domain") String packageName,
        @Query("limit") int limit);
  }
}
