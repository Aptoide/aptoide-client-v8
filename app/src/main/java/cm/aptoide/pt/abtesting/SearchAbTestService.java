package cm.aptoide.pt.abtesting;

import cm.aptoide.pt.autoupdate.AbSearchGroupResponse;
import retrofit2.http.GET;
import rx.Observable;

public class SearchAbTestService {
  private final Service service;

  public SearchAbTestService(Service service) {
    this.service = service;
  }

  public Observable<AbSearchGroupResponse> getExperimentForSearchAbTest() {
    return service.getAbTestForSearch();
  }

  public interface Service {
    @GET("search_ab_test_mobile.json") Observable<AbSearchGroupResponse> getAbTestForSearch();
  }
}
