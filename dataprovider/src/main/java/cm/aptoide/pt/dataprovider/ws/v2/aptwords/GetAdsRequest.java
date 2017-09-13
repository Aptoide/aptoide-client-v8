/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v2.aptwords;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;

import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.dataprovider.util.referrer.ReferrerUtils;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Lombok;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 08-06-2016.
 */

@EqualsAndHashCode(callSuper = true) public class GetAdsRequest
        extends V7<GetAdsResponse, GetAdsRequest.Body> {

  public enum Location {
    homepage("native-aptoide:homepage"), appview("native-aptoide:appview"), middleappview(
            "native-aptoide:middleappview"), search("native-aptoide:search"), secondinstall(
            "native-aptoide:secondinstall"), secondtry("native-aptoide:secondtry"), aptoidesdk(
            "sdk-aptoide:generic"), firstinstall("native-aptoide:first-install"), notification(
            "native-aptoide:notifications");
    private final String value;

    Location(String value) {
      this.value = value;
    }

    @Override public String toString() {
      return value;
    }
  }

  private GetAdsRequest(String baseHost, Body body){
    super(body, baseHost);
  }

  private GetAdsRequest(OkHttpClient httpClient, Converter.Factory converterFactory,
                        String baseHost, GetAdsRequest.Body body) {
    super(body, httpClient, converterFactory, baseHost);
  }


  public static GetAdsRequest ofHomepage(String accessToken, String aptoideClientUUID,
                                         boolean googlePlayServicesAvailable, String oemid,
                                         boolean mature, int numberOfColumns) {

    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    // TODO: 09-06-2016 neuro limit based on max colums
    return new GetAdsRequest(BASE_HOST, (GetAdsRequest.Body)decorator.decorate(
            new Body(googlePlayServicesAvailable, oemid, mature,
                    Type.ADS.getPerLineCount() * numberOfColumns, Location.homepage), accessToken));
  }

  public static GetAdsRequest ofHomepageMore(String accessToken, String aptoideClientUUID,
                                             boolean googlePlayServicesAvailable, String oemid,
                                             boolean mature) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    // TODO: 09-06-2016 neuro limit based on max colums
    return new GetAdsRequest(BASE_HOST, (GetAdsRequest.Body)decorator.decorate(
            new Body(googlePlayServicesAvailable, oemid, mature,
                    50, Location.homepage), accessToken));
  }

  public static GetAdsRequest ofAppviewOrganic(String accessToken, String packageName,
                                               String storeName, String aptoideClientUUID,
                                               boolean googlePlayServicesAvailable, String oemid,
                                               boolean mature) {

    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    return new GetAdsRequest(BASE_HOST, (GetAdsRequest.Body)decorator.decorate(
            new Body(googlePlayServicesAvailable, oemid, mature,
                    1, Location.appview, packageName, storeName), accessToken));
  }

  public static GetAdsRequest ofFirstInstallOrganic(String accessToken, String packageName,
                                                    String storeName, String aptoideClientUUID,
                                                    boolean googlePlayServicesAvailable,
                                                    String oemid, boolean mature) {

    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    return new GetAdsRequest(BASE_HOST, (GetAdsRequest.Body)decorator.decorate(
            new Body(googlePlayServicesAvailable, oemid, mature,
                    1, Location.firstinstall, packageName, storeName), accessToken));
  }

  public static GetAdsRequest ofAppviewSuggested(String accessToken, List<String> keywords,
                                                 String aptoideClientUUID,
                                                 boolean googlePlayServicesAvailable,
                                                 String excludedPackage, String oemid,
                                                 boolean mature) {

    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    return new GetAdsRequest(BASE_HOST, (GetAdsRequest.Body)decorator.decorate(
            new Body(googlePlayServicesAvailable, oemid, mature,
                    3, Location.middleappview, excludedPackage, keywords), accessToken));
  }

  public static GetAdsRequest ofSearch(String accessToken, String query, String aptoideClientUUID,
                                       boolean googlePlayServicesAvailable, String oemid,
                                       boolean mature) {

    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    return new GetAdsRequest(BASE_HOST, (GetAdsRequest.Body)decorator.decorate(
            new Body(googlePlayServicesAvailable, oemid, mature,
                    1, Location.search, query), accessToken));

  }

  public static GetAdsRequest ofSecondInstall(String accessToken, String packageName,
                                              String aptoideClientUUID,
                                              boolean googlePlayServicesAvailable, String oemid,
                                              boolean mature) {


    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    return new GetAdsRequest(BASE_HOST, (GetAdsRequest.Body)decorator.decorate(
            new Body(googlePlayServicesAvailable, oemid, mature,
                    1, packageName, Location.secondinstall), accessToken));
  }

  public static GetAdsRequest ofSecondTry(String accessToken, String packageName,
                                          String aptoideClientUUID,
                                          boolean googlePlayServicesAvailable,
                                          String oemid, boolean mature) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    return new GetAdsRequest(BASE_HOST, (GetAdsRequest.Body)decorator.decorate(
            new Body(googlePlayServicesAvailable, oemid, mature,
                    1, packageName, Location.secondtry), accessToken));
  }

  public static GetAdsRequest ofFirstInstall(String accessToken, String aptoideClientUUID,
                                                       boolean googlePlayServicesAvailable,
                                                       String oemid, int numberOfAds,
                                                       boolean mature, String excludedPackages){

    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    return new GetAdsRequest(BASE_HOST, (GetAdsRequest.Body)decorator.decorate(
            new Body(excludedPackages, googlePlayServicesAvailable, oemid, mature,
                    numberOfAds, Location.firstinstall), accessToken));

  }

  public static GetAdsRequest ofNotification(String accessToken, String aptoideClientUUID,
                                                       boolean googlePlayServicesAvailable,
                                                       String oemid, int numberOfAds,
                                                       boolean mature, String excludedPackages) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    return new GetAdsRequest(BASE_HOST, (GetAdsRequest.Body)decorator.decorate(
            new Body(excludedPackages, googlePlayServicesAvailable, oemid, mature,
                    numberOfAds, Location.notification), accessToken));
  }


  @Override
  protected Observable<GetAdsResponse> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getAds(body, bypassCache);
  }

  @Data @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody{

    @JsonProperty("gms") @Getter private boolean googlePlayServicesAvailable;
    @JsonProperty("not_package_names") @Getter private List<String> excludedPackage;
    @Getter private String location;
    @Getter private List<String> keywords;
    @Getter private Integer limit;
    @JsonProperty("package_names") @Getter private List<String> packageName;
    @Getter private String repo;
    @JsonProperty("not_network_names") @Getter private List<String> excludedNetworks;

    public Body(boolean googlePlayServicesAvailable, String oemid, boolean mature) {
      super();
      this.googlePlayServicesAvailable = googlePlayServicesAvailable;
      this.setOem_id(oemid);
      this.setMature(mature);
    }
    public Body(boolean googlePlayServicesAvailable, String oemid, boolean mature,
                Integer limit, Location location){
      super();
      this.googlePlayServicesAvailable = googlePlayServicesAvailable;
      this.setOem_id(oemid);
      this.setMature(mature);
      this.limit = limit;
      this.location = location.toString();
    }

    public Body(boolean googlePlayServicesAvailable, String oemid, boolean mature,
                 Integer limit, Location location, String packageName, String repo){
      super();
      this.googlePlayServicesAvailable = googlePlayServicesAvailable;
      this.setOem_id(oemid);
      this.setMature(mature);
      this.limit = limit;
      this.location = location.toString();
      this.packageName = new ArrayList<>(Arrays.asList(packageName.split(",")));
      this.repo = repo;

      if (ReferrerUtils.excludedNetworks.containsKey(packageName)) {
        List<String> networks = new ArrayList<>();
        networks.add(String.valueOf(ReferrerUtils.excludedNetworks.get(packageName)));
        this.excludedNetworks = networks;
      }
    }

    public Body(boolean googlePlayServicesAvailable, String oemid, boolean mature,
                Integer limit, String packageName, Location location){
      super();
      this.googlePlayServicesAvailable = googlePlayServicesAvailable;
      this.setOem_id(oemid);
      this.setMature(mature);
      this.limit = limit;
      this.location = location.toString();
      this.packageName = new ArrayList<>(Arrays.asList(packageName.split(",")));

      if (ReferrerUtils.excludedNetworks.containsKey(packageName)) {
        List<String> networks = new ArrayList<>();
        networks.add(String.valueOf(ReferrerUtils.excludedNetworks.get(packageName)));
        this.excludedNetworks = networks;
      }
    }

    public Body(boolean googlePlayServicesAvailable, String oemid, boolean mature,
                Integer limit, Location location, String keyword){
      super();
      this.googlePlayServicesAvailable = googlePlayServicesAvailable;
      this.setOem_id(oemid);
      this.setMature(mature);
      this.limit = limit;
      this.location = location.toString();
      this.keywords = new ArrayList<>(Arrays.asList(keyword.split(",")));
    }

    public Body(boolean googlePlayServicesAvailable, String oemid, boolean mature,
                Integer limit, Location location, String excludedPackage, List<String> keywords){
      super();
      this.googlePlayServicesAvailable = googlePlayServicesAvailable;
      this.setOem_id(oemid);
      this.setMature(mature);
      this.limit = limit;
      this.location = location.toString();
      this.excludedPackage = new ArrayList<>(Arrays.asList(excludedPackage.split(",")));
      this.keywords = keywords;
    }

    public Body(String excludedPackage, boolean googlePlayServicesAvailable, String oem_id,
                boolean mature, Integer limit, Location location){
      super();
      this.googlePlayServicesAvailable = googlePlayServicesAvailable;
      this.setOem_id(oem_id);
      this.setMature(mature);
      this.limit = limit;
      this.location = location.toString();
      this.excludedPackage = new ArrayList<>(Arrays.asList(excludedPackage.split(",")));
    }
  }

}



