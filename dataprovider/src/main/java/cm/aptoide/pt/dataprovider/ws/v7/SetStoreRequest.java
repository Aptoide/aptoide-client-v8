package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.store.AccessTokenRequestBodyAdapter;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.preferences.Application;
import java.io.File;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;

/**
 * Created by pedroribeiro on 09/12/16.
 */

@Data @Accessors (chain = true) @EqualsAndHashCode(callSuper = true) public class SetStoreRequest
    extends V7<BaseV7Response, AccessTokenBody> {

  private static final String BASE_HOST = "https://ws75-primary.aptoide.com/api/7/";

  private MultipartBody.Part file;


  protected SetStoreRequest(AccessTokenBody body, String baseHost, MultipartBody.Part file) {
    super(body, baseHost, file);
  }

  public static SetStoreRequest of(String aptoideClientUUID, String accessToken,
      String storeName, String storeTheme, String storeAvatarPath) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    AccessTokenRequestBodyAdapter body =
        new AccessTokenRequestBodyAdapter(new BaseBody(), decorator, storeName, storeTheme);
    File file = new File(Application.getConfiguration().getUserAvatarCachePath() + "aptoide_store_avatar.png");
    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
    MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("store_avatar", file.getName(), requestFile);
    return new SetStoreRequest(body, BASE_HOST, multipartBody);
  }


  //private RequestBody createBodyPartFromString(String string) {
  //  return RequestBody.create(MediaType.parse("multipart/form-data"), string);
  //}

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {

    return interfaces.editStore(file, ((AccessTokenRequestBodyAdapter) body).get());
  }
}