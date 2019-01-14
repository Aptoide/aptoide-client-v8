package cm.aptoide.pt.ads;

import cm.aptoide.pt.dataprovider.ws.v8.CampaignsServiceProvider;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Response;
import retrofit2.http.GET;
import rx.Observable;
import rx.schedulers.Schedulers;

public class CampaignsService implements CampaignsServiceProvider {
  private CampaignsServiceV8 service;

  public CampaignsService(CampaignsServiceV8 service) {
    this.service = service;
  }

  public Observable<CampaignsServiceResponse> getCampaigns() {
    return service.getCampaigns()
        .map(this::mapToResponse)
        .subscribeOn(Schedulers.io());
  }

  private CampaignsServiceResponse mapToResponse(Response<CampaignsServiceResponseBody> response) {
    List<CampaignsServiceResponse.Campaign> campaigns = new ArrayList<>();
    String next;
    if (!response.isSuccessful() || response.body() == null) {
      return new CampaignsServiceResponse();
    } else {
      return new CampaignsServiceResponse(response.body()
          .getNext(), mapResponseList(response.body()));
    }
  }

  private List<CampaignsServiceResponse.Campaign> mapResponseList(
      CampaignsServiceResponseBody response) {
    List<CampaignsServiceResponse.Campaign> campaigns = new ArrayList<>();
    for (CampaignsServiceResponseBody.CampaignApp app : response.getItems()) {
      campaigns.add(
          new CampaignsServiceResponse.Campaign(app.getUid(), app.getLabel(), app.getIcon(),
              app.getDownloads(), app.getPackageInfo()
              .getName(), app.getRating()
              .getAverage(), app.getCampaign()
              .getAppc(), app.getCampaign()
              .getUrls()
              .getClick(), app.getCampaign()
              .getUrls()
              .getDownload(), app.getPackageInfo()
              .getName()));
    }
    return campaigns;
  }

  public interface CampaignsServiceV8 {
    @GET("advertising/8.20181126/apps")
    Observable<Response<CampaignsServiceResponseBody>> getCampaigns();
  }
}
