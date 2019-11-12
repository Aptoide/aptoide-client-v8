package cm.aptoide.pt.dataprovider.ws.v7;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.HashMap;
import java.util.Map;

public class QueryStringMapper {

  public Map<String, String> map(BaseBody body, boolean shouldSendAptoideUID,
      Map<String, String> queryMap) {
    put("aptoide_md5sum", body.getAptoideMd5sum(), queryMap);
    put("aptoide_package", body.getAptoidePackage(), queryMap);
    put("aptoide_vercode", body.getAptoideVercode(), queryMap);
    put("cdn", body.getCdn(), queryMap);
    put("lang", body.getLang(), queryMap);
    put("mature", body.isMature(), queryMap);
    put("q", body.getQ(), queryMap);
    put("refresh", body.isRefresh(), queryMap);
    if (shouldSendAptoideUID) {
      put("aptoide_uid", body.getAptoideId(), queryMap);
    }
    put("country", body.getCountry(), queryMap);
    if (body.getAccessToken() != null && !body.getAccessToken()
        .equals("")) {
      put("access_token", body.getAccessToken(), queryMap);
    }

    if (body instanceof BaseBodyWithAlphaBetaKey) {
      put("not_apk_tags", ((BaseBodyWithAlphaBetaKey) body).getNotApkTags(), queryMap);
    }

    if (body instanceof BaseBodyWithApp) {
      put("store_user", ((BaseBodyWithApp) body).getStoreUser(), queryMap);
      put("store_pass_sha1", ((BaseBodyWithApp) body).getStorePassSha1(), queryMap);
    }
    return queryMap;
  }

  public Map<String, String> map(ListAppsRequest.Body body, boolean shouldEnableAab) {
    Map<String, String> data = new HashMap<>();
    map(body, false, data);
    put("aab", shouldEnableAab, data);
    put("not_apk_tags", body.getNotApkTags(), data);
    put("store_user", body.getStoreUser(), data);
    put("store_pass_sha1", body.getStorePassSha1(), data);
    put("limit", body.getLimit(), data);
    put("offset", body.getOffset(), data);
    put("group_id", body.getGroupId(), data);
    put("store_id", body.getStoreId(), data);
    if (body.getSort() != null) {
      put("sort", body.getSort()
          .name(), data);
    }
    return data;
  }

  public Map<String, String> map(GetAppRequest.Body body, boolean shouldEnableAppBundles) {
    Map<String, String> data = new HashMap<>();
    map(body, false, data);
    put("aab", shouldEnableAppBundles, data);
    put("app_id", body.getAppId(), data);
    put("nodes", getNodesAsString(body), data);
    put("package_name", body.getPackageName(), data);
    put("package_uname", body.getUname(), data);
    put("apk_md5sum", body.getMd5(), data);
    put("store_name", body.getStoreName(), data);
    return data;
  }

  private void put(String key, Object value, Map<String, String> data) {
    if (value != null) {
      data.put(key, String.valueOf(value));
    }
  }

  private String getNodesAsString(GetAppRequest.Body body) {
    String nodes = null;
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
      objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
      nodes = objectMapper.writeValueAsString(body.getNodes());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return nodes;
  }
}