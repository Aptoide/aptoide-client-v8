package cm.aptoide.pt.promotions;

import cm.aptoide.pt.networking.IdsRepository;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public class CaptchaService {

  private ServiceInterface service;
  private IdsRepository idsRepository;

  private String captchaUrl;

  public CaptchaService(ServiceInterface service, IdsRepository idsRepository) {
    this.service = service;
    this.idsRepository = idsRepository;
  }

  public Single<String> getCaptcha() {
    return service.getCaptcha(idsRepository.getUniqueIdentifier())
        .subscribeOn(Schedulers.io())
        .map(response -> {
          if (response.isSuccessful() && response.body() != null) {
            return response.body()
                .getCaptchaUrl();
          } else {
            return "";
          }
        })
        .toSingle();
  }

  public String getCaptchaUrl() {
    return captchaUrl;
  }

  public void saveCaptchaUrl(String captchaUrl) {
    this.captchaUrl = captchaUrl;
  }

  public interface ServiceInterface {
    @GET("captcha/create/{user_id}") Observable<Response<ResponseBody>> getCaptcha(
        @Path(value = "user_id") String userId);
  }

  public static class ResponseBody {
    private String captchaUrl;

    public String getCaptchaUrl() {
      return captchaUrl;
    }

    public void setCaptchaUrl(String captchaUrl) {
      this.captchaUrl = captchaUrl;
    }
  }
}
