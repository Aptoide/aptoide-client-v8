package cm.aptoide.pt.dataprovider.ws.v7.store;

import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RequestBodyFactory {

  public RequestBody createBodyPartFromLong(long longValue) {
    return RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(longValue));
  }

  public RequestBody createBodyPartFromString(String string) {
    if (string == null) {
      string = "";
    }
    return RequestBody.create(MediaType.parse("multipart/form-data"), string);
  }

  public MultipartBody.Part createBodyPartFromFile(String key, File file) {
    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
    return MultipartBody.Part.createFormData(key, file.getName(), requestFile);
  }
}