package cm.aptoide.pt.promotions;

import retrofit2.Response;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;
import rx.Single;

public class CaptchaService {

  private ServiceInterface service;

  public CaptchaService(ServiceInterface service) {
    this.service = service;
  }

  public Single<String> getCaptcha(String userId) {
    return service.getCaptcha(userId)
        .map(response -> response.body()
            .getCaptchaUrl())
        .toSingle();
  }

  public interface ServiceInterface {
    @POST("captcha/create/{user_id}") Observable<Response<ResponseBody>> getCaptcha(
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
