package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.AccessTokenBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.SimpleSetStoreRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.listapp.File;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.Application;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pedroribeiro on 12/12/16.
 */

public class AccessTokenRequestBodyAdapter implements AccessTokenBody {

  private final BaseBody baseBody;
  private final BaseBodyDecorator decorator;
  private String storeName;
  private String accessToken;
  private String storeTheme;
  private Boolean storeEdit;
  private Long storeid;
  private SimpleSetStoreRequest.StoreProperties storeProperties;

  private static final String JSONOBJECT_ERROR = "Couldn't build store_properties json";

  public AccessTokenRequestBodyAdapter(BaseBody baseBody, BaseBodyDecorator decorator,
      String accessToken, String storeName, String storeTheme) {
    this.baseBody = baseBody;
    this.decorator = decorator;
    this.storeName = storeName;
    this.storeTheme = storeTheme;
    this.accessToken = accessToken;
  }

  public AccessTokenRequestBodyAdapter(BaseBody baseBody, BaseBodyDecorator decorator,
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

  @Override public String getAccessToken() {
    return accessToken;
  }

  @Override public void setAccessToken(String accessToken) {
    getAccessToken();
  }

  public HashMapNotNull<String, RequestBody> get() {
    decorator.decorate(baseBody, accessToken);
    HashMapNotNull<String, RequestBody> body = new HashMapNotNull<>();

    if (storeEdit) {
      body.put("store_id", createBodyPartFromLong(storeid));
      try {
        body.put("store_properties", createBodyPartFromString(new ObjectMapper().writeValueAsString(storeProperties)));
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    } else {
      body.put("store_name", createBodyPartFromString(storeName));
      if (storeTheme.length() > 0) {
        try {
          body.put("store_properties",
              createBodyPartFromString(new JSONObject().put("theme", storeTheme).toString()));
        } catch (JSONException e) {
          Logger.e(AccessTokenRequestBodyAdapter.class.getSimpleName(), JSONOBJECT_ERROR, e);
        }
      }
    }
    body.put("access_token", createBodyPartFromString(accessToken));

    return body;
  }

  private RequestBody createBodyPartFromString(String string) {
    if (string == null) {
      string = "";
    }
    return RequestBody.create(MediaType.parse("multipart/form-data"), string);
  }

  private RequestBody createBodyPartFromLong(Long longValue) {
    if (longValue == null) {
      longValue = Long.valueOf(0);
    }
    return RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(longValue));
  }
}
