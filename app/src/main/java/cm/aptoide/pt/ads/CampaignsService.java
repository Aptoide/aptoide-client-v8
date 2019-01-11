package cm.aptoide.pt.ads;

import cm.aptoide.pt.dataprovider.ws.v8.CampaignsServiceProvider;
import com.fasterxml.jackson.annotation.JsonProperty;
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
        .observeOn(Schedulers.io());
  }

  private CampaignsServiceResponse mapToResponse(Response<ResponseBody> response) {
    List<CampaignsServiceResponse.Campaign> campaigns = new ArrayList<>();
    boolean hasError;
    String next;
    if (!response.isSuccessful() || response.body() == null) {
      hasError = true;
      next = "";
    } else {
      hasError = false;
      next = response.body()
          .getNext();
      for (ResponseBody.CampaignApp app : response.body()
          .getItems()) {
        campaigns.add(
            new CampaignsServiceResponse.Campaign(app.getUid(), app.getLabel(), app.getIcon(),
                app.getDownloads(), app.getPackageInfo()
                .getName(), app.getRating()
                .getAverage(), app.getRating()
                .getTotal(), app.getCampaign()
                .getAppc(), app.getCampaign()
                .getUrls()
                .getClick(), app.getCampaign()
                .getUrls()
                .getDownload(), app.getPackageInfo()
                .getName()));
      }
    }
    return new CampaignsServiceResponse(next, campaigns, hasError);
  }

  public interface CampaignsServiceV8 {
    @GET("advertising/8.20181126/apps") Observable<Response<ResponseBody>> getCampaigns();
  }

  public static class ResponseBody {
    private String next;
    private List<CampaignApp> items;

    public String getNext() {
      return next;
    }

    public void setNext(String next) {
      this.next = next;
    }

    public List<CampaignApp> getItems() {
      return items;
    }

    public void setItems(List<CampaignApp> items) {
      this.items = items;
    }

    public static class CampaignApp {
      private String uid;
      private String label;
      private String icon;
      private int downloads;
      @JsonProperty("package") private CampaignPackage packageInfo;
      private Rating rating;
      private CampaignInfo campaign;
      private ApkInfo apk;

      public String getUid() {
        return uid;
      }

      public void setUid(String uid) {
        this.uid = uid;
      }

      public String getLabel() {
        return label;
      }

      public void setLabel(String label) {
        this.label = label;
      }

      public String getIcon() {
        return icon;
      }

      public void setIcon(String icon) {
        this.icon = icon;
      }

      public Rating getRating() {
        return rating;
      }

      public void setRating(Rating rating) {
        this.rating = rating;
      }

      public CampaignInfo getCampaign() {
        return campaign;
      }

      public void setCampaign(CampaignInfo campaign) {
        this.campaign = campaign;
      }

      public ApkInfo getApk() {
        return apk;
      }

      public void setApk(ApkInfo apk) {
        this.apk = apk;
      }

      public CampaignPackage getPackageInfo() {
        return packageInfo;
      }

      public void setPackageInfo(CampaignPackage packageInfo) {
        this.packageInfo = packageInfo;
      }

      public int getDownloads() {
        return downloads;
      }

      public void setDownloads(int downloads) {
        this.downloads = downloads;
      }

      public static class CampaignPackage {
        private String name;

        public String getName() {
          return name;
        }

        public void setName(String name) {
          this.name = name;
        }
      }

      public static class Rating {
        private float average;
        private long total;

        public float getAverage() {
          return average;
        }

        public void setAverage(float average) {
          this.average = average;
        }

        public long getTotal() {
          return total;
        }

        public void setTotal(long total) {
          this.total = total;
        }
      }

      public static class CampaignInfo {
        private String appc;
        private CampaignUrls urls;

        public String getAppc() {
          return appc;
        }

        public void setAppc(String appc) {
          this.appc = appc;
        }

        public CampaignUrls getUrls() {
          return urls;
        }

        public void setUrls(CampaignUrls urls) {
          this.urls = urls;
        }

        public static class CampaignUrls {
          private String click;
          private String download;

          public String getClick() {
            return click;
          }

          public void setClick(String click) {
            this.click = click;
          }

          public String getDownload() {
            return download;
          }

          public void setDownload(String download) {
            this.download = download;
          }
        }
      }

      public static class ApkInfo {
        private ApkVersion version;

        public ApkVersion getVersion() {
          return version;
        }

        public void setVersion(ApkVersion version) {
          this.version = version;
        }

        public static class ApkVersion {
          private String name;

          public String getName() {
            return name;
          }

          public void setName(String name) {
            this.name = name;
          }
        }
      }
    }
  }
}
