package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.AccessTokenBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.listapp.File;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.Application;
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

  public AccessTokenRequestBodyAdapter(BaseBody baseBody, BaseBodyDecorator decorator, String accessToken, String storeName, String storeTheme) {
    this.baseBody = baseBody;
    this.decorator = decorator;
    this.storeName = storeName;
    this.storeTheme = storeTheme;
    this.accessToken = accessToken;
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


    body.put("store_name", createBodyPartFromString(storeName));
    try {
      body.put("store_properties", createBodyPartFromString(new JSONObject().put("theme", "red").toString()));
    } catch (JSONException e) {
      Logger.e(AccessTokenRequestBodyAdapter.class.getSimpleName(), "Couldn't build store_properties json", e);
    }
    body.put("store_theme", createBodyPartFromString(storeTheme));
    body.put("access_token", createBodyPartFromString(accessToken));
    /*body.put("aptoide_uid", createBodyPartFromString(baseBody.getAptoideId()));
    body.put("aptoide_vercode", createBodyPartFromString(String.valueOf(baseBody.getAptoideVercode())));

    body.put("cdn", createBodyPartFromString(baseBody.getCdn()));
    body.put("lang", createBodyPartFromString(baseBody.getLang()));
    body.put("q", createBodyPartFromString(baseBody.getQ()));
    body.put("country", createBodyPartFromString(baseBody.getCountry()));

    body.put("mature", createBodyPartFromString(String.valueOf(baseBody.isMature())));*/

    return body;
  }

  private RequestBody createBodyPartFromString(String string) {
    if (string == null) {
      string = "";
    }
    return RequestBody.create(MediaType.parse("multipart/form-data"), string);
  }


}
