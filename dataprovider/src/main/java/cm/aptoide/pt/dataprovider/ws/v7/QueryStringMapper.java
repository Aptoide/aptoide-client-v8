package cm.aptoide.pt.dataprovider.ws.v7;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.HashMap;
import java.util.Map;

public class QueryStringMapper {

  public Map<String, String> map(Map<String, String> queryMap, BaseBody body,
      boolean shouldSendAptoideUID) {
    queryMap.put("aptoide_md5sum", body.getAptoideMd5sum());
    queryMap.put("aptoide_package", body.getAptoidePackage());
    queryMap.put("aptoide_vercode", String.valueOf(body.getAptoideVercode()));
    queryMap.put("cdn", body.getCdn());
    queryMap.put("lang", body.getLang());
    queryMap.put("mature", String.valueOf(body.isMature()));
    queryMap.put("q", body.getQ());
    queryMap.put("refresh", String.valueOf(body.isRefresh()));
    if (shouldSendAptoideUID) {
      queryMap.put("aptoide_uid", body.getAptoideId());
    }
    if (body.getCountry() != null) {
      queryMap.put("country", body.getCountry());
    }
    if (body.getAccessToken() != null && !body.getAccessToken()
        .equals("")) {
      queryMap.put("access_token", body.getAccessToken());
    }

    if (body instanceof BaseBodyWithAlphaBetaKey) {
      String notApkTags = ((BaseBodyWithAlphaBetaKey) body).getNotApkTags();
      if (notApkTags != null) {
        queryMap.put("not_apk_tags", notApkTags);
      }
    }

    if (body instanceof BaseBodyWithApp) {
      String storeUser = ((BaseBodyWithApp) body).getStoreUser();
      if (storeUser != null) {
        queryMap.put("store_user", storeUser);
      }
      String storePassSha1 = ((BaseBodyWithApp) body).getStorePassSha1();
      if (storePassSha1 != null) {
        queryMap.put("store_pass_sha1", storePassSha1);
      }
    }
    return queryMap;
  }

  public Map<String, String> map(ListAppsRequest.Body body, boolean shouldEnableAab) {
    Map<String, String> data = new HashMap<>();
    map(data, body, false);
    data.put("aab", String.valueOf(shouldEnableAab));
    data.put("not_apk_tags", body.getNotApkTags());
    String storeUser = body.getStoreUser();
    if (storeUser != null) {
      data.put("store_user", storeUser);
    }

    String storePassSha1 = body.getStorePassSha1();
    if (storePassSha1 != null) {
      data.put("store_pass_sha1", storePassSha1);
    }

    if (body.getLimit() != null) {
      data.put("limit", String.valueOf(body.getLimit()));
    }

    data.put("offset", String.valueOf(body.getOffset()));

    if (body.getGroupId() != null) {
      data.put("group_id", String.valueOf(body.getGroupId()));
    }
    if (body.getSort() != null) {
      data.put("sort", body.getSort()
          .name());
    }
    if (body.getStoreId() != null) {
      data.put("store_id", String.valueOf(body.getStoreId()));
    }
    return data;
  }

  public Map<String, String> map(GetAppRequest.Body body, boolean shouldEnableAppBundles) {
    Map<String, String> data = new HashMap<>();
    map(data, body, false);

    data.put("aab", String.valueOf(shouldEnableAppBundles));

    Long appId = body.getAppId();
    if (appId != null) {
      data.put("app_id", String.valueOf(appId));
    }

    String nodes = getNodesAsString(body);
    if (nodes != null) {
      data.put("nodes", nodes);
    }

    if (body.getPackageName() != null) {
      data.put("package_name", body.getPackageName());
    }

    if (body.getUname() != null) {
      data.put("package_uname", body.getUname());
    }

    if (body.getMd5() != null) {
      data.put("apk_md5sum", body.getMd5());
    }

    if (body.getStoreName() != null) {
      data.put("store_name", body.getStoreName());
    }
    return data;
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