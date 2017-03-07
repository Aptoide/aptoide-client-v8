package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.AccessTokenBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.SimpleSetStoreRequest;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by pedroribeiro on 12/12/16.
 */

public class AccessTokenRequestBodyAdapter implements AccessTokenBody {

  private static final String JSONOBJECT_ERROR = "Couldn't build store_properties json";
  private final BaseBody baseBody;
  private final BodyInterceptor decorator;
  private String storeName;
  private String accessToken;
  private String storeTheme;
  private Boolean storeEdit = false;
  private Long storeid;
  private SimpleSetStoreRequest.StoreProperties storeProperties;

  public AccessTokenRequestBodyAdapter(BaseBody baseBody, BodyInterceptor decorator,
      String accessToken, String storeName, String storeTheme) {
    this.baseBody = baseBody;
    this.decorator = decorator;
    this.storeName = storeName;
    storeProperties = new SimpleSetStoreRequest.StoreProperties(storeTheme, null);
    this.accessToken = accessToken;
  }

  public AccessTokenRequestBodyAdapter(BaseBody baseBody, BodyInterceptor decorator,
      String accessToken, String storeName, String storeTheme, String storeDescription,
      Boolean storeEdit, long storeid) {
    this.baseBody = baseBody;
    this.decorator = decorator;
    this.storeName = storeName;
    this.accessToken = accessToken;
    storeProperties = new SimpleSetStoreRequest.StoreProperties(storeTheme, storeDescription);
    this.storeEdit = storeEdit;
    this.storeid = storeid;
  }

  public HashMapNotNull<String, RequestBody> get() {
    decorator.intercept(baseBody);
    HashMapNotNull<String, RequestBody> body = new HashMapNotNull<>();
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    if (storeEdit) {
      body.put("store_id", createBodyPartFromLong(storeid));
      try {
        body.put("store_properties",
            createBodyPartFromString(mapper.writeValueAsString(storeProperties)));
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    } else {
      body.put("store_name", createBodyPartFromString(storeName));
      try {
        body.put("store_properties",
            createBodyPartFromString(mapper.writeValueAsString(storeProperties)));
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }
    body.put("access_token", createBodyPartFromString(accessToken));

    return body;
  }

  private RequestBody createBodyPartFromLong(Long longValue) {
    if (longValue == null) {
      longValue = Long.valueOf(0);
    }
    return RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(longValue));
  }

  private RequestBody createBodyPartFromString(String string) {
    if (string == null) {
      string = "";
    }
    return RequestBody.create(MediaType.parse("multipart/form-data"), string);
  }

  @Override public String getAccessToken() {
    return accessToken;
  }

  @Override public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }
}
