package cm.aptoide.pt.dataprovider;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import rx.Observable;

/**
 * Created by danielchen on 24/07/17.
 */

public class AdsOptimizer {


    private static final String EXCLUDED_PACKAGE_KEY = "excPck";

    /**
     * This method checks if the ads returned in the GetAdsResponse are valid.
     * To be valid, its required that at least more than half of them are NOT installed already
     *
     * @param ads - list of ads from GetAdsResponse
     * @param excludedPackages - list of excluded packages, that include the ones in the original
     *                         request and saved in the sharedPreferences of the device
     * @param numberOfAdsToShow - original number of ads asked by the request
     * @return true if it's valid, false if not
     */
    private static boolean checkIfAdsListIsValid(List<GetAdsResponse.Ad> ads,
                                                 List<String> excludedPackages,
                                                 Integer numberOfAdsToShow){

        boolean valid = true;
        if(excludedPackages == null){
            excludedPackages = new ArrayList<>();
        }
        List<GetAdsResponse.Ad> newAdsList = new ArrayList<>();
        for(GetAdsResponse.Ad ad : ads) {
            if (AptoideUtils.SystemU.isAppInstalled(ad.getData().getApp().getPackageName())) {
                excludedPackages.add(ad.getData().getApp().getPackageName());
            } else {
                newAdsList.add(ad);
            }
        }
        if(newAdsList.size() < ads.size() / 2){
            valid = false;
        }
        else{
            //Return only the number of ads that are supposed to be shown
            if(numberOfAdsToShow <= newAdsList.size()) {
                newAdsList.subList(numberOfAdsToShow, newAdsList.size()).clear();
                ads.clear();
                ads.addAll(newAdsList);
            }
        }
        addToSharedPreferences(excludedPackages);
        return valid;
    }

    /**
     * This methods is used to diminish the amount of already installed aps showed in the ads
     * The requested ads are doubled to allow greater chances of success with one single request
     * The returned ads are always limited by the variable "adsLimit" anyways.
     * @param accessToken access token
     * @param location - location
     * @param refresh - refresh
     * @param adsLimit - number of ads to be shown
     * @param aptoideClientUUID - aptoideClientUUID
     * @param googlePlayServicesAvailable - googlePlayServicesAvailable
     * @param oemid - oemid
     * @param mature - mature
     * @param excludedPackages - Cannot be null. List of packages to be excluded. If it's not
     *                         required, use empty list.
     * @param query - query to add to the request. Null if it's not required
     * @param packageName - packageName to add to  the request. Null if it's not required
     * @param repo - repo name to add to the request. Null if it's not required
     * @param keywords - list of keywords to add to the request. Null if it's not required
     * @return Observable response
     */
    public static Observable<GetAdsResponse> optimizeAds(String accessToken,
                                                         GetAdsRequest.Location location,
                                                         boolean refresh, Integer adsLimit,
                                                         String aptoideClientUUID,
                                                         boolean googlePlayServicesAvailable,
                                                         String oemid, boolean mature,
                                                         List<String> excludedPackages,
                                                         List<String> query, String packageName,
                                                         String repo, List<String> keywords) {

        getFromSharedPreferencesInto(excludedPackages);

        //Decrease chances of making a second request, does not influence the number of
        //ads that are to be shown.
        final Integer requestAds = adsLimit*2;

        if(excludedPackages == null){
            throw new IllegalArgumentException("Excluded Packages list cannot be null");
        }
        GetAdsRequest request;

        //if(query == null){
        //    request = GetAdsRequest.of(accessToken, location, requestAds, aptoideClientUUID,
        //            googlePlayServicesAvailable, oemid, mature);
//
        //}else {
        request = GetAdsRequest.of(accessToken, query, location, requestAds, aptoideClientUUID,
                    googlePlayServicesAvailable, oemid,
                    AptoideUtils.StringU.commaSeparatedValues(excludedPackages), mature);
        //}

        if(!TextUtils.isEmpty(packageName)){
            request.getBody().setPackageName(new ArrayList<>(Arrays.asList(packageName.split(","))));
        }
        if(!TextUtils.isEmpty(repo)){
            request.getBody().setRepo(repo);
        }

        if(keywords != null){
            request.getBody().setKeywords(keywords);
        }

        Observable<GetAdsResponse> response = request.observe(refresh);

        return response.flatMap(ads -> {
            if(!checkIfAdsListIsValid(ads.getDataList().getList(), excludedPackages, adsLimit)){
                 return optimizeAds(accessToken, location, refresh, adsLimit, aptoideClientUUID,
                            googlePlayServicesAvailable, oemid, mature, excludedPackages, query,
                            packageName, repo, keywords);
            }
            return Observable.just(ads);
        });
    }

    /**
     * This method allows new package's name to be added to the sharedPreferences of the device.
     * @param excludedPackages - list of packages to add
     */
    private static void addToSharedPreferences(List<String> excludedPackages){
        Set<String> set = new HashSet<>();
        set.addAll(excludedPackages);
        SharedPreferences sp  =
                PreferenceManager.getDefaultSharedPreferences(Application.getContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(EXCLUDED_PACKAGE_KEY, set);
        editor.apply();
    }

    /**
     * This method allows the use of the packages saved in the sharedPreferences of the
     * device, in order to increase the number of excludedPackages used if required.
     * If a previously installed ad was uninstalled, it is removed from the
     * sharedPreferences.
     * @param excludedPackages - list in which the saved package's names are to be added.
     */
    private static void getFromSharedPreferencesInto(List<String> excludedPackages){
        SharedPreferences sp  =
                PreferenceManager.getDefaultSharedPreferences(Application.getContext());
        Set<String> savedExcPck = sp.getStringSet(EXCLUDED_PACKAGE_KEY, null);

        Set<String> set = new HashSet<>();
        set.addAll(excludedPackages);
        if(savedExcPck != null) {
            removeFromSharedPreferenceIfNeeded(savedExcPck);
            set.addAll(savedExcPck);
        }
        excludedPackages.clear();
        excludedPackages.addAll(set);
    }

    /**
     * This method is used to remove previously installed ads, that were uninstalled in the
     * meanwhile.
     * @param savedExcPcks - set of packages to be excluded
     */
    private static void removeFromSharedPreferenceIfNeeded(Set<String> savedExcPcks){
        for(Iterator<String> iterator = savedExcPcks.iterator(); iterator.hasNext(); ){
            String saved = iterator.next();
            if(!AptoideUtils.SystemU.isAppInstalled(saved)){
                iterator.remove();
            }
        }
        SharedPreferences sp  =
                PreferenceManager.getDefaultSharedPreferences(Application.getContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(EXCLUDED_PACKAGE_KEY);
        editor.putStringSet(EXCLUDED_PACKAGE_KEY, savedExcPcks);
        editor.apply();
    }
}
